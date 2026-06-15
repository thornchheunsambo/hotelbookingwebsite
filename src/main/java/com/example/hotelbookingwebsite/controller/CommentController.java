package com.example.hotelbookingwebsite.controller;

import com.example.hotelbookingwebsite.dto.request.CreateCommentRequest;
import com.example.hotelbookingwebsite.dto.request.UpdateCommentRequest;
import com.example.hotelbookingwebsite.dto.response.CommentResponse;
import com.example.hotelbookingwebsite.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Room comments / reviews endpoints")
public class CommentController {

    private final CommentService commentService;

    // ─── Public ────────────────────────────────────────────

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get all comments for a room")
    public ResponseEntity<List<CommentResponse>> getCommentsByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(commentService.getCommentsByRoom(roomId));
    }

    // ─── Authenticated user ──────────────────────────────────

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add a comment/review to a room")
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(userDetails.getUsername(), request));
    }

    @GetMapping("/my")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my comments")
    public ResponseEntity<List<CommentResponse>> getMyComments(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.getMyComments(userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update my comment (or any comment if Admin)")
    public ResponseEntity<CommentResponse> updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentRequest request) {

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(
                commentService.updateComment(userDetails.getUsername(), id, request, isAdmin));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete my comment (or any comment if Admin)")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        commentService.deleteComment(userDetails.getUsername(), id, isAdmin);
        return ResponseEntity.noContent().build();
    }

    // ─── Admin ───────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all comments (Admin only)")
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }
}