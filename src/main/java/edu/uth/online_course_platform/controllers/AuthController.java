package edu.uth.online_course_platform.controllers;

import edu.uth.online_course_platform.dto.request.LoginRequest;
import edu.uth.online_course_platform.dto.request.RegisterRequest;
import edu.uth.online_course_platform.dto.response.ApiResponse;
import edu.uth.online_course_platform.dto.response.LoginResponse;
import edu.uth.online_course_platform.dto.response.RegisterResponse;
import edu.uth.online_course_platform.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        ApiResponse<RegisterResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("success");
        apiResponse.setCode(200);
        apiResponse.setResult(authService.register(registerRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login (@Valid @RequestBody LoginRequest loginRequest) {
        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("success");
        apiResponse.setCode(200);
        apiResponse.setResult(authService.login(loginRequest));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
