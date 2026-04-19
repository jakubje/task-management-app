package com.taskmanager.api.exception;

public class InvalidSubtaskIndexException extends RuntimeException {
    public InvalidSubtaskIndexException(int index) {
        super("Invalid subtask index: " + index);
    }
}
