package com.school.controller;

import com.school.util.JwtUtils;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.dto.LoginRequest;
import com.school.dto.SignupRequest;
import com.school.dto.JwtResponse;
import com.school.dto.MessageResponse;
import com.school.entity.ERole;
import com.school.entity.Parent;
import com.school.entity.Role;
import com.school.entity.User;
import com.school.repository.ParentRepository;
import com.school.repository.RoleRepository;
import com.school.repository.StudentRepository;
import com.school.repository.UserRepository;

import com.school.security.UserDetailsImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ParentRepository parentRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.debug("Login attempt for username: {}", loginRequest.getUsername());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            logger.info("User '{}' authenticated successfully", userDetails.getUsername());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));

        } catch (Exception e) {
            logger.warn("Authentication failed for username '{}': {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(401)
                    .body(new MessageResponse("Invalid username or password"));
        }
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Public self-signup may only grant STUDENT or PARENT. Admin/teacher/staff accounts are
        // provisioned separately (by an authenticated admin, via StudentService/TeacherService/
        // UserService) so this endpoint can't be used to self-escalate to a privileged role.
        Set<String> strRoles = signUpRequest.getRole();
        boolean isParent = strRoles != null && strRoles.contains("parent");

        // A parent needs to be persisted as a Parent (not a bare User) to participate in the
        // JOINED inheritance hierarchy and be linkable to their children via ParentRepository.
        User user = isParent ? new Parent() : new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setAddress(signUpRequest.getAddress());

        Role assignedRole = roleRepository.findByName(isParent ? ERole.ROLE_PARENT : ERole.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRoles(Set.of(assignedRole));

        if (isParent) {
            Parent parent = (Parent) user;
            if (signUpRequest.getChildStudentId() != null && !signUpRequest.getChildStudentId().isBlank()) {
                studentRepository.findByStudentId(signUpRequest.getChildStudentId())
                        .ifPresent(student -> parent.getChildren().add(student));
            }
            parentRepository.save(parent);
        } else {
            userRepository.save(user);
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}