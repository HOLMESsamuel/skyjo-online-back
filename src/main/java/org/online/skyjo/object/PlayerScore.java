package org.online.skyjo.object;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PlayerScore {
	List<Integer> scores;
	int totalScore;

	public PlayerScore(List<Integer> scores) {
		this.scores = scores;
		this.totalScore = scores.stream().mapToInt(Integer::intValue).sum();
	}
}
