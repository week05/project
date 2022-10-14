package com.example.intermediate.service;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Likes;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;
    private final PostService postService;
    public ResponseDto<?> liksesUp (Long postId , UserDetailsImpl userDetails) {
        System.out.println(postId);
        Member member = userDetails.getMember();
        Post post = postService.isPresentPost(postId);
        System.out.println("asdasds" + post);
        //라이크 디비에서 맴버아이디와 포스트아이디가 존재하는지 확인
        Optional<Likes> likes1 = likesRepository.findByMemberAndPost(member, post);
        System.out.println(likes1);
        if (likes1.isPresent()) {
            likesRepository.deleteById(likes1.get().getId());
            return ResponseDto.success(false);
        } else {
            Likes likes = Likes.builder()
                    .post(post)
                    .member(member)
                    .build();
            likesRepository.save(likes);
            return ResponseDto.success(true);
        }
    }
}
