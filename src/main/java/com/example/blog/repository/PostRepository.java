package com.example.blog.repository;

import java.util.List;

import org.springframework.boot.data.autoconfigure.web.DataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.model.Post;
import com.example.blog.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);

    Page<Post> findByAuthor(User author, Pageable pageable);

    List<Post> findByTitleContainingIgnoreCase(String keyword);

    @Query("Select p FROM Post LEFT JOIN FETCH p.likes WHERE p.id = :postId")
    Post findByIdWithLikes(@Param("postId") Long postId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Post p where p.id = :postId AND p.author.id = :userId")
    boolean existsByIdAndAuthorId(@Param("postId") Long postId, @Param("userId") Long userId);

    @Query("SELECT p, COUNT(c) as commentCount FROM Post p " +
            "LEFT JOIN p.comments c " +
            "Group BY p.id")
    List<Object[]> findAllWithCommentCounts();
}
