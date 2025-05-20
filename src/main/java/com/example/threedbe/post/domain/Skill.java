package com.example.threedbe.post.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "skills")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Skill {

	public static final List<String> MAIN_SKILLS =
		List.of("JAVA", "SPRING", "NEXT.JS", "REACT", "JAVASCRIPT", "NODE.JS", "TYPESCRIPT", Skill.ETC);

	public static final String ETC = "기타";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	public Skill(String name) {
		this.name = name;
	}

}
