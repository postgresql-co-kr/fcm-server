package com.ecobridge.fcm.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtTokenUtil jwtTokenUtil;
//
//    @Autowired
//    private CustomUserDetailsService userDetailsService;
//
//    @Autowired
//    private UsersEntityRepository usersEntityRepository;
//
//    @Autowired
//    private UserRolesEntityRepository userRolesEntityRepository;
//
//    @PostMapping("/signup")
//    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
//        if (usersEntityRepository.existsByUsername(signupRequest.getUsername())) {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
//        }
//
//        User user = new User(signupRequest.getUsername(),
//                passwordEncoder().encode(signupRequest.getPassword()),
//                signupRequest.getEmail(),
//                signupRequest.getFullName());
//
//        Set<String> strRoles = signupRequest.getRole();
//        Set<UserRolesEntity> roles = new HashSet<>();
//
//        if (strRoles == null) {
//            UserRolesEntity userRole = userRolesEntityRepository.findByName(RoleName.ROLE_USER.name())
//                                                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roles.add(userRole);
//        } else {
//            strRoles.forEach(role -> {
//                switch (role) {
//                    case "admin":
//                        UserRolesEntity adminRole = userRolesEntityRepository.findByName(RoleName.ROLE_ADMIN)
//                                                                             .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(adminRole);
//                        break;
//                    default:
//                        UserRolesEntity userRole = userRolesEntityRepository.findByName(RoleName.ROLE_USER)
//                                                                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(userRole);
//                }
//            });
//        }
//
//        user.setRoles(roles);
//        usersEntityRepository.save(user);
//
//        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
//    }
//
//    @PostMapping("/signin")
//    public ResponseEntity<?> signin(@RequestBody SigninRequest signinRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        UserDetails userDetails = userDetailsService.loadUserByUsername(signinRequest.getUsername());
//
//        String token = jwtTokenUtil.generateToken(userDetails);
//        String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
//
//        return ResponseEntity.ok(new JwtTokenResponse(token, refreshToken));
//    }
//
//    @PostMapping("/refresh")
//    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
//        if (jwtTokenUtil.canRefresh(refreshTokenRequest.getRefreshToken())) {
//            String token = jwtTokenUtil.refreshToken(refreshTokenRequest.getRefreshToken());
//
//            return ResponseEntity.ok(new JwtTokenResponse(token, refreshTokenRequest.getRefreshToken()));
//        } else {
//            return ResponseEntity.badRequest().body(new MessageResponse("Error: Refresh token is invalid or expired."));
//        }
//    }
//
//
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout() {
//        // Logout logic here
//        return ResponseEntity.ok(new MessageResponse("Logout successfully!"));
//    }
}
