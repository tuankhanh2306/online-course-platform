package edu.uth.online_course_platform.services;

import edu.uth.online_course_platform.dto.request.LoginRequest;
import edu.uth.online_course_platform.dto.request.RegisterRequest;
import edu.uth.online_course_platform.dto.response.LoginResponse;
import edu.uth.online_course_platform.dto.response.RegisterResponse;
import edu.uth.online_course_platform.exceptions.AppException;
import edu.uth.online_course_platform.exceptions.ErrorCode;
import edu.uth.online_course_platform.models.User;
import edu.uth.online_course_platform.repositories.UserRepository;
import edu.uth.online_course_platform.until.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    public RegisterResponse register(RegisterRequest registerRequest)
    {
        User user = new User();
        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED);
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setRole(User.UserRole.STUDENT);
        userRepository.save(user);
        return new RegisterResponse(
                user.getFullName(),
                user.getEmail()
        );
    }

    public LoginResponse login(LoginRequest loginRequest) {
        /* If user was found, hand to authenticate with these steps below:
        1. Generate a auth token as an authentication application inside UsernamePasswordAuthenticationToken. If it's successfull, it will create an authentication Object (but not authenticated yet) just for username, password and user's authority
        2. Then the authentication will be passed to authenticate Method of authenticationManager. Until now, username and password from login are authenticated
        3. If authentication failed, authenticationManager will terminate and release exception to failed login
        4. If authenticate successfully, the authentication will be passed into SecurityContextHolder. Until then, any services or logics can get user's authentication from securityContext -> this is the primary benefit of using token that user donnot need anymore login whenever changing service.
        5. However, to do that, we need to create Jwt token after  the first authentication at login.
         */
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        var authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        String token = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setFullName(((UserDetails) authentication.getPrincipal()).getUsername());
        loginResponse.setRole(authentication.getAuthorities().iterator().next().getAuthority());
        return loginResponse;
    }

    public String logout() {
        SecurityContextHolder.clearContext();
        return "Logged out";
    }
}
