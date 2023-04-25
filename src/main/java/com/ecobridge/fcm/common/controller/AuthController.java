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
import com.ecobridge.fcm.common.dto.MessageResponse;
import com.ecobridge.fcm.common.dto.SigninDto;
import com.ecobridge.fcm.common.dto.SigninRequest;
import com.ecobridge.fcm.common.mapper.SigninResponseMapper;
import com.ecobridge.fcm.common.service.AuthService;
import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Timed(value = "fcm.auth.controller.timed")
public class AuthController {

    private final FcmPropsConfig fcmPropsConfig;
    private final AuthService authService;

    private final JwtTokenUtil jwtTokenUtil;

    private final SigninResponseMapper signinResponseMapper;

    @GetMapping("/hello")
    public String hello() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Hello Auth";

    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequest signinRequest,
                                    @CookieValue(name = "#{fcmPropsConfig.jwt.refreshCookieName}", required = false) String oldRefreshToken,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        SigninDto signinDto = SigninDto.builder()
                                       .username(signinRequest.getUsername())
                                       .password(signinRequest.getPassword())
                                       .oldRefreshToken(oldRefreshToken)
                                       .userAgent(request.getHeader("user-agent"))
                                       .rememberMe(signinRequest.isRememberMe())
                                       .build();

        authService.signIn(signinDto);  // sign in
        setRefreshCookie(response, signinDto.getRefreshToken(), signinDto.isRememberMe()); // send refresh token
        return ResponseEntity.ok(signinResponseMapper.apply(signinDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "#{fcmPropsConfig.jwt.refreshCookieName}", required = false) String refreshToken,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        SigninDto signinDto = authService.getRefreshToken(refreshToken, request.getHeader("user-agent"));
        String newToken = signinDto.getRefreshToken();
        setRefreshCookie(response, newToken, jwtTokenUtil.isRememberMe(newToken)); // send refresh token
        return ResponseEntity.ok(signinResponseMapper.apply(signinDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "#{fcmPropsConfig.jwt.refreshCookieName}", required = false) String refreshToken,
                                    HttpServletResponse response) {

        if (refreshToken != null) {
            authService.logout(refreshToken);
            // delete cookie
            Cookie cookie = new Cookie(fcmPropsConfig.getJwt().getRefreshCookieName(), null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return ResponseEntity.ok(new MessageResponse("Logout successfully!"));
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken, boolean isRememberMe) {
        ResponseCookie.ResponseCookieBuilder responseCookieBuilder =
                ResponseCookie.from(fcmPropsConfig.getJwt().getRefreshCookieName(), refreshToken)
                              .path("/")
                              .httpOnly(true);
        if (isRememberMe) {
            responseCookieBuilder.maxAge(fcmPropsConfig.getJwt().getRefreshExpirationTime());
        } else {
            responseCookieBuilder.maxAge(fcmPropsConfig.getJwt().getExpirationTime());
        }

        if (fcmPropsConfig.isCookieHttps()) {
            responseCookieBuilder.secure(true)
                                 .sameSite("None");
        }

        response.addHeader("Set-Cookie", responseCookieBuilder.build().toString());
    }
}
