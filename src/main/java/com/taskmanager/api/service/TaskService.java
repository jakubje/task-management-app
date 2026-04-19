package com.taskmanager.api.service;

import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService {

    Mono<Task> createTask(Task task);

    Flux<Task> getAllTasks();

    Mono<Task> getTaskById(String id);

    Flux<Task> getTasksByStatus(TaskStatus status);

    Mono<Task> updateTask(String id, Task updatedTask);

    Mono<Void> deleteTask(String id);

    Mono<Void> deleteSubTask(String id, int subtaskIndex);

}
