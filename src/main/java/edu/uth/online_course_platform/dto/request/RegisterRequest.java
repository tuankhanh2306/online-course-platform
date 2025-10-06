package edu.uth.online_course_platform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email is invalid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "Your name cannot be blank")
    private String fullName;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(min = 10, max = 10,message = "Your phone number must have 10 digits")
    String phoneNumber;
}
