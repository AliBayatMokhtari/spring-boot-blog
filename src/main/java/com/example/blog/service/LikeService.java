package com.example.blog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.dto.LikeResponse;
import com.example.blog.model.Like;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.LikeRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponse likePost(Long postId, Long userId) {
        log.info("User ID: {} liking post ID: {}", userId, postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);

                    return new RuntimeException("Post not found");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);

                    return new RuntimeException("User not found");
                });

        if (likeRepository.existsByUserAndPost(user, post)) {
            log.warn("User {} already liked post {}", userId, postId);

            throw new RuntimeException("You already liked this post");
        }

        Like like = new Like();

        like.setUser(user);
        like.setPost(post);

        Like savedLike = likeRepository.save(like);

        log.info("Like created with ID: {} for post: {}", savedLike.getId(), postId);

        return mapToLikeResponse(savedLike);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        log.info("User ID: {} unliking post ID: {}", userId, postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);

                    return new RuntimeException("Post not found");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);

                    return new RuntimeException("User not found");
                });

        if (!likeRepository.existsByUserAndPost(user, post)) {
            log.warn("User {} hasn't liked post {}", userId, postId);

            throw new RuntimeException("You haven't liked this post");
        }

        likeRepository.deleteByUserAndPost(user, post);

        log.info("Like removed for post: {} by user: {}", postId, userId);
    }

    @Transactional(readOnly = true)
    public long getLikeCount(Long postId) {
        log.debug("Getting like count for post ID: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);

                    return new RuntimeException("Post not found");
                });

        return likeRepository.countByPost(post);
    }

    @Transactional(readOnly = true)
    public boolean hasUserLikedPost(Long postId, Long userId) {
        log.debug("Checking if user {} liked post {}", userId, postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);

                    return new RuntimeException("Post not found");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);

                    return new RuntimeException("User not found");
                });

        return likeRepository.existsByUserAndPost(user, post);
    }

    private LikeResponse mapToLikeResponse(Like like) {
        return LikeResponse.builder()
                .id(like.getId())
                .userId(like.getUser().getId())
                .userName(like.getUser().getName())
                .postId(like.getPost().getId())
                .createdAt(like.getCreatedAt())
                .build();
    }
}
