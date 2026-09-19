package com.school.service;

import com.school.entity.User;
import com.school.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

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
}
