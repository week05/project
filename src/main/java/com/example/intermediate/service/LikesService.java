package com.example.intermediate.service;

import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Likes;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.repository.LikesRepository;
import com.example.intermediate.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;
    private final PostService postService;
    private final PostRepository postRepository;

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

    // 내가 좋아요한 게시글들 조회
    public ResponseDto<?> LikesPost(UserDetailsImpl userDetails){

        List<Likes> likelist = likesRepository.findAllByMember(userDetails.getMember());

        if(likelist.isEmpty()){
            ResponseDto.fail("NOT_FOUND", "좋아요한 게시글이 없습니다.");
        }

        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        for(Likes like : likelist){
            Post post = postRepository.findById(like.getPost().getId()).orElseThrow(
                    () -> new NullPointerException("좋아요한 게시글이 아닙니다.")
            );

            postResponseDtos.add(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .author(post.getMember().getEmailid())
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(postResponseDtos);

    }
}
