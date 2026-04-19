package com.taskmanager.api.service;

import com.taskmanager.api.exception.InvalidSubtaskIndexException;
import com.taskmanager.api.exception.TaskNotFoundException;
import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import com.taskmanager.api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public Mono<Task> createTask(Task task) {
        if (task.getStatus() == null){
            task.setStatus(TaskStatus.TODO);
        }
        return taskRepository.save(task);
    }

    @Override
    public Flux<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Mono<Task> getTaskById(String id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(id)));
    }

    @Override
    public Flux<Task> getTasksByStatus(TaskStatus status) {
            return taskRepository.findByStatus(status);
    }

    @Override
    public Mono<Task> updateTask(String id, Task updatedTask) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(id)))
                .flatMap(existingTask -> {
                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setStatus(updatedTask.getStatus());
                    existingTask.setSubTasks(updatedTask.getSubTasks());
                    return taskRepository.save(existingTask);
                });
    }

    @Override
    public Mono<Void> deleteTask(String id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(id)))
                .flatMap(task -> taskRepository.deleteById(task.getId()));
    }

    @Override
    public Mono<Void> deleteSubTask(String id, int subtaskIndex) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(id)))
                .flatMap(task -> {
                    if (subtaskIndex < 0 || subtaskIndex >= task.getSubTasks().size()) {
                        return Mono.error(new InvalidSubtaskIndexException(subtaskIndex));
                    }
                    ArrayList<Task> subTasks = new ArrayList<>(task.getSubTasks());
                    subTasks.remove(subtaskIndex);
                    task.setSubTasks(subTasks);
                    return taskRepository.save(task).then();
                });
    }
}
