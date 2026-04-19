package com.taskmanager.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "tasks")
public class Task {

    @Id
    private String id;

    private String title;

    private String description;

    private TaskStatus status;

    @Builder.Default
    private List<Task> subTasks = new ArrayList<>();

}
