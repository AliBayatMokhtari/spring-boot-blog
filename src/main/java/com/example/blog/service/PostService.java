package com.example.blog.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.dto.UserSummary;
import com.example.blog.model.Post;
import com.example.blog.model.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse createPost(Long userId, PostRequest request) {
        log.info("Creating post for user ID: {}", userId);

        User author = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found with ID: {}", userId);

            return new RuntimeException("User not found");
        });

        Post post = new Post();

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setAuthor(author);

        Post savedPost = postRepository.save(post);

        log.info("Post created with ID: {}", savedPost.getId());

        return mapToPostResponse(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        log.debug("Fetching posts with pagination");

        return postRepository.findAll(pageable)
                .map(this::mapToPostResponse);
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        log.debug("Fetching post with ID: {}", postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);

                    return new RuntimeException("Post not found");
                });

        return mapToPostResponse(post);
    }

    @Transactional
    public PostResponse updatePost(Long postId, Long userId, PostRequest request) {
        log.info("Updating post ID: {} by user ID: {}", postId, userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);

                    return new RuntimeException("Post not found");
                });

        if (!post.getAuthor().getId().equals(userId)) {
            log.warn("User {} tried to update post {} they don't own", userId, postId);

            throw new RuntimeException("You don't have permission to update this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        Post updatedPost = postRepository.save(post);

        log.info("Post updated successfully: {}", updatedPost.getId());

        return mapToPostResponse(updatedPost);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        log.info("Deleting post ID: {} by user ID: {}", postId, userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with ID: {}", postId);

                    return new RuntimeException("Post not found");
                });

        if (!post.getAuthor().getId().equals(userId)) {
            log.warn("User {} tried to delete post {} they don't own", userId, postId);

            throw new RuntimeException("You don't have permission to delete this post");
        }

        postRepository.delete(post);

        log.info("Post deleted successfully: {}", postId);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByAuthor(Long userId) {
        log.debug("Fetching posts for user ID: {}", userId);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);

                    return new RuntimeException("User not found");
                });

        return postRepository.findByAuthor(author)
                .stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isPostOwner(Long postId, Long userId) {
        return postRepository.existsByIdAndAuthorId(postId, userId);
    }

    private PostResponse mapToPostResponse(Post post) {
        UserSummary authorSummary = new UserSummary(
                post.getAuthor().getId(),
                post.getAuthor().getName(),
                post.getAuthor().getEmail());

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(authorSummary)
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
