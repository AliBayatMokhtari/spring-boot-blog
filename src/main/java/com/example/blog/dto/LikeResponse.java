package com.example.blog.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long postId;
    private LocalDateTime createdAt;
}
