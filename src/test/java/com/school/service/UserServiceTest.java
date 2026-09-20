package com.school.service;

import com.school.dto.UserDTO;
import com.school.entity.ERole;
import com.school.entity.Role;
import com.school.entity.User;
import com.school.repository.RoleRepository;
import com.school.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_delegatesToRepository() {
        User user = new User("jdoe", "jdoe@example.com", "hash", "Jane", "Doe");
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.getAllUsers();

        assertThat(result).containsExactly(user);
    }

    @Test
    void getUserByUsername_notFound_returnsEmpty() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThat(userService.getUserByUsername("ghost")).isEmpty();
    }

    @Test
    void saveUser_delegatesToRepository() {
        User user = new User("jdoe", "jdoe@example.com", "hash", "Jane", "Doe");
        when(userRepository.save(user)).thenReturn(user);

        assertThat(userService.saveUser(user)).isEqualTo(user);
    }

    @Test
    void deleteUser_delegatesToRepository() {
        userService.deleteUser(3L);

        verify(userRepository).deleteById(3L);
    }

    @Test
    void existsByUsername_delegatesToRepository() {
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThat(userService.existsByUsername("jdoe")).isTrue();
    }

    @Test
    void existsByEmail_delegatesToRepository() {
        when(userRepository.existsByEmail("jdoe@example.com")).thenReturn(false);

        assertThat(userService.existsByEmail("jdoe@example.com")).isFalse();
    }

    private UserDTO accountantDTO() {
        UserDTO dto = new UserDTO();
        dto.setUsername("accountant1");
        dto.setEmail("accountant1@example.com");
        dto.setFirstName("Amy");
        dto.setLastName("Ng");
        dto.setRoles(Set.of("ACCOUNTANT"));
        return dto;
    }

    @Test
    void createUser_encodesPasswordAndAssignsRoles() {
        when(roleRepository.findByName(ERole.ROLE_ACCOUNTANT)).thenReturn(Optional.of(new Role(ERole.ROLE_ACCOUNTANT)));
        when(encoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO created = userService.createUser(accountantDTO());

        assertThat(created.getUsername()).isEqualTo("accountant1");
        assertThat(created.getRoles()).containsExactly("ACCOUNTANT");
    }

    @Test
    void createUser_unknownRole_throws() {
        UserDTO dto = accountantDTO();
        dto.setRoles(Set.of("NOT_A_ROLE"));

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role is not found");
    }

    @Test
    void updateRoles_userNotFound_throws() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateRoles(404L, Set.of("ADMIN")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateRoles_replacesExistingRoles() {
        User user = new User("jdoe", "jdoe@example.com", "hash", "Jane", "Doe");
        user.setRoles(Set.of(new Role(ERole.ROLE_TEACHER)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(ERole.ROLE_DEPUTY_PRINCIPAL))
                .thenReturn(Optional.of(new Role(ERole.ROLE_DEPUTY_PRINCIPAL)));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO updated = userService.updateRoles(1L, Set.of("DEPUTY_PRINCIPAL"));

        assertThat(updated.getRoles()).containsExactly("DEPUTY_PRINCIPAL");
    }

    @Test
    void listUsers_filtersByRole() {
        User accountant = new User("accountant1", "a@example.com", "hash", "Amy", "Ng");
        accountant.setRoles(Set.of(new Role(ERole.ROLE_ACCOUNTANT)));

        User teacher = new User("teacher1", "t@example.com", "hash", "Tom", "Lee");
        teacher.setRoles(Set.of(new Role(ERole.ROLE_TEACHER)));

        when(userRepository.findAll()).thenReturn(List.of(accountant, teacher));

        List<UserDTO> result = userService.listUsers("ACCOUNTANT");

        assertThat(result).extracting(UserDTO::getUsername).containsExactly("accountant1");
    }
}
