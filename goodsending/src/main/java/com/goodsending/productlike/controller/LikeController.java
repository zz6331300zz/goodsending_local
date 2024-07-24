package com.goodsending.productlike.controller;

import com.goodsending.product.dto.response.ProductCreateResponseDto;
import com.goodsending.productlike.dto.LikeRequestDto;
import com.goodsending.productlike.dto.LikeResponseDto;
import com.goodsending.productlike.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

  @Operation(summary = "찜하기 토글 기능", description = "멤버Id, 상품Id, 클릭값 입력하면 찜하기 등록,취소 된다.")
  @PostMapping("/like")
  public ResponseEntity<LikeResponseDto> toggleLike(@RequestParam(name = "memberId") Long memberId,
      @RequestBody @Valid LikeRequestDto requestDto) {
    return likeService.toggleLike(memberId, requestDto);
  }

}
