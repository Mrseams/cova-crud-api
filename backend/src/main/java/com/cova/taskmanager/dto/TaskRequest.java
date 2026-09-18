package com.cova.taskmanager.dto;

import com.cova.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;

public record TaskRequest(
        @NotBlank String title,
        String description,
        TaskStatus status
) {
}
