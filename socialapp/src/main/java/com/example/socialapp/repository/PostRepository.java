package com.example.socialapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.socialapp.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
