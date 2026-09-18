package com.cova.taskmanager.service;

import com.cova.taskmanager.dto.TaskRequest;
import com.cova.taskmanager.dto.TaskResponse;
import com.cova.taskmanager.entity.Task;
import com.cova.taskmanager.entity.TaskStatus;
import com.cova.taskmanager.entity.User;
import com.cova.taskmanager.exception.TaskNotFoundException;
import com.cova.taskmanager.repository.TaskRepository;
import com.cova.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("jane@example.com").password("hashed").build();
    }

    @Test
    void createTaskDefaultsToTodoWhenNoStatusGiven() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        TaskRequest request = new TaskRequest("buy milk", "2 liters", null);
        TaskResponse response = taskService.createTask(user.getEmail(), request);

        assertThat(response.title()).isEqualTo("buy milk");
    }

    @Test
    void updateTaskThrowsWhenTaskDoesNotBelongToUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUserId(99L, user.getId())).thenReturn(Optional.empty());

        TaskRequest request = new TaskRequest("title", "desc", TaskStatus.DONE);

        assertThatThrownBy(() -> taskService.updateTask(user.getEmail(), 99L, request))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deleteTaskRemovesItWhenOwnedByUser() {
        Task task = Task.builder().id(5L).title("old task").status(TaskStatus.TODO).user(user).build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUserId(5L, user.getId())).thenReturn(Optional.of(task));

        taskService.deleteTask(user.getEmail(), 5L);
    }
}
