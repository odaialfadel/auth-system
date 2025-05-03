package com.odai.auth.service;

import com.odai.auth.exception.UserAlreadyExistsException;
import com.odai.auth.exception.UserNotFoundException;
import com.odai.auth.keycloak.KeycloakService;
import com.odai.auth.model.User;
import com.odai.auth.repository.UserRepository;
import com.odai.auth.shared.dto.user.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    @Transactional
    @Override
    public User registerNewUser(String email, String firstName, String lastName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        UUID keycloakId = UUID.fromString(keycloakService.RegisterNewUser(email, firstName, lastName));

        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    @Override
    public User getUserByKeycloakId(UUID keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        keycloakService.deleteUser(user.getKeycloakId().toString());
        userRepository.delete(user);
    }

    @Override
    public UserDto getUserProfile(Jwt jwt) {

        String keycloakId = jwt.getSubject();
        List<String> roles = jwt.getClaimAsStringList("realm_access.roles");

        User user = userRepository.findByKeycloakId(UUID.fromString(keycloakId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return UserDto.builder()
                .id(user.getId())
                .keycloakId(user.getKeycloakId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(user.getIsActive())
                .roles(roles)
                .build();
    }
}
