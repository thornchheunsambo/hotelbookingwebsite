package com.example.hotelbookingwebsite.service;

import com.example.hotelbookingwebsite.dto.request.CreateCommentRequest;
import com.example.hotelbookingwebsite.dto.request.UpdateCommentRequest;
import com.example.hotelbookingwebsite.dto.response.CommentResponse;
import com.example.hotelbookingwebsite.entity.Comment;
import com.example.hotelbookingwebsite.entity.Room;
import com.example.hotelbookingwebsite.entity.User;
import com.example.hotelbookingwebsite.exception.BadRequestException;
import com.example.hotelbookingwebsite.exception.ResourceNotFoundException;
import com.example.hotelbookingwebsite.repository.CommentRepository;
import com.example.hotelbookingwebsite.repository.RoomRepository;
import com.example.hotelbookingwebsite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(String username, CreateCommentRequest request) {
        if (request.getRating() != null && (request.getRating() < 1 || request.getRating() > 5)) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new BadRequestException("Comment content cannot be empty");
        }

        User user = findUserByUsername(username);
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", request.getRoomId()));

        Comment comment = Comment.builder()
                .user(user)
                .room(room)
                .content(request.getContent())
                .rating(request.getRating() != null ? request.getRating() : 5)
                .build();

        return toResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> getCommentsByRoom(Long roomId) {
        roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId));

        return commentRepository.findByRoomIdOrderByCreatedAtDesc(roomId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<CommentResponse> getMyComments(String username) {
        User user = findUserByUsername(username);
        return commentRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse updateComment(String username, Long commentId,
                                         UpdateCommentRequest request, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!isAdmin && !comment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only edit your own comments");
        }

        if (request.getContent() != null && !request.getContent().isBlank()) {
            comment.setContent(request.getContent());
        }
        if (request.getRating() != null) {
            if (request.getRating() < 1 || request.getRating() > 5) {
                throw new BadRequestException("Rating must be between 1 and 5");
            }
            comment.setRating(request.getRating());
        }

        return toResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(String username, Long commentId, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!isAdmin && !comment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    // ─── Admin ───────────────────────────────────────────────

    public List<CommentResponse> getAllComments() {
        return commentRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // ─── Helpers ────────────────────────────────────────────

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private CommentResponse toResponse(Comment c) {
        return CommentResponse.builder()
                .id(c.getId())
                .userId(c.getUser().getId())
                .username(c.getUser().getUsername())
                .fullName(c.getUser().getFullName())
                .roomId(c.getRoom().getId())
                .roomName(c.getRoom().getName())
                .content(c.getContent())
                .rating(c.getRating())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}