package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.service.LikesService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CustomBaseControllerAnnotation
public class LikeController {
    private final LikesService likesService;

    // 좋아요
    @PostMapping("/auth/post/{postid}/like")
    public ResponseDto<?> Likes(@PathVariable Long postid, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likesService.liksesUp(postid, userDetails);
    }

    // 좋아요 한 게시글 조회
    @GetMapping("/auth/post/like")
    public ResponseDto<?> LikesPost(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likesService.LikesPost(userDetails);
    }

}
