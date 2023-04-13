/*
 * Copyright 2023 jinyoonoh@gmail.com (postgresql.co.kr, ecobridge.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecobridge.fcm.common.controller;

import com.ecobridge.fcm.common.config.FcmPropsConfig;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Timed(value = "fcm.auth.controller.timed")
public class AuthController {
    public final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UsersEntityRepository usersEntityRepository;
    private final UserRolesEntityRepository userRolesEntityRepository;
    private final AuthenticationManager authenticationManager;
    private final FcmPropsConfig fcmPropsConfig;

    @GetMapping("/hello")
    public String hello() {
        return "Hello Auth";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {

        if (usersEntityRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                                 .code(HttpStatus.BAD_REQUEST.value() + "01")
                                 .message("Username is already taken!")
                                 .path("/signup").build());
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
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequest signinRequest, HttpServletResponse response) {

        // 인증 처리 check username/password 실패시 throws
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signinRequest.getUsername(),
                        signinRequest.getPassword()
                )
        );

        // 토큰 발급 처리
        Map<String, Object> claims = new HashMap<>();
        claims.put("rememberMe", signinRequest.isRememberMe());

        String refreshToken = jwtTokenUtil.generateRefreshToken(signinRequest.getUsername(), claims);
        Cookie cookie = new Cookie(fcmPropsConfig.getJwt().getRefreshCookieName(), refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(fcmPropsConfig.isCookieHttps()); // https use
        response.addCookie(cookie);

        String accessToken = jwtTokenUtil.generateAccessToken(signinRequest.getUsername(), claims);
        SigninResponse response1 = new SigninResponse();

        return ResponseEntity.ok(SigninResponse.builder()
                                               .rememberMe(signinRequest.isRememberMe())
                                               .username(signinRequest.getUsername())
                                               .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "#{fcmPropsConfig.jwt.refreshCookieName}", required = false) String refreshToken) {
        if (jwtTokenUtil.canRefresh(refreshToken)) {
            String token = jwtTokenUtil.refreshToken(refreshToken);
            return ResponseEntity.ok(
                    JwtTokenResponse.builder()
                                    .token(token)
                                    .build()
            );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ErrorResponse.builder().code(HttpStatus.UNAUTHORIZED.value() + "02")
                                 .message("Refresh token is invalid or expired.")
                                 .path("/refresh")
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(new MessageResponse("Logout successfully!"));
    }
}
