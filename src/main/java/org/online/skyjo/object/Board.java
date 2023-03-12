package org.online.skyjo.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board{
	private int score;
	private Card[][] grid;

	public Board deepCopy() {
		Card[][] copyGrid = new Card[3][4];
		for(int i = 0; i<3; i++) {
			for(int j = 0; j<4; j++) {
				Card originalCard = this.grid[i][j];
				if(originalCard != null) {
					Card copyCard = new Card(originalCard);
					copyGrid[i][j] = copyCard;
				}
			}
		}
		Board copyBoard = new Board();
		copyBoard.setScore(this.score);
		copyBoard.setGrid(copyGrid);

		return copyBoard;
	}

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

	/**
	 * Computes the board's score by adding each visible card number.
	 * @return the board score
	 */
	public int computeVisibleScore() {
		int boardScore = 0;
		for(Card[] cardLine : this.grid) {
			for (Card card : cardLine) {
				if(card.isVisible()) {
					boardScore += card.getNumber();
				}
			}
		}
		return boardScore;
	}

	public void revealCard(int row, int column) {
		grid[row][column].setVisible(true);
	}

	public void revealAllCards() {
		for(Card[] cardLine : this.grid) {
			for (Card card : cardLine) {
				card.setVisible(true);
			}
		}
	}

	public void replaceCard(int row, int column, Card card) {
		card.setVisible(true);
		grid[row][column] = card;
	}
}
