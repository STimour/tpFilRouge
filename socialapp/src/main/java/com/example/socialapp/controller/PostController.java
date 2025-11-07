package com.example.socialapp.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.example.socialapp.dto.PostDto;
import com.example.socialapp.dto.PostResponseDto;
import com.example.socialapp.entity.Post;
import com.example.socialapp.services.interfaces.IPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final IPostService postService;

    /**
     * Crée un nouveau post pour l'utilisateur connecté.
     */
    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestBody PostDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Post post = postService.createPost(dto, userDetails.getUsername());
        PostResponseDto response = PostResponseDto.fromEntity(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Récupère les posts paginés pour l'affichage (scroll infini).
     */
    @GetMapping
    public ResponseEntity<Page<PostResponseDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponseDto> posts = postService.getAllPosts(page, size)
                .map(PostResponseDto::fromEntity);
        return ResponseEntity.ok(posts);
    }

    /**
     * Ajoute un "like" à un post existant.
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<PostResponseDto> likePost(@PathVariable Long id) {
        Post liked = postService.likePost(id);
        PostResponseDto response = PostResponseDto.fromEntity(liked);
        return ResponseEntity.ok(response);
    }
}
