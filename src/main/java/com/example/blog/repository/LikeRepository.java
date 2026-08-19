package com.example.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.model.Like;
import com.example.blog.model.Post;
import com.example.blog.model.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);

    long countByPost(Post post);

    void deleteByUserAndPost(User user, Post post);

    void deleteByPost(Post post);

    @Query("SELECT l FROM Like l JOIN FETCH l.user JOIN FETCH l.post WHERE l.id = :likeId")
    Optional<Like> findByIdWithUserAndPost(@Param("likeId") Long likeId);
}
