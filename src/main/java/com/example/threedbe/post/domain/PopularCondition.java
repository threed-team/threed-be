package com.example.threedbe.post.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

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

	public abstract LocalDateTime calculateStartDate(LocalDateTime baseDate);

	public static Optional<PopularCondition> of(String conditionName) {
		return Arrays.stream(PopularCondition.values())
			.filter(condition -> condition.name().equals(conditionName))
			.findFirst();
	}

}
