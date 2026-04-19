package com.taskmanager.api.controller;

import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import com.taskmanager.api.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Task> createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public Flux<Task> getTasks(@RequestParam(required = false) TaskStatus status) {
        if (status != null) {
            return taskService.getTasksByStatus(status);
        }
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Mono<Task> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public Mono<Task> updateTask(@PathVariable String id, @RequestBody Task task) {
        return taskService.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTask(@PathVariable String id) {
        return taskService.deleteTask(id);
    }

    @DeleteMapping("{id}/subtasks/{subtaskIndex}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteSubTask(@PathVariable String id, @PathVariable int subtaskIndex) {
        return taskService.deleteSubTask(id, subtaskIndex);
    }
}
