package com.gbsw.board.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "사용자명은 필수항목입니다.")
    @Size(min = 3, max = 20, message = "사용자명은 3자 이상 20자 이하로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수항목입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 필수항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}