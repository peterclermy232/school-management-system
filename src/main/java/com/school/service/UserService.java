package com.school.service;

import com.school.dto.UserDTO;
import com.school.entity.ERole;
import com.school.entity.Role;
import com.school.entity.User;
import com.school.repository.RoleRepository;
import com.school.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // --- Generic admin user/role management (backs UserController) ---

    public List<UserDTO> listUsers(String roleFilter) {
        return userRepository.findAll().stream()
                .filter(user -> roleFilter == null || hasRole(user, roleFilter))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserDTO(Long id) {
        return userRepository.findById(id).map(this::convertToDTO);
    }

    /**
     * Creates a plain staff User (not a Student/Teacher/Parent subtype) with the given roles —
     * used to provision accountant/principal/deputy-principal (and additional admin) accounts,
     * which don't need a dedicated entity of their own.
     */
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        String rawPassword = (userDTO.getPassword() == null || userDTO.getPassword().isBlank())
                ? "password123"
                : userDTO.getPassword();
        user.setPassword(encoder.encode(rawPassword));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setRoles(resolveRoles(userDTO.getRoles()));

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateRoles(Long id, Set<String> roleNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setRoles(resolveRoles(roleNames));
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    private boolean hasRole(User user, String roleName) {
        String normalized = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName.toUpperCase();
        return user.getRoles().stream().anyMatch(role -> role.getName().name().equals(normalized));
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(name -> {
                    String normalized = name.startsWith("ROLE_") ? name : "ROLE_" + name.toUpperCase();
                    ERole eRole;
                    try {
                        eRole = ERole.valueOf(normalized);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Error: Role is not found: " + name);
                    }
                    return roleRepository.findByName(eRole)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found: " + name));
                })
                .collect(Collectors.toSet());
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name().replaceFirst("^ROLE_", ""))
                .collect(Collectors.toSet()));
        return dto;
    }
}
