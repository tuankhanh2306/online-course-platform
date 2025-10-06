package edu.uth.online_course_platform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Your email cannot be blank")
    @Email(message = "Your Email is invalid")
    private String email;

    @NotBlank(message = "Your password cannot be blank")
    private String password;
}
