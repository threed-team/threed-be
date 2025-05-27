package com.example.threedbe.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.post.domain.CompanyPost;

public interface CompanyPostRepository extends JpaRepository<CompanyPost, Long>, CompanyPostRepositoryCustom {

}
