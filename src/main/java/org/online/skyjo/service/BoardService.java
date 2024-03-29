package org.online.skyjo.service;

import org.online.skyjo.object.Board;
import org.online.skyjo.object.Card;
import org.online.skyjo.object.Deck;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.online.skyjo.Constants.ROW_NUMBER;
import static org.online.skyjo.Constants.COLUMN_NUMBER;

@ApplicationScoped
public class BoardService {

	@Inject
	DeckService deckService;

	/**
	 * Creates a bord with 12 random cards from the deck.
	 * @param deck deck to take the cards from
	 * @return the board containing a 0 score and a twelve card grid
	 */
	public Board initiateBoard(Deck deck) {
		Board board = new Board();

		Card[][] grid = new Card[ROW_NUMBER][COLUMN_NUMBER];

		for(int i = 0; i< ROW_NUMBER; i++) {
			for(int j = 0; j< COLUMN_NUMBER; j++) {
				grid[i][j] = deckService.pickRandomCard(deck);
				grid[i][j].setVisible(false);
			}
		}

		board.setGrid(grid);
		board.setScore(0);

		return board;
	}

	/**
	 * Make a column cards 0 if all cards of the column have the same value, just like in the game.
	 */
	public void eliminateColumn(Board board, Deck deck) {
		Card[][] grid = board.getGrid();
		//go through the 4 columns
		for(int i = 0; i <4; i++) {
			//get the first number in column
			int columnValue = grid[0][i].getNumber();
			//suppose all values in column are equals to the first number of the column
			boolean allValueEqualsInColumn = true;

			for(int j = 0; j<3; j++) {
				allValueEqualsInColumn = allValueEqualsInColumn && grid[j][i].getNumber() == columnValue && grid[j][i].isVisible();
			}

			if(allValueEqualsInColumn && columnValue !=0) {
				for(Card[] cards : grid) {
					deck.getRemovedCards().add(cards[i]);
					cards[i] = new Card(0);
					cards[i].setVisible(true);
					cards[i].setEliminated(true);
				}
			}
		}
	}

	/**
	 * Reveal a number of random cards of the board.
	 * Used to reveal the two first cards of the bot.
	 */
	public void revealRandomCards(Board board, int numberOfCards) {
		Card[][] grid = board.getGrid();
		for(int i = 0; i<numberOfCards; i++) {
			int randomRow = (int) (Math.random() * 3);
			int randomLine = (int) (Math.random() * 4);
			if(!grid[randomRow][randomLine].isVisible()) {
				grid[randomRow][randomLine].setVisible(true);
			} else {
				i--;
			}
		}
	}

	public int numberOfVisibleCards(Board board) {
		Card[][] grid = board.getGrid();
		int numberOfVisibleCards = 0;
		for(int i = 0; i< ROW_NUMBER; i++) {
			for(int j = 0; j< COLUMN_NUMBER; j++) {
				if(grid[i][j] != null && grid[i][j].isVisible()){
					numberOfVisibleCards += 1;
				}
			}
		}
		return numberOfVisibleCards;
	}
}
