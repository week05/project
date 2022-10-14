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

  @NotBlank(message = "{nickname.notblank}")
  @Pattern(regexp ="^[a-z]{4,12}$", message = "{nickname.option}" )
  private String nickname;
  @NotBlank(message = "{password.notblank}")
  @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$" , message = "{password.option}" )
  private String password;
  @NotBlank(message = "{password.notblank}")
  @Pattern(regexp ="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,20}$" , message = "{password.option}" )
  private String passwordConfirm;
}
