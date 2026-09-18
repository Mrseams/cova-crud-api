package com.cova.taskmanager.service;

import com.cova.taskmanager.dto.TaskRequest;
import com.cova.taskmanager.dto.TaskResponse;
import com.cova.taskmanager.entity.Task;
import com.cova.taskmanager.entity.TaskStatus;
import com.cova.taskmanager.entity.User;
import com.cova.taskmanager.exception.TaskNotFoundException;
import com.cova.taskmanager.repository.TaskRepository;
import com.cova.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<TaskResponse> getTasks(String userEmail, TaskStatus status, String search) {
        User user = getUser(userEmail);
        return taskRepository.search(user.getId(), status, search).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public TaskResponse createTask(String userEmail, TaskRequest request) {
        User user = getUser(userEmail);

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .user(user)
                .build();

        taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    public TaskResponse updateTask(String userEmail, Long id, TaskRequest request) {
        User user = getUser(userEmail);
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.status() != null) {
            task.setStatus(request.status());
        }

        taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    public void deleteTask(String userEmail, Long id) {
        User user = getUser(userEmail);
        Task task = taskRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TaskNotFoundException(id));

        taskRepository.delete(task);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("no user found with email " + email));
    }
}
