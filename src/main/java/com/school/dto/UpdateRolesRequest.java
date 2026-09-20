package com.school.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public class UpdateRolesRequest {
    // Role names without the "ROLE_" prefix, e.g. "ACCOUNTANT", "PRINCIPAL".
    @NotEmpty
    private Set<String> roles;

    public UpdateRolesRequest() {}

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
