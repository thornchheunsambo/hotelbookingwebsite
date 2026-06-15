package com.example.hotelbookingwebsite.repository;

import com.example.hotelbookingwebsite.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByRoomIdOrderByCreatedAtDesc(Long roomId);

    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
}