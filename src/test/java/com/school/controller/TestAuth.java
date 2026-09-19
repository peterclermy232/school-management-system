package com.school.controller;

import com.school.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

/**
 * Builds a real {@link UserDetailsImpl} principal for MockMvc requests, instead of the default
 * {@code @WithMockUser} principal, because several endpoints evaluate
 * {@code authentication.principal.id} in their {@code @PreAuthorize} expressions.
 */
final class TestAuth {
    private TestAuth() {}

    static RequestPostProcessor asUser(Long id, String username, String... roles) {
        List<GrantedAuthority> authorities = List.of(roles).stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        UserDetailsImpl principal = new UserDetailsImpl(id, username, username + "@example.com", "hash", authorities);
        return authentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
