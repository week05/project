package com.example.intermediate.controller;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.service.LikesService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CustomBaseControllerAnnotation
public class LikeController {
    private final LikesService likesService;

    @PostMapping("/auth/post/{postid}/like")
    public ResponseDto<?> Likes(@PathVariable Long postid, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return likesService.liksesUp(postid,userDetails);
    }
}
