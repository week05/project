package com.example.intermediate.controller;

import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.MemberService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CustomBaseControllerAnnotation
public class MemberController {

  private final MemberService memberService;

  // 회원가입
  @PostMapping(value = "/member/signup")
  public ResponseDto<?> signup(@Valid @RequestBody MemberRequestDto requestDto) {

    return memberService.createMember(requestDto);
  }


  // 로그인
  @PostMapping(value = "/member/login")
  public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto,
      HttpServletResponse response
  ) {
    return memberService.login(requestDto, response);
  }


// 로그아웃
  @PostMapping(value = "/auth/member/logout")
  public ResponseDto<?> logout(HttpServletRequest request) {
    return memberService.logout(request);
  }
}
