package com.goodsending.productlike.controller;

import com.goodsending.global.security.anotation.MemberId;
import com.goodsending.product.dto.response.ProductlikeCountDto;
import com.goodsending.productlike.dto.LikeRequestDto;
import com.goodsending.productlike.dto.LikeResponseDto;
import com.goodsending.productlike.entity.ProductLikeWithScore;
import com.goodsending.productlike.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class LikeController {

  private final LikeService likeService;

  /**
   * 찜하기 토글 기능
   *
   * 사용자가 상품을 찜하기/취소 합니다
   *
   * @param memberId 회원Id
   * @param requestDto 상품Id, 클릭여부
   * @return 찜하기 된 상품정보 반환
   * @author : zz6331300zz
   */

  @Operation(summary = "찜하기 토글 기능", description = "멤버Id, 상품Id, 클릭값 입력하면 찜하기 등록,취소 된다.")
  @PostMapping("/likes")
  public ResponseEntity<LikeResponseDto> toggleLike(@MemberId Long memberId,
      @RequestBody @Valid LikeRequestDto requestDto) {
    return likeService.toggleLike(memberId, requestDto);
  }

  /**
   *
   * 찜한 상품 목록 조회 기능
   *
   * 찜한 상품의 목록을 페이지 정보와 함께 조회합니다
   *
   * @param memberId 회원 Id
   * @param page 조회할 페이지 번호
   * @param size 한 페이지에 보여줄 상품 개수
   * @param sortBy 정렬 항목(id, title, price...)
   * @param isAsc 오름차순(true:오름차순 false:내림차순)
   * @return 찜한 상품 목록 정보 : number:조회된 페이지 번호(0부터 시작),
   *  content:조회된 상품정보, size: 한 페이지에 보여줄 상품개수,
   *  numberOfElements:전체 상품 개수(회원이 등록한 모든 상품의 개수),
   *  totalPages: 전체 페이지 수
   * @author : zz6331300zz
   */

  @Operation(summary = "찜한 상품 목록 조회 페이징", description = "회원의 찜한 목록이 조회된다.")
  @GetMapping("/likes")
  public ResponseEntity<Page<ProductlikeCountDto>> getLikeProductsPage(
      @MemberId Long memberId,
      @RequestParam("page") int page,
      @RequestParam("size") int size,
      @RequestParam("sortBy") String sortBy,
      @RequestParam("isAsc") boolean isAsc
  ) {
    return ResponseEntity.ok(
        likeService.getLikeProductsPage(memberId, page - 1, size, sortBy, isAsc));
  }

  /**
   * 찜하기 수 Top 5 상품 조회 기능(queryDSL 사용)
   *
   * 미래에 진행되는 찜하기 Top 5 상품을 queryDSL을 사용해서 조회합니다
   *
   * @return 정렬된 Top5 찜하기 상품 목록
   * @author : zz6331300zz
   */

  @Operation(summary = "찜하기 수 top5 상품 조회", description = "찜하기 수 top5 상품조회 한다.")
  @GetMapping("/likes/top5")
  public ResponseEntity<List<ProductlikeCountDto>> getTop5LikeProduct(
  ) {
    LocalDateTime dateTime = LocalDateTime.now();
    return ResponseEntity.ok(
        likeService.getTop5LikeProduct(dateTime));
  }

  /**
   * 찜하기 상품 등록 기능(Redis 사용)
   *
   * 상품 찜을 Redis을 사용해서 등록합니다
   *
   * @param memberId 회원 Id
   * @param requestDto 상품Id, 클릭여부
   * @return 찜한 상품의 등록정보(상태코드)
   * @author : zz6331300zz
   */

  @Operation(summary = "찜하기 수 top5 상품 등록 redis", description = "찜하기 수 top5 상품등록 한다. redis")
  @PostMapping("/likes/redis")
  public ResponseEntity<LikeResponseDto> create(@MemberId Long memberId,
      @RequestBody @Valid LikeRequestDto requestDto) {
    return likeService.toggleLikeRedis(memberId, requestDto);
  }

  /**
   * 찜하기 수 Top 5 상품 조회 기능(Redis 사용)
   *
   * 미래에 진행되는 찜하기 Top 5 상품을 Redis을 사용해서 조회합니다
   * @return 정렬된 Top5 찜하기 상품 목록
   * @author : zz6331300zz
   */

  @Operation(summary = "찜하기 수 top5 상품 조회 redis", description = "찜하기 수 top5 상품조회 한다. redis")
  @GetMapping("/likes/redis")
  public ResponseEntity<List<ProductLikeWithScore>> read() {
    return ResponseEntity.ok(likeService.readTop5LikeProduct());
  }
}
