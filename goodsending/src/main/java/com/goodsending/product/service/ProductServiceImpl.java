package com.goodsending.product.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.global.service.S3Uploader;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.dto.response.ProductImageCreateResponseDto;
import com.goodsending.product.dto.response.ProductInfoDto;
import com.goodsending.product.dto.response.ProductSummaryDto;
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

  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final S3Uploader s3Uploader;
  private final MemberRepository memberRepository;

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
    List<String> uploadedFileNames = s3Uploader.uploadProductImageFileList(productImages, "images/products");

    // 업로드 된 상품의 url 저장
    List<ProductImageCreateResponseDto> savedProductImages = new ArrayList<>();
    for (String uploadedFileName : uploadedFileNames) {
      ProductImage productImage = ProductImage.of(product, uploadedFileName);
      productImageRepository.save(productImage);

      ProductImageCreateResponseDto productImageCreateResponseDto = ProductImageCreateResponseDto.from(productImage);
      savedProductImages.add(productImageCreateResponseDto);
    }

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
