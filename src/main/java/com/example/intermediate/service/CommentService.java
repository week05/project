package com.example.intermediate.service;

import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.CommentRequestDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  private final TokenProvider tokenProvider;
  private final PostService postService;

  // 댓글 작성
  @Transactional
  public ResponseDto<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {
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

    Post post = postService.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }
      if (requestDto.getResponseTo()==null){
        Comment comment = Comment.builder()
                .member(member)
                .post(post)
                .content(requestDto.getContent())
                .build();
        commentRepository.save(comment);
        return ResponseDto.success(
                CommentResponseDto.builder()
                        .id(comment.getId())
                        .author(comment.getMember().getNickname())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .modifiedAt(comment.getModifiedAt())
                        .build()
        );

      }
      Comment comment = Comment.builder()
              .member(member)
              .post(post)
              .responseTo(requestDto.getResponseTo())
              .content(requestDto.getContent())
              .build();
        commentRepository.save(comment);
        return ResponseDto.success(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getNickname())
                      .content(comment.getContent())
                      .responseTo(requestDto.getResponseTo())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build());
  }

  // 작성한 댓글 조회 (마이페이지)
  @Transactional(readOnly = true)
  public ResponseDto<?> getMyCommentsByPost(UserDetailsImpl userDetails) {

    // 현재 로그인된 유저의 id를 comment DB에 대조하여 적은 comment들 리스트화
    // list화한 이유 : 작성한 comment 가 두개 이상일 수도 있기 때문에
    List<Comment> comments = commentRepository.findAllByMember_id(userDetails.getMember().getId());

    // 작성한 comment 가 없을 경우 실패 처리
    if (null == comments) {
      return ResponseDto.fail("NOT_FOUND", "작성한 게시글이 존재하지 않습니다.");
    }

    // list화 된 comment 들을 최종적으로 담아 출력할 commentlist 생성
    List<CommentResponseDto> commentlist = new ArrayList<>();

    // 작성한 comment들의 각 정보들을 CommentResponseDto 에 저장하여 commentlist에 최종적으로 저장
    for(Comment comment : comments){
      commentlist.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getNickname())
                      .content(comment.getContent())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }

    // 최종적으로 작성한 모든 comment들의 정보가 저장된 commentlist 출력
    return ResponseDto.success(commentlist);


  }

  // 전체 댓글 조회
//게시글 전체
  @Transactional(readOnly = true)
  public ResponseDto<?> getAllCommentsByPost(Long postId) {
    Post post = postService.isPresentPost(postId);
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
    return ResponseDto.success(commentResponseDtoList);
  }

  // 댓글 수정
  @Transactional
  public ResponseDto<?> updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
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

    Post post = postService.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    comment.update(requestDto);
    return ResponseDto.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    );
  }


  // 댓글 삭제
  @Transactional
  public ResponseDto<?> deleteComment(Long id, HttpServletRequest request) {
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

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
    }

    if (comment.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    commentRepository.deleteByIdOrResponseTo(id,id);
    return ResponseDto.success("success");
  }

  @Transactional(readOnly = true)
  public Comment isPresentComment(Long id) {
    Optional<Comment> optionalComment = commentRepository.findById(id);
    return optionalComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
}
