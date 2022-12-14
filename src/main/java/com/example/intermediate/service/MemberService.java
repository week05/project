package com.example.intermediate.service;

import com.example.intermediate.controller.error.ErrorCode;
import com.example.intermediate.controller.response.MemberResponseDto;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.RefreshToken;
import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.controller.request.TokenDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.MemberRepository;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {
    if (null != isPresentMember(requestDto.getNickname())) {
      return ResponseDto.fail(ErrorCode.ALREADY_SAVED_ID.name(),
          ErrorCode.ALREADY_SAVED_ID.getMessage());
    }

    if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
      return ResponseDto.fail(ErrorCode.PASSWORDS_NOT_MATCHED.name(),
          ErrorCode.PASSWORDS_NOT_MATCHED.getMessage());
    }

        Member member = Member.builder()
                .nickname(requestDto.getNickname())
                .name(requestDto.getName())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .build();

        memberRepository.save(member);

        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .name(member.getName())
                        .createdAt(member.getCreatedAt())
                        .modifiedAt(member.getModifiedAt())
                        .build()
        );
    }

    // ?????????
    @Transactional
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = isPresentMember(requestDto.getNickname());
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "???????????? ?????? ??? ????????????.");
        }

        if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail("INVALID_MEMBER", "???????????? ?????? ??? ????????????.");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .name(member.getName())
                        .createdAt(member.getCreatedAt())
                        .modifiedAt(member.getModifiedAt())
                        .build()
        );
    }


    // ????????????
    public ResponseDto<?> logout(HttpServletRequest request) {
//        Verification verification=new Verification();
//        verification.logout(request);
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail("INVALID_TOKEN", "Token??? ???????????? ????????????.");
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "???????????? ?????? ??? ????????????.");
        }
//        Member member = tokenProvider.getMemberFromAuthentication();
        return tokenProvider.deleteRefreshToken(member);
    }

    @Transactional(readOnly = true)
    public Member isPresentMember(String nickname) {
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
        return optionalMember.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }

//?????? ?????? ?????? ??????
//public class Verification{
//      public ResponseDto<Object> logout(HttpServletRequest request){
//          if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
//              return ResponseDto.fail("INVALID_TOKEN", "Token??? ???????????? ????????????.");
//          }
//          Member member = tokenProvider.getMemberFromAuthentication();
//          if (null == member) {
//              return ResponseDto.fail("MEMBER_NOT_FOUND",
//                      "???????????? ?????? ??? ????????????.");
//          }
//          return null;
//      }
//    }
}
