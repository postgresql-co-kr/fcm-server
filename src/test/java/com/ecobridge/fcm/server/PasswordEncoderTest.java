package com.ecobridge.fcm.server;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

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
@Slf4j
public class PasswordEncoderTest {
    @Test
    public void encodeTest() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        log.debug(encoder.encode("123456"));
        boolean matches = encoder.matches("1234", "$2a$10$uBDDPY6/N0uuY/YfG7W4YONf0edIgLXdVBXRjqOdPlAFu7E48hoyq");
        assertThat(matches).isTrue();

    }
}
