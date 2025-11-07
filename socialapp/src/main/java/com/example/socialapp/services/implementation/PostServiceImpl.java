package com.example.socialapp.services.implementation;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.socialapp.dto.PostDto;
import com.example.socialapp.entity.Post;
import com.example.socialapp.entity.User;
import com.example.socialapp.repository.PostRepository;
import com.example.socialapp.repository.UserRepository;
import com.example.socialapp.services.interfaces.IPostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements IPostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Post createPost(PostDto dto, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = Post.builder()
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .author(author)
                .likesCount(0)
                .build();

        return postRepository.save(post);
    }

    @Override
    public Page<Post> getAllPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public Post likePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikesCount(post.getLikesCount() + 1);
        return postRepository.save(post);
    }
}
