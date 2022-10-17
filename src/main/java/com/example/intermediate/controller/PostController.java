package com.example.intermediate.controller;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.service.PostService;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CustomBaseControllerAnnotation
public class PostController {

  private final PostService postService;

  @ApiImplicitParams({
          @ApiImplicitParam(
                  name = "Refresh-Token",
                  required = true,
                  dataType = "string",
                  paramType = "header"
          )
  })

  // 게시글 작성
  @PostMapping(value = "/auth/post")
  public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto,
                                   HttpServletRequest request) {
    return postService.createPost(requestDto, request);
  }

  // 게시글 조회
  @GetMapping(value = "/post/{id}")
  public ResponseDto<?> getPost(@PathVariable Long id) {
    return postService.getPost(id);
  }

  // 작성한 게시글 조회 (마이페이지)
  // 조건 : 인증된 정보로 현재 로그인된 유저가 작성한 게시글을 특정하여 조회
  @GetMapping(value = "/auth/post")
  public ResponseDto<?> getUserPost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.getMyPost(userDetails);
  }
  //카테고리 번호로 가져오기
  @GetMapping(value="/post/category/{id}")
  public ResponseDto<?> categoryAll(@PathVariable Long id){
    return postService.categoryAllGet(id);
  }

  // 게시글 전체 조회
  @GetMapping(value = "/post")
  public ResponseDto<?> getAllPosts() {
    return postService.getAllPost();
  }

  // 게시글 수정
  @PutMapping(value = "/auth/post/{id}")
  public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
      HttpServletRequest request) {
    return postService.updatePost(id, postRequestDto, request);
  }

  // 게시글 삭제
  @DeleteMapping(value = "/auth/post/{id}")
  public ResponseDto<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }

}
