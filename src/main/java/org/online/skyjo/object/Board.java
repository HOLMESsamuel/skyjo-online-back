package org.online.skyjo.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {
	private int score;
	private Card[][] grid;

	/**
	 * Return true if all boards cards are visible and false otherwise.
	 * @return true if all board cards are visible
	 */
	public boolean isVisible() {
		for(Card[] cardLine : this.grid) {
			for(Card card : cardLine) {
				if (!card.isVisible()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Computes the board's score by adding each card number.
	 * @return the board score
	 */
	public int computeScore() {
		int boardScore = 0;
		for(Card[] cardLine : this.grid) {
			for (Card card : cardLine) {
				boardScore += card.getNumber();
			}
		}
		return boardScore;
	}

	public void revealCard(int row, int line) {
		grid[row][line].setVisible(true);
	}

	public void revealAllCards() {
		for(Card[] cardLine : this.grid) {
			for (Card card : cardLine) {
				card.setVisible(true);
			}
		}
	}

	public void replaceCard(int row, int line, Card card) {
		card.setVisible(true);
		grid[row][line] = card;
	}
}
