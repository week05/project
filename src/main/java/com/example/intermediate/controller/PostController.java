package com.example.intermediate.controller;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.PostService;
import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
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
  @PostMapping(value = "/auth/post")
  public ResponseDto<?> createPost(@RequestBody PostRequestDto requestDto,
      HttpServletRequest request) {
    return postService.createPost(requestDto, request);
  }

  @GetMapping(value = "/post/{id}")
  public ResponseDto<?> getPost(@PathVariable Long id) {
    return postService.getPost(id);
  }

  @GetMapping(value = "/post")
  public ResponseDto<?> getAllPosts() {
    return postService.getAllPost();
  }

  @PutMapping(value = "/auth/post/{id}")
  public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
      HttpServletRequest request) {
    return postService.updatePost(id, postRequestDto, request);
  }

  @DeleteMapping(value = "/auth/post/{id}")
  public ResponseDto<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }

}
