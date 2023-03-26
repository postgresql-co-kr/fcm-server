package com.ecobridge.fcm.common.controller;

import com.ecobridge.fcm.common.config.JwtTokenUtil;
import com.ecobridge.fcm.common.dto.*;
import com.ecobridge.fcm.common.entity.UserRolesEntity;
import com.ecobridge.fcm.common.entity.UsersEntity;
import com.ecobridge.fcm.common.enums.RoleName;
import com.ecobridge.fcm.common.repository.UserRolesEntityRepository;
import com.ecobridge.fcm.common.repository.UsersEntityRepository;
import com.ecobridge.fcm.common.service.CustomUserDetailsService;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Timed(value = "fcm.auth.controller.timed")
public class AuthController {
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UsersEntityRepository usersEntityRepository;
    private final UserRolesEntityRepository userRolesEntityRepository;
    public final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")

    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {

        if (usersEntityRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        UsersEntity user = new UsersEntity();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        usersEntityRepository.save(user);
        UserRolesEntity userRoles = new UserRolesEntity();
        userRoles.setRoleName(RoleName.USER);
        userRolesEntityRepository.save(userRoles);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninRequest signinRequest, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signinRequest.getUsername(),
                        signinRequest.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(signinRequest.getUsername());
        String token = jwtTokenUtil.generateAccessToken(userDetails);
        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok(new JwtTokenResponse(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody JwtRefreshTokenRequest refreshTokenRequest) {
        if (jwtTokenUtil.canRefresh(refreshTokenRequest.getRefreshToken())) {
            String token = jwtTokenUtil.refreshToken(refreshTokenRequest.getRefreshToken());
            return ResponseEntity.ok(new JwtTokenResponse(token));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Refresh token is invalid or expired."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {

        return ResponseEntity.ok(new MessageResponse("Logout successfully!"));
    }
}
