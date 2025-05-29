package com.example.threedbe.post.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.threedbe.post.domain.Skill;
import com.example.threedbe.post.repository.SkillRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkillService {

	private final SkillRepository skillRepository;

	@Transactional
	public List<Skill> findOrCreateSkills(Collection<String> skillNames) {
		if (skillNames == null || skillNames.isEmpty()) {
			return Collections.emptyList();
		}

		Set<String> uniqueNames = new HashSet<>(skillNames);
		List<Skill> existingSkills = skillRepository.findByNameIn(uniqueNames);
		Set<String> existingSkillNames = existingSkills.stream()
			.map(Skill::getName)
			.collect(Collectors.toSet());

		List<Skill> newSkills = uniqueNames.stream()
			.filter(name -> !existingSkillNames.contains(name))
			.map(Skill::new)
			.toList();

		if (!newSkills.isEmpty()) {
			skillRepository.saveAll(newSkills);
		}

		return Stream.concat(existingSkills.stream(), newSkills.stream())
			.toList();
	}

}
