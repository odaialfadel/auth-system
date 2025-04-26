package com.odai.auth.service;

import com.odai.auth.model.User;
import com.odai.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User registerUser(String email, String firstName, String lastName) {
        String keycloakId = createUserInKeycloak(email, firstName, lastName);

        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    @Override
    public User getUserByKeycloakId(String keycloakId) {
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

    private String createUserInKeycloak(String email, String firstName, String lastName) {
        // TODO: Integrate with Keycloak Admin API and return the keycloakId
        return "dummy-keycloak-id";
    }
}
