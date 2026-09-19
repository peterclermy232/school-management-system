package com.school.util;

import com.school.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    private static final String TEST_SECRET = "dGVzdC1vbmx5LXNlY3JldC1kby1ub3QtdXNlLWluLXByb2Q=";

    @Mock
    private Authentication authentication;

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);
    }

    @Test
    void generateAndParseToken_roundTripsUsername() {
        UserDetailsImpl principal = new UserDetailsImpl(1L, "jdoe", "jdoe@example.com", "hash",
                Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(principal);

        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(token).isNotBlank();
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("jdoe");
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    void validateJwtToken_malformedToken_returnsFalse() {
        assertThat(jwtUtils.validateJwtToken("not-a-real-jwt")).isFalse();
    }

    @Test
    void validateJwtToken_expiredToken_returnsFalse() {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", -1000);
        UserDetailsImpl principal = new UserDetailsImpl(1L, "jdoe", "jdoe@example.com", "hash",
                Collections.emptyList());
        when(authentication.getPrincipal()).thenReturn(principal);

        String expiredToken = jwtUtils.generateJwtToken(authentication);

        assertThat(jwtUtils.validateJwtToken(expiredToken)).isFalse();
    }
}
