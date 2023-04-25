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
package com.ecobridge.fcm.common.service;

import com.ecobridge.fcm.common.config.JwtTokenUtil;
import com.ecobridge.fcm.common.dto.SigninDto;
import com.ecobridge.fcm.common.dto.UserDto;
import com.ecobridge.fcm.common.entity.UserTokensEntity;
import com.ecobridge.fcm.common.entity.UsersEntity;
import com.ecobridge.fcm.common.repository.UserRolesEntityRepository;
import com.ecobridge.fcm.common.repository.UserTokensEntityRepository;
import com.ecobridge.fcm.common.repository.UsersEntityRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    public final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    private final UsersEntityRepository usersEntityRepository;
    private final UserRolesEntityRepository userRolesEntityRepository;
    private final UserTokensEntityRepository userTokensEntityRepository;
    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService customUserDetailsService;


    @Transactional
    public void signIn(@Valid SigninDto signinDto) {

        // 인증 처리 check username/password 실패시 throws
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signinDto.getUsername(),
                        signinDto.getPassword()
                )
        );

        // 토큰 발급 처리
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtTokenUtil.REMEMBER_ME_KEY, signinDto.isRememberMe());
        String accessToken = jwtTokenUtil.generateAccessToken(signinDto.getUsername(), claims);
        String refreshToken = jwtTokenUtil.generateRefreshToken(signinDto.getUsername(), claims);

        UserDto principal = (UserDto) authenticate.getPrincipal();

        if (signinDto.getOldRefreshToken() != null) {
            Optional<UserTokensEntity> userToken = userTokensEntityRepository.findByToken(signinDto.getOldRefreshToken());
            if (userToken.isPresent()) {
                userTokensEntityRepository.delete(userToken.get());
            }
        }

        userTokensEntityRepository.save(
                UserTokensEntity.builder()
                                .userId(principal.getId())
                                .token(refreshToken)
                                .userAgent(signinDto.getUserAgent())
                                .build()
        );

        UsersEntity user = usersEntityRepository.findByUsername(signinDto.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("Username not found")
        );
        user.setLastLoginAt(LocalDateTime.now());
        usersEntityRepository.save(user);

        signinDto.setAccessToken(accessToken);
        signinDto.setRefreshToken(refreshToken);
        signinDto.setRoles(principal.getRoles());
    }

    @Transactional
    public SigninDto getRefreshToken(String oldRefreshToken, String userAgent) {

        if (jwtTokenUtil.canRefresh(oldRefreshToken)) {
            UserTokensEntity userToken = userTokensEntityRepository.findByToken(oldRefreshToken)
                                                                   .orElseThrow(() -> new InvalidCookieException("The refresh token not exists!"));

            String refreshToken = jwtTokenUtil.refreshToken(oldRefreshToken);
            userTokensEntityRepository.delete(userToken);
            userTokensEntityRepository.save(
                    UserTokensEntity.builder()
                                    .userId(userToken.getUserId())
                                    .token(refreshToken)
                                    .userAgent(userAgent)
                                    .build()
            );

            String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
            UserDto userDto = (UserDto) customUserDetailsService.loadUserByUsername(username);
            boolean isRememberMe = jwtTokenUtil.isRememberMe(refreshToken);
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtTokenUtil.REMEMBER_ME_KEY, isRememberMe);
            String accessToken = jwtTokenUtil.generateAccessToken(username, claims);
            return SigninDto.builder()
                            .username(username)
                            .userAgent(userAgent)
                            .rememberMe(isRememberMe)
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .roles(userDto.getRoles())
                            .build();
        } else {
            throw new InvalidCookieException("The refresh token is expired");
        }

    }

    @Transactional
    public void logout(String refreshToken) {
        if (!StringUtils.hasLength(refreshToken)) {
            return;
        }
        UserTokensEntity userToken =
                userTokensEntityRepository.findByToken(refreshToken)
                                          .orElseGet(null);
        if (userToken != null) {
            userTokensEntityRepository.delete(userToken);
        }
        SecurityContextHolder.clearContext();
    }

}


