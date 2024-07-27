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
import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import com.goodsending.product.repository.ProductImageRepository;
import com.goodsending.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Date : 2024. 07. 12.
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
