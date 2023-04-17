package com.ecobridge.fcm.common.mapper;

import com.ecobridge.fcm.common.dto.SigninDto;
import com.ecobridge.fcm.common.dto.SigninResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

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
@Component
public class SigninResponseMapper implements Function<SigninDto, SigninResponse> {
    @Override
    public SigninResponse apply(SigninDto signinDto) {
        return SigninResponse.builder()
                             .rememberMe(signinDto.isRememberMe())
                             .username(signinDto.getUsername())
                             .accessToken(signinDto.getAccessToken())
                             .roles(signinDto.getRoles())
                             .build();
    }
}
