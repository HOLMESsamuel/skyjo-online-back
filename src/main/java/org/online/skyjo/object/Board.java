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
		int score = 0;
		for(Card[] cardLine : this.grid) {
			for (Card card : cardLine) {
				score += card.getNumber();
			}
		}
		return score;
	}

	public void revealCard(int row, int line) {
		grid[row][line].setVisible(true);
	}

	public void replaceCard(int row, int line, Card card) {
		card.setVisible(true);
		grid[row][line] = card;
	}

	/**
	 * Make a row cores 0 if all cards of the row have the same value, just like in the game.
	 */
	public void eliminateRow() {
		for(int i = 0; i<grid[0].length; i++) {
			boolean allValueEqualsInRow = true;
			int rowValue = grid[0][i].getNumber();
			for(Card[] cards : grid) {
				allValueEqualsInRow = cards[i].getNumber() == rowValue;
			}
			if(allValueEqualsInRow && rowValue !=0) {
				for(Card[] cards : grid) {
					cards[i].setNumber(0);
				}
			}
		}
	}
}
