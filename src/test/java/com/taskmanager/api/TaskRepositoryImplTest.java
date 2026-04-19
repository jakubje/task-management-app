package com.taskmanager.api;

import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import com.taskmanager.api.repository.TaskRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskRepositoryImplTest {

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @InjectMocks
    private TaskRepositoryImpl taskRepository;

    private Task task;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id("771283128381238")
                .title("Test task")
                .description("Test description")
                .status(TaskStatus.TODO)
                .build();
    }

    @Test
    void findAll_shouldReturnAllTasks() {
        when(mongoTemplate.findAll(Task.class))
                .thenReturn(Flux.just(task));

        StepVerifier.create(taskRepository.findAll())
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void findMyId_shouldReturnTaskWhenFound() {
        when(mongoTemplate.findOne(any(Query.class), eq(Task.class)))
                .thenReturn(Mono.just(task));

        StepVerifier.create(taskRepository.findById("nonexistentId"))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void findByStatus_shouldReturnMatchingTasks() {
        when(mongoTemplate.find(any(Query.class), eq(Task.class)))
                .thenReturn(Flux.just(task));

        StepVerifier.create(taskRepository.findByStatus(TaskStatus.TODO))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void save_shouldReturnSavedTask() {
        when(mongoTemplate.save(task))
                .thenReturn(Mono.just(task));

        StepVerifier.create(taskRepository.save(task))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void deleteById_shouldCompleteSuccessfully() {
        when(mongoTemplate.remove(any(Query.class), eq(Task.class)))
                .thenReturn(Mono.just(
                        com.mongodb.client.result.DeleteResult.acknowledged(1)
                ));

        StepVerifier.create(taskRepository.deleteById(task.getId()))
                .verifyComplete();
    }

}
