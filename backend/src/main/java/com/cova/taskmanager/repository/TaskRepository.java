package com.cova.taskmanager.repository;

import com.cova.taskmanager.entity.Task;
import com.cova.taskmanager.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    @Query("""
            select t from Task t
            where t.user.id = :userId
            and (:status is null or t.status = :status)
            and (:search is null or lower(t.title) like lower(concat('%', :search, '%')))
            order by t.createdAt desc
            """)
    List<Task> search(@Param("userId") Long userId,
                       @Param("status") TaskStatus status,
                       @Param("search") String search);
}
