package com.example.threedbe.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

}
