package com.example.threedbe.post.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import com.example.threedbe.common.exception.ThreedBadRequestException;

import lombok.Getter;

@Getter
public enum PopularCondition {

	WEEK {
		public LocalDateTime calculateStartDate(LocalDateTime baseDate) {
			return baseDate.minusWeeks(1);
		}
	},
	MONTH {
		public LocalDateTime calculateStartDate(LocalDateTime baseDate) {
			return baseDate.minusMonths(1);
		}
	};

	public static final PopularCondition DEFAULT = WEEK;

	public abstract LocalDateTime calculateStartDate(LocalDateTime baseDate);

	public static Optional<PopularCondition> of(String conditionName) {
		return Arrays.stream(PopularCondition.values())
			.filter(condition -> condition.name().equals(conditionName))
			.findFirst();
	}

	public static PopularCondition fromName(String conditionName) {
		return of(conditionName)
			.orElseThrow(() -> new ThreedBadRequestException("잘못된 인기 조건입니다: " + conditionName));
	}

}
