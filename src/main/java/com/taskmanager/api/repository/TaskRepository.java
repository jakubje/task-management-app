package com.taskmanager.api.repository;

import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskRepository {
    Flux<Task> findAll();

    Mono<Task> findById(String id);

    Flux<Task> findByStatus(TaskStatus status);

    Mono<Task> save(Task task);

    Mono<Void> deleteById(String id);

}
