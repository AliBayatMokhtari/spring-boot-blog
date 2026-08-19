package com.example.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.model.Comment;
import com.example.blog.model.Post;
import com.example.blog.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    List<Comment> findByAuthor(User auth);

    List<Comment> findByPostAndAuthor(Post post, User author);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Comment c WHERE c.id = :commentId AND c.author.id = :userId")
    boolean existsByIdAndAuthorId(@Param("commentId") Long commentId, @Param("userId") Long userId);

    long countByPost(Post post);

    void deleteByPost(Post post);
}
