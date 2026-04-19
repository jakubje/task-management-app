package com.taskmanager.api;

import com.taskmanager.api.exception.InvalidSubtaskIndexException;
import com.taskmanager.api.exception.TaskNotFoundException;
import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import com.taskmanager.api.repository.TaskRepository;
import com.taskmanager.api.service.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private Task taskWithSubtasks;

    @BeforeEach
    void setUp() {
        task = Task.builder()
            .id("771283128381238")
            .title("Test task")
            .description("Test description")
            .status(TaskStatus.TODO)
            .build();

        Task subTask = Task.builder()
            .title("Sub task")
            .description("Sub task description")
            .status(TaskStatus.TODO)
            .build();

        taskWithSubtasks = Task.builder()
                .id("771283128381239")
                .title("Task with subtasks")
                .description("Test description")
                .status(TaskStatus.TODO)
                .subTasks(new ArrayList<>(List.of(subTask)))
                .build();
    }

    @Test
    void createTask_shouldSetStatusToTodoIfNoneProvided() {
        Task noStatusTask = Task.builder()
                .title("No status task")
                .description("No status description")
                .build();

        when(taskRepository.save(any(Task.class)))
                .thenReturn(Mono.just(noStatusTask));

        StepVerifier.create(taskService.createTask(noStatusTask))
                .expectNextMatches(t -> t.getStatus() == TaskStatus.TODO)
                .verifyComplete();
    }

    @Test
    void createTask_shouldSaveAndReturnTask() {
        when(taskRepository.save(any(Task.class)))
                .thenReturn(Mono.just(task));

        StepVerifier.create(taskService.createTask(task))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void getAllTasks_shouldReturnAllTasks() {
        when(taskRepository.findAll())
                .thenReturn(Flux.just(task));

        StepVerifier.create(taskService.getAllTasks())
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void getTaskById_shouldReturnTaskWhenFound() {
        when(taskRepository.findById(task.getId()))
                .thenReturn(Mono.just(task));

        StepVerifier.create(taskService.getTaskById(task.getId()))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void getTaskById_shouldThrowTaskNotFoundExceptionWhenNotFound () {
        when(taskRepository.findById("nonexistentId"))
                .thenReturn(Mono.empty());

        StepVerifier.create(taskService.getTaskById("nonexistentId"))
                .expectError(TaskNotFoundException.class)
                .verify();
    }

    @Test
    void getTaskByStatus_shouldReturnMatchingTasks () {
        when(taskRepository.findByStatus(TaskStatus.TODO))
                .thenReturn(Flux.just(task));

        StepVerifier.create(taskService.getTasksByStatus(TaskStatus.TODO))
                .expectNext(task)
                .verifyComplete();
    }

    @Test
    void updateTask_shouldUpdateAndReturnTask () {
        Task updatedTask = Task.builder()
            .title("Updated title")
            .description("Updated description")
            .status(TaskStatus.IN_PROGRESS)
            .build();

        when(taskRepository.findById(task.getId()))
                .thenReturn(Mono.just(task));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(Mono.just(updatedTask));

        StepVerifier.create(taskService.updateTask(task.getId(), updatedTask))
                .expectNextMatches(t ->
                    t.getTitle().equals("Updated title") &&
                    t.getStatus() == TaskStatus.IN_PROGRESS)
                .verifyComplete();
    }

    @Test
    void updateTask_shouldThrowTaskNotFoundExceptionWhenNotFound () {
        when(taskRepository.findById("nonexistentId"))
                .thenReturn(Mono.empty());

        StepVerifier.create(taskService.updateTask("nonexistentId", task))
                .expectError(TaskNotFoundException.class)
                .verify();
    }

    @Test
    void deleteTask_shouldDeleteTaskSuccessfully() {
        when(taskRepository.findById(task.getId()))
                .thenReturn(Mono.just(task));
        when(taskRepository.deleteById(task.getId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(taskService.deleteTask(task.getId()))
                .verifyComplete();
    }

    @Test
    void deleteTask_shouldThrowTaskNotFoundExceptionWhenNotFound () {
        when(taskRepository.findById("nonexistentId"))
                .thenReturn(Mono.empty());

        StepVerifier.create(taskService.deleteTask("nonexistentId"))
                .expectError(TaskNotFoundException.class)
                .verify();
    }

    @Test
    void deleteSubTask_shouldRemoveSubtaskAtGivenIndex() {
        when(taskRepository.findById(taskWithSubtasks.getId()))
                .thenReturn(Mono.just(taskWithSubtasks));
        when(taskRepository.save(any(Task.class)))
                .thenReturn(Mono.just(taskWithSubtasks));

        StepVerifier.create(taskService.deleteSubTask(taskWithSubtasks.getId(), 0))
                .verifyComplete();
    }

    @Test
    void deleteSubTask_shouldThrowTaskNotFoundExceptionWhenNotFound() {
        when(taskRepository.findById("nonexistentId"))
                .thenReturn(Mono.empty());

        StepVerifier.create(taskService.deleteTask("nonexistentId"))
                .expectError(TaskNotFoundException.class)
                .verify();
    }

    @Test
    void deleteSubTask_shouldThrowInvalidSubtaskIndexExceptionWhenIndexOutOfBounds() {
        when(taskRepository.findById(taskWithSubtasks.getId()))
                .thenReturn(Mono.just(taskWithSubtasks));

        StepVerifier.create(taskService.deleteSubTask(taskWithSubtasks.getId(), 111))
                .expectError(InvalidSubtaskIndexException.class)
                .verify();
    }

}
