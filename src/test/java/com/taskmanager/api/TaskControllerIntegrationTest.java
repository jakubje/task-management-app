package com.taskmanager.api;


import com.taskmanager.api.controller.TaskController;
import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import com.taskmanager.api.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(TaskController.class)
public class TaskControllerIntegrationTest {


    private static final String BASE_URL = "/api/v1/tasks";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TaskService taskService;

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
    void createTask_shouldReturn201WithCreatedTask() {
        when(taskService.createTask(any(Task.class)))
                .thenReturn(Mono.just(task));

        webTestClient.post()
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(task)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Task.class)
                .isEqualTo(task);
    }

    @Test
    void getAllTasks_shouldReturn200WithAllTasks() {
        when(taskService.getAllTasks())
                .thenReturn(Flux.just(task));

        webTestClient.get()
                .uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(1)
                .contains(task);
    }

    @Test
    void getAllTasks_shouldReturn200WithEmptyList() {
        when(taskService.getAllTasks())
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri(BASE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(0);
    }

    @Test
    void getTasksByStatus_shouldReturn200WithFilteredTasks() {
        when(taskService.getTasksByStatus(TaskStatus.TODO))
                .thenReturn(Flux.just(task));

        webTestClient.get()
                .uri(BASE_URL + "?status=TODO")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(1)
                .contains(task);
    }

    @Test
    void getTaskById_shouldReturn200WithTask() {
        when(taskService.getTaskById(task.getId()))
                .thenReturn(Mono.just(task));

        webTestClient.get()
                .uri(BASE_URL + "/" + task.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .isEqualTo(task);
    }

    @Test
    void getTaskById_shouldReturn404WhenNotFound() {
        when(taskService.getTaskById("nonexistentId"))
                .thenReturn(Mono.error(new com.taskmanager.api.exception.TaskNotFoundException("nonexistentId")));

        webTestClient.get()
                .uri(BASE_URL + "/nonexistentId")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateTask_shouldReturn200WithUpdatedTask() {
        Task updatedTask = Task.builder()
                .id(task.getId())
                .title("Updated title")
                .description("Updated description")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(taskService.updateTask(eq(task.getId()), any(Task.class)))
                .thenReturn(Mono.just(updatedTask));

        webTestClient.put()
                .uri(BASE_URL + "/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedTask)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .isEqualTo(updatedTask);
    }

    @Test
    void updateTask_shouldReturn404WhenNotFound() {
        when(taskService.updateTask(eq("nonexistentId"), any(Task.class)))
                .thenReturn(Mono.error(new com.taskmanager.api.exception.TaskNotFoundException("nonexistentId")));

        webTestClient.put()
                .uri(BASE_URL + "/nonexistentId")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(task)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteTask_shouldReturn204WhenDeleted() {
        when(taskService.deleteTask(task.getId()))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(BASE_URL + "/" + task.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteTask_shouldReturn404WhenNotFound() {
        when(taskService.deleteTask("nonexistentId"))
                .thenReturn(Mono.error(new com.taskmanager.api.exception.TaskNotFoundException("nonexistentId")));

        webTestClient.delete()
                .uri(BASE_URL + "/nonexistentId")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteSubTask_shouldReturn204WhenDeleted() {
        when(taskService.deleteSubTask(taskWithSubtasks.getId(), 0))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(BASE_URL + "/" + taskWithSubtasks.getId() + "/subtasks/0")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteSubTask_shouldReturn404WhenTaskNotFound() {
        when(taskService.deleteSubTask("nonexistentId", 0))
                .thenReturn(Mono.error(new com.taskmanager.api.exception.TaskNotFoundException("nonexistentId")));

        webTestClient.delete()
                .uri(BASE_URL + "/nonexistentId/subtasks/0")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteSubTask_shouldReturn400WhenIndexOutOfBounds() {
        when(taskService.deleteSubTask(taskWithSubtasks.getId(), 99))
                .thenReturn(Mono.error(new com.taskmanager.api.exception.InvalidSubtaskIndexException(99)));

        webTestClient.delete()
                .uri(BASE_URL + "/" + taskWithSubtasks.getId() + "/subtasks/99")
                .exchange()
                .expectStatus().isBadRequest();
    }
}
