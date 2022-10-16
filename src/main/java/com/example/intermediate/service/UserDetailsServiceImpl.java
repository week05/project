package com.example.intermediate.service;

import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
    // nickname 이 아닌 emailid 로 변경되었으니 변수명 및 jpa 함수명 변경
    Optional<Member> member = memberRepository.findByNickname(nickname);

    System.out.println("멤버 잘 찾아오니 : " + member);

    return member
            .map(UserDetailsImpl::new)
            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
  }
}
