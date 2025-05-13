package com.example.threedbe.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
