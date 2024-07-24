package com.goodsending.product.service;

import com.goodsending.global.exception.CustomException;
import com.goodsending.global.exception.ExceptionCode;
import com.goodsending.global.service.S3Uploader;
import com.goodsending.member.entity.Member;
import com.goodsending.member.repository.MemberRepository;
import com.goodsending.product.dto.request.ProductCreateRequestDto;
import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.product.dto.response.ProductImageInfoDto;
import com.goodsending.product.entity.Product;
import com.goodsending.product.entity.ProductImage;
import com.goodsending.product.repository.ProductImageRepository;
import com.goodsending.product.repository.ProductRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductImageRepository productImageRepository;
  private final S3Uploader s3Uploader;
  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public ProductCreateResponseDto createProduct(ProductCreateRequestDto requestDto,
      List<MultipartFile> productImages, LocalDateTime currentTime, Long memberId) {

    // 존재하는 회원인지 판별
    Member member = findMember(memberId);

    // 상품 정보 저장
    Product product = Product.of(requestDto, currentTime, member);
    Product savedProduct = productRepository.save(product);

    // 버킷에 상품 이미지 업로드
    List<String> uploadedFileNames = s3Uploader.uploadProductImageFileList(productImages, "images/products");

    // 업로드 된 상품의 url 저장
    List<ProductImageInfoDto> savedProductImages = new ArrayList<>();
    for (String uploadedFileName : uploadedFileNames) {
      ProductImage productImage = ProductImage.of(product, uploadedFileName);
      productImageRepository.save(productImage);

      ProductImageInfoDto productImageInfoDto = ProductImageInfoDto.from(productImage);
      savedProductImages.add(productImageInfoDto);
    }

    return ProductCreateResponseDto.of(savedProduct, savedProductImages);
  }

  private Member findMember(Long memberId) {
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> CustomException.from(ExceptionCode.USER_NOT_FOUND));
    return member;
  }
}
