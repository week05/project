package com.example.intermediate.service;

import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.domain.Category;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  private final TokenProvider tokenProvider;

  // 게시글 작성
  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = Post.builder()
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .member(member)
        .category(requestDto.getCategory())
        .build();

    postRepository.save(post);

    return ResponseDto.success(
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .author(post.getMember().getNickname())
            .category(post.getCategory())
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  // 게시글 조회
  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getNickname())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }

    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .commentResponseDtoList(commentResponseDtoList)
                    .author(post.getMember().getNickname())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  // 마이페이지 : 작성한 게시글 조회 (일반적인 게시글 조회가 아님)
  @Transactional(readOnly = true)
  public ResponseDto<?> getMyPost(UserDetailsImpl userDetails) {

    // 유저 ID로 게시글 존재 확인
    // 해당 유저가 작성한 게시글이 여러개일 수 있으니 List로 받아준다.
    List<Post> posts = existMyPost(userDetails.getMember().getId());

    // 받아온 작성 게시글이 없으면 예외 처리
    if (posts.isEmpty()) {
      return ResponseDto.fail("NOT_FOUND", "해당 유저가 작성한 게시글이 존재하지 않습니다.");
    }

    // 최종적으로 작성한 게시글들과 게시글에 존재하는 댓글들 전부 담는 리스트
    List<PostResponseDto> PostResponseDtoList = new ArrayList<>();
    // 게시글에 존재하는 모든 댓글들의 정보를 최정적으로 한번에 담는 리스트
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    // 받아온 게시글 리스트의 각 게시글마다 정보를 받아옴
    for (Post post : posts) {

      // 작성한 각 게시글마다 존재하는 댓글들을 저장
      List<Comment> commentList = commentRepository.findAllByPost(post);

      // 댓글이 존재하면 이곳으로 진입
      if(!commentList.isEmpty()){

        for (Comment comment : commentList) {
          // 저장된 각 댓글들의 정보를 CommentResponseDto에 넣고 commentResponseDtoList 에 stacking
          commentResponseDtoList.add(
                  CommentResponseDto.builder()
                          .id(comment.getId())
                          .author(comment.getMember().getNickname())
                          .content(comment.getContent())
                          .createdAt(comment.getCreatedAt())
                          .modifiedAt(comment.getModifiedAt())
                          .build()
          );

          // 게시글 정보와 모든 댓글들의 정보가 담겨있는 commentResponseDtoList 를 저장
          PostResponseDtoList.add(
                  PostResponseDto.builder()
                          .id(post.getId())
                          .title(post.getTitle())
                          .content(post.getContent())
                          .commentResponseDtoList(commentResponseDtoList)
                          .author(post.getMember().getNickname())
                          .createdAt(post.getCreatedAt())
                          .modifiedAt(post.getModifiedAt())
                          .build()
          );

          System.out.println("게시글 댓글 전부 존재하는 list : " + PostResponseDtoList);

        }
      }else{
        // 해당 게시글에 댓글이 존재하지 않으면 없는 상태인 정보와 게시글 정보를 저장
        PostResponseDtoList.add(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .commentResponseDtoList(commentResponseDtoList)
                        .author(post.getMember().getNickname())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
      }
    }

    // 출력
    return ResponseDto.success(PostResponseDtoList);
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {
    return ResponseDto.success(postRepository.findAllByOrderByModifiedAtDesc());
  }

  @Transactional
  public ResponseDto<Post> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    post.update(requestDto);
    return ResponseDto.success(post);
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }


    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  //카테고리 번호로 게시글 불러오기
  @Transactional
  public ResponseDto<?> categoryAllGet(Long id){
    System.out.println("id" + id);
    Category cos=Category.findById(id);
    System.out.println("카테고리 명 : "+cos);
    System.out.println("디비 데이타"+postRepository.findByCategory(cos));
    return ResponseDto.success(postRepository.findByCategory(cos));
  }

  // 마이페이지 게시글 리스트
  @Transactional(readOnly = true)
  public List<Post> existMyPost(Long id) {
    // 게시글이 여러개를 작성했을 수도 있기에 List로 받음
    List<Post> Postlist = postRepository.findAllByMember_Id(id);
    return Postlist;
  }

  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

}
