package com.school.security;

import com.school.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Referenced from {@code @PreAuthorize} SpEL expressions (as {@code @parentAccessService...})
 * to scope a parent's access to only their own linked children.
 */
@Component("parentAccessService")
public class ParentAccessService {

    @Autowired
    private ParentRepository parentRepository;

    public boolean isParentOf(Long parentId, Long studentId) {
        if (parentId == null || studentId == null) {
            return false;
        }
        return parentRepository.existsByIdAndChildren_Id(parentId, studentId);
    }
}
