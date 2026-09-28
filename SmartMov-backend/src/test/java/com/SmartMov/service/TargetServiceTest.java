package com.SmartMov.service;

import com.SmartMov.entity.LearningSession;
import com.SmartMov.entity.Resource;
import com.SmartMov.entity.Target;
import com.SmartMov.entity.User;
import com.SmartMov.repository.LearningSessionRepository;
import com.SmartMov.repository.ResourceRepository;
import com.SmartMov.repository.TargetRepository;
import com.SmartMov.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetServiceTest {

    @Mock
    private TargetRepository targetRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private LearningSessionRepository learningSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private TargetService targetService;

    @Test
    void deleteTargetRemovesSessionsResourcesFilesAndTarget() throws Exception {
        User user = new User();
        user.setUsername("owner");

        Target target = new Target();
        target.setId(20L);
        target.setUser(user);

        Resource resource = new Resource();
        resource.setId(30L);
        resource.setFilePath("uploads/file.pdf");
        resource.setTarget(target);

        LearningSession session = new LearningSession();
        session.setId(40L);
        session.setTarget(target);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(user));
        when(targetRepository.findByIdAndUser(20L, user)).thenReturn(Optional.of(target));
        when(resourceRepository.findByTargetIdAndTargetUser(20L, user))
                .thenReturn(List.of(resource));
        when(learningSessionRepository.findByTargetIdAndTargetUser(20L, user))
                .thenReturn(List.of(session));

        targetService.deleteTarget(20L, "owner");

        verify(fileStorageService).deleteFile("uploads/file.pdf");
        verify(learningSessionRepository).deleteAll(List.of(session));
        verify(resourceRepository).deleteAll(List.of(resource));
        verify(targetRepository).delete(target);
    }
}
