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

import com.ecobridge.fcm.common.dto.UserDto;
import com.ecobridge.fcm.common.entity.UserRolesEntity;
import com.ecobridge.fcm.common.entity.UsersEntity;
import com.ecobridge.fcm.common.enums.RoleName;
import com.ecobridge.fcm.common.repository.UserRolesEntityRepository;
import com.ecobridge.fcm.common.repository.UsersEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersEntityRepository usersEntityRepository;
    private final UserRolesEntityRepository userRolesEntityRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersEntity user = usersEntityRepository.findByUsername(username)
                          .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<UserRolesEntity> roles = userRolesEntityRepository.findByUserId(user.getId())
                                                               .orElse(Collections.EMPTY_LIST);

        Set<RoleName> roleNames = roles.stream().map(role -> role.getRoleName())
                .collect(Collectors.toSet());

        return UserDto.builder().username(user.getUsername())
                .password(user.getPassword())
                .roles(roleNames).build();
    }
}
