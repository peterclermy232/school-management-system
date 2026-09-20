package com.school.controller;

import com.school.dto.UpdateRolesRequest;
import com.school.dto.UserDTO;
import com.school.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Generic admin user/role management. Student and Teacher accounts have their own dedicated
 * creation flow (StudentController/TeacherController) tied to their domain entities; this
 * exists for the roles that don't need one — accountant, principal, deputy principal, or an
 * additional admin — which are just a {@code Set<Role>} on a plain User.
 */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.listUsers(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserDTO(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        if (userService.existsByUsername(userDTO.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        if (userService.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDTO> updateRoles(@PathVariable Long id, @Valid @RequestBody UpdateRolesRequest request) {
        try {
            UserDTO updatedUser = userService.updateRoles(id, request.getRoles());
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
