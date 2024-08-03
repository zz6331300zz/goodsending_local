package com.goodsending.product.service;

import com.goodsending.deposit.entity.Deposit;
import com.goodsending.deposit.repository.DepositRepository;
import com.goodsending.deposit.type.DepositStatus;
import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.global.service.S3Uploader;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.request.ProductUpdateRequestDto;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.dto.response.ProductImageCreateResponseDto;
import com.goodsending.product.dto.response.ProductInfoDto;
import com.goodsending.product.dto.response.ProductSummaryDto;
import com.goodsending.product.dto.response.ProductUpdateResponseDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import com.goodsending.product.repository.ProductImageRepository;
import com.goodsending.product.repository.ProductRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Date : 2024. 07. 23.
 * @Team : GoodsEnding
 * @author : puclpu
 * @Project : goodsending-be :: goodsending
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

  private static String S3_IMAGE_PATH = "image/products";

  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final S3Uploader s3Uploader;
  private final MemberRepository memberRepository;
  private final DepositRepository depositRepository;

  /**
   * 상품 등록
   * @param requestDto
   * @param productImages
   * @param memberId
   * @return 생성된 상품 정보 반환
   * @author : puclpu
   */
  @Override
  @Transactional
  public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto,
      List<MultipartFile> productImages, Long memberId) {

    // 상품 이미지 개수 초과 판별
    int size = productImages.size();
    if (size > 5) {
      throw CustomException.from(ExceptionCode.FILE_COUNT_EXCEEDED);
    }

    // 존재하는 회원인지 판별
    Member member = findMember(memberId);

    // 상품 정보 저장
    Product product = Product.of(requestDto, member);
    Product savedProduct = productRepository.save(product);

    // 버킷에 상품 이미지 업로드
    List<String> uploadedFileNames = s3Uploader.uploadProductImageFileList(productImages, S3_IMAGE_PATH);

    // 업로드 된 상품의 url 저장
    List<ProductImageCreateResponseDto> savedProductImages = new ArrayList<>();
    for (String uploadedFileName : uploadedFileNames) {
      ProductImage productImage = ProductImage.of(product, uploadedFileName);
      productImageRepository.save(productImage);

      ProductImageCreateResponseDto productImageCreateResponseDto = ProductImageCreateResponseDto.from(productImage);
      savedProductImages.add(productImageCreateResponseDto);
    }

    // 보증금 차감
    Integer productPrice = savedProduct.getPrice();
    Integer depositPrice = (int)(productPrice * 0.05); // 보증금은 0.05%를 가져갑니다
    if (depositPrice < 3000) { // 보증금은 최소 3000원
      depositPrice = 3000;
    }
    member.deductCash(depositPrice);

    // 보증금 내역 저장
    Deposit deposit = Deposit.of(savedProduct, member, depositPrice);
    depositRepository.save(deposit);

    return ProductCreateResponseDto.of(savedProduct, savedProductImages);
  }

  /**
   * 선택한 경매 상품 상세 정보 조회
   * @param productId
   * @return 경매 상품 상세 정보 반환
   */
  @Override
  public ProductInfoDto getProduct(Long productId) {

    Product product = findProduct(productId);
    List<ProductImage> productImageList = findProductImageList(product);

    // TODO: 입찰 여부 확인 로직 구현

    return ProductInfoDto.of(product, productImageList);
  }

  /**
   * 경매 상품 목록 조회
   *
   * @param now                 현재 시각
   * @param openProduct         구매 가능한 매물 선택 여부
   * @param closedProduct       마감된 매물 선택 여부
   * @param keyword             검색어
   * @param cursorStartDateTime
   * @param cursorId            사용자에게 응답해준 마지막 데이터의 식별자값
   * @param size                조회할 데이터 개수
   * @return 조회한 경매 상품 목록 반환
   * @author : puclpu
   */
  @Override
  public Slice<ProductSummaryDto> getProductSlice(LocalDateTime now, String openProduct,
      String closedProduct, String keyword, LocalDateTime cursorStartDateTime, Long cursorId, int size) {
    Pageable pageable = PageRequest.of(0, size);
    Slice<ProductSummaryDto> productSummaryDtoSlice = productRepository.findByFiltersAndSort(now, openProduct, closedProduct, keyword, cursorStartDateTime, cursorId, pageable);
    return productSummaryDtoSlice;
  }

  /**
   * 경매 상품 수정
   * @param productId 상품 아이디
   * @param requestDto 상품 수정 요청 정보
   * @param productImages 상품 이미지
   * @param memberId 등록자
   * @param now 현재 시각
   * @return 수정된 상품 정보 반환
   */
  @Override
  @Transactional
  public ProductUpdateResponseDto updateProduct(Long productId, ProductUpdateRequestDto requestDto,
      List<MultipartFile> productImages, Long memberId, LocalDateTime now) {

    // 등록된 상품인지 판별
    Product product = findProduct(productId);

    // 수정을 요청한 사용자와 판매자가 동일한지 판별
    Long writer = product.getMember().getMemberId();
    if (writer != memberId) {
      throw CustomException.from(ExceptionCode.MEMBER_ID_MISMATCH);
    }

    // 입찰자 존재 여부 판별
    if (product.getBiddingCount() > 0) {
      throw CustomException.from(ExceptionCode.BIDDER_ALREADY_EXIST);
    }

    // 경매 마감일이 지났는지 판별
    LocalDateTime maxEndDateTime = product.getMaxEndDateTime();
    LocalDateTime dynamicEndDateTime = product.getDynamicEndDateTime();
    if (maxEndDateTime.isBefore(now) || dynamicEndDateTime != null && dynamicEndDateTime.isBefore(now)) {
      throw CustomException.from(ExceptionCode.AUCTION_ALREADY_CLOSED);
    }

    // 상품 이미지 변경 시 이미지 삭제 후 새로운 이미지 등록
    List<ProductImage> savedProductImages = new ArrayList<>();
    if (productImages.size() > 0) { // 이미지 변동 사항이 있으면
      // 등록된 이미지 모두 삭제
      List<ProductImage> productImageList = findProductImageList(product);
      s3Uploader.deleteProductImageFileList(productImageList);
      productImageRepository.deleteAllInBatch(productImageList);

      // 새 이미지 S3 업로드
      List<String> uploadedFileNames = s3Uploader.uploadProductImageFileList(productImages,
          S3_IMAGE_PATH);

      // 업로드 된 상품의 url 저장
      for (String uploadedFileName : uploadedFileNames) {
        ProductImage productImage = ProductImage.of(product, uploadedFileName);
        savedProductImages.add(productImage);
      }
    }
    productImageRepository.saveAll(savedProductImages);

    // 상품 정보 수정
    product.update(requestDto);

    return ProductUpdateResponseDto.from(product, savedProductImages);
  }

  /**
   * 경매 상품 삭제
   * @param productId 상품 아이디
   * @param memberId 등록자
   * @param now 현재 시각
   */
  @Override
  @Transactional
  public void deleteProduct(Long productId, Long memberId, LocalDateTime now) {
    // 등록된 상품인지 판별
    Product product = findProduct(productId);

    // 수정을 요청한 사용자와 판매자가 동일한지 판별
    Long writer = product.getMember().getMemberId();
    if (writer != memberId) {
      throw CustomException.from(ExceptionCode.MEMBER_ID_MISMATCH);
    }

    // 경매 마감일이 지났는지 판별
    LocalDateTime maxEndDateTime = product.getMaxEndDateTime();
    LocalDateTime dynamicEndDateTime = product.getDynamicEndDateTime();
    if (maxEndDateTime.isBefore(now) || dynamicEndDateTime != null && dynamicEndDateTime.isBefore(now)) {
      throw CustomException.from(ExceptionCode.AUCTION_ALREADY_CLOSED);
    }

    // 입찰자 존재 여부 판별
    if (product.getBiddingCount() > 0) {
      throw CustomException.from(ExceptionCode.BIDDER_ALREADY_EXIST); // 입찰자가 존재하면 삭제 불가
    }

    // 판매자의 보증금 환불
    Member member = findMember(memberId);
    Deposit deposit = depositRepository.findByProduct(product);
    member.addCash(deposit.getPrice());
    deposit.setStatus(DepositStatus.RETURNED);

    // 상품 이미지 삭제
    List<ProductImage> productImageList = findProductImageList(product);
    productImageRepository.deleteAllInBatch(productImageList);

    // 상품 삭제
    productRepository.delete(product);
  }

  private List<ProductImage> findProductImageList(Product product) {
    List<ProductImage> productImageList = productImageRepository.findAllByProduct(product);
    return productImageList;
  }

  private Product findProduct(Long productId) {
    Product product = productRepository.findById(productId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.PRODUCT_NOT_FOUND));
    return product;
  }

  private Member findMember(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.USER_NOT_FOUND));
    return member;
  }
}
