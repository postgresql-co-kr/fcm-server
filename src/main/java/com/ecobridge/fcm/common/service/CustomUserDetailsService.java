package com.ecobridge.fcm.common.service;

import com.ecobridge.fcm.common.dto.UserDto;
import com.ecobridge.fcm.common.entity.UserRolesEntity;
import com.ecobridge.fcm.common.entity.UsersEntity;
import com.ecobridge.fcm.common.enums.RoleName;
import com.ecobridge.fcm.common.repository.UserRolesEntityRepository;
import com.ecobridge.fcm.common.repository.UsersEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersEntityRepository usersEntityRepository;
    private final UserRolesEntityRepository userRolesEntityRepository;

    public CustomUserDetailsService(UsersEntityRepository usersEntityRepository, UserRolesEntityRepository userRolesEntityRepository) {
        this.usersEntityRepository = usersEntityRepository;
        this.userRolesEntityRepository = userRolesEntityRepository;
    }

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
