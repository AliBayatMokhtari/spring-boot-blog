package com.example.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.dto.CommentRequest;
import com.example.blog.dto.CommentResponse;
import com.example.blog.dto.UserSummary;
import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(Long postId, Long userId, CommentRequest request) {
        log.info("Creating comment for post ID: {} by user ID: {}", postId, userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);

                    return new RuntimeException("Post not found");
                });

        User author = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);

                    return new RuntimeException("User not found");
                });

        Comment comment = new Comment();

        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setUser(author);

        Comment savedComment = commentRepository.save(comment);

        log.info("Comment created with ID: {}", savedComment.getId());

        return mapToCommentResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPost(Long postId) {
        log.debug("Fetching comments for post ID: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);

                    return new RuntimeException("Post not found");
                });

        return commentRepository.findByPostOrderByCreatedAtDesc(post)
                .stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentId) {
        log.debug("Fetching comment with ID: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Comment not found with ID: {}", commentId);

                    return new RuntimeException("Comment not found");
                });

        return mapToCommentResponse(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, Long userId, CommentRequest request) {
        log.info("Updating comment ID: {} by user ID: {}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Comment not found with ID: {}", commentId);

                    return new RuntimeException("Comment not found");
                });

        if (!comment.getUser().getId().equals(userId)) {
            log.warn("User {} tried to update comment {} they don't own", userId, commentId);

            throw new RuntimeException("You don't have permission to update this comment");
        }

        comment.setContent(request.getContent());

        Comment updatedComment = commentRepository.save(comment);

        log.info("Comment updated successfully: {}", updatedComment.getId());

        return mapToCommentResponse(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        log.info("Deleting comment ID: {} by user ID: {}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Comment not found with ID: {}", commentId);

                    return new RuntimeException("Comment not found");
                });

        if (!comment.getUser().getId().equals(userId)) {
            log.warn("User {} tried to delete comment {} they don't own", userId, commentId);

            throw new RuntimeException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);

        log.info("Comment deleted successfully: {}", commentId);
    }

    @Transactional(readOnly = true)
    public boolean isCommentOwner(Long commentId, Long userId) {
        return commentRepository.existsByIdAndAuthorId(commentId, userId);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        UserSummary authorSummary = new UserSummary(
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getUser().getEmail());

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(authorSummary)
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
