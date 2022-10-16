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

        // 현재 로그인된 유저정보로 like DB에서 연관되어 있는 like 정보 리스트업
        // 리스트업한 이유 : 좋아요한 글이 두개 이상일 수도 있기 때문
        List<Likes> likelist = likesRepository.findAllByMember(userDetails.getMember());

        // 조회 가능한 이력이 없다면 실패 처리
        if(likelist.isEmpty()){
            ResponseDto.fail("NOT_FOUND", "좋아요한 게시글이 없습니다.");
        }

        // 조회된 post들을 최종적으로 담아 보여줄 list 생성
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        // 조회된 좋아요한 post들의 각 postid로 DB에서 조회하여 post 불러옴
        for(Likes like : likelist){
            Post post = postRepository.findById(like.getPost().getId()).orElseThrow(
                    () -> new NullPointerException("좋아요한 게시글이 아닙니다.")
            );

            // 불러온 post들을 postResponseDtos 에 저장
            postResponseDtos.add(
                    PostResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .author(post.getMember().getNickname())
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt())
                            .build()
            );
        }

        // postResponseDtos 에 저장되어 최종적으로 좋아요가 되어있는 게시글들 전부 조회
        return ResponseDto.success(postResponseDtos);

    }
}
