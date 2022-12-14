package com.example.intermediate.controller.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

  // nickname 에서 email 형식의 아이디로 변경, 패턴 변경
  @NotBlank(message = "{nickname.notblank}")
  @Pattern(regexp ="^[a-zA-Z0-9]+@[a-zA-Z]+.[a-z]+${4,12}$", message = "{nickname.option}" )
  private String nickname;

  // entity에 name 속성이 추가됨에 따라 추가
  @NotBlank(message = "{nickname.notblank}")
  private String name;

  @NotBlank(message = "{password.notblank}")
  @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$" , message = "{password.option}" )
  private String password;
  @NotBlank(message = "{password.notblank}")
  @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$" , message = "{password.option}" )
  private String passwordConfirm;
}
