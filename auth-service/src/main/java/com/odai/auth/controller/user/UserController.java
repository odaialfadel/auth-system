package com.odai.auth.controller.user;

import com.odai.auth.service.auth.UserService;
import com.odai.auth.shared.dto.user.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserInfo(@AuthenticationPrincipal Jwt jwt) {
//        UserDto user = userService.getUserProfile(jwt);
        return ResponseEntity.ok(null);
    }
}
