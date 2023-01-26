package org.online.skyjo.service;

import org.online.skyjo.object.Board;
import org.online.skyjo.object.Card;
import org.online.skyjo.object.Deck;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static org.online.skyjo.Constants.LINE_NUMBER;
import static org.online.skyjo.Constants.ROW_NUMBER;

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

		Card[][] grid = new Card[LINE_NUMBER][ROW_NUMBER];

		for(int i = 0; i<LINE_NUMBER; i++) {
			for(int j = 0; j<ROW_NUMBER; j++) {
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
		for(int i = 0; i<grid[0].length; i++) {
			boolean allValueEqualsInColumn = true;
			int rowValue = grid[0][i].getNumber();
			for(Card[] cards : grid) {
				allValueEqualsInColumn = cards[i].getNumber() == rowValue && cards[i].isVisible();
			}
			if(allValueEqualsInColumn && rowValue !=0) {
				for(Card[] cards : grid) {
					deck.getRemovedCards().add(cards[i]);
					cards[i] = new Card(0);
					cards[i].setVisible(true);
					cards[i].setEliminated(true);
				}
			}
		}
	}
}
