package com.school.security;

import com.school.repository.ParentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParentAccessServiceTest {

    @Mock
    private ParentRepository parentRepository;

    @InjectMocks
    private ParentAccessService parentAccessService;

    @Test
    void isParentOf_linkedChild_returnsTrue() {
        when(parentRepository.existsByIdAndChildren_Id(1L, 2L)).thenReturn(true);

        assertThat(parentAccessService.isParentOf(1L, 2L)).isTrue();
    }

    @Test
    void isParentOf_unlinkedChild_returnsFalse() {
        when(parentRepository.existsByIdAndChildren_Id(1L, 99L)).thenReturn(false);

        assertThat(parentAccessService.isParentOf(1L, 99L)).isFalse();
    }

    @Test
    void isParentOf_nullIds_returnsFalseWithoutQuerying() {
        assertThat(parentAccessService.isParentOf(null, 2L)).isFalse();
        assertThat(parentAccessService.isParentOf(1L, null)).isFalse();
    }
}
