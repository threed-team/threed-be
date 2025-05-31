package com.example.threedbe.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.post.domain.MemberPost;

public interface MemberPostRepository extends JpaRepository<MemberPost, Long>, MemberPostRepositoryCustom {

}
