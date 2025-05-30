package com.example.threedbe.post.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.threedbe.post.domain.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {

	Optional<Skill> findByName(String name);

	List<Skill> findByNameIn(Collection<String> names);

}
