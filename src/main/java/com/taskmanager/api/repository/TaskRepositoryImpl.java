package com.taskmanager.api.repository;

import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<Task> findAll() {
        return mongoTemplate.findAll(Task.class);
    }

    @Override
    public Mono<Task> findById(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Task.class);
    }

    @Override
    public Flux<Task> findByStatus(TaskStatus status) {
        Query query = Query.query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, Task.class);
    }

    @Override
    public Mono<Task> save(Task task) {
        return mongoTemplate.save(task);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.remove(query, Task.class).then();
    }
}
