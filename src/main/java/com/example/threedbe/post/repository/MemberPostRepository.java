package com.example.threedbe.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.post.domain.MemberPost;

public interface MemberPostRepository extends JpaRepository<MemberPost, Long> {

	Page<MemberPost> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

}
