package com.example.socialapp.dto;

import java.time.LocalDateTime;
import com.example.socialapp.entity.Post;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private int likesCount;
    private String authorUsername;

    public static PostResponseDto fromEntity(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .likesCount(post.getLikesCount())
                .authorUsername(post.getAuthor() != null ? post.getAuthor().getUsername() : null)
                .build();
    }
}
