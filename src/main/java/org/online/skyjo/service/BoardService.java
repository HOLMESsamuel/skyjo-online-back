package org.online.skyjo.service;

import org.online.skyjo.object.Board;
import org.online.skyjo.object.Card;
import org.online.skyjo.object.Deck;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BoardService {

	/**
	 * Make a row cores 0 if all cards of the row have the same value, just like in the game.
	 */
	public void eliminateRow(Board board, Deck deck) {
		Card[][] grid = board.getGrid();
		for(int i = 0; i<grid[0].length; i++) {
			boolean allValueEqualsInRow = true;
			int rowValue = grid[0][i].getNumber();
			for(Card[] cards : grid) {
				allValueEqualsInRow = cards[i].getNumber() == rowValue;
			}
			if(allValueEqualsInRow && rowValue !=0) {
				for(Card[] cards : grid) {
					deck.getRemovedCards().add(cards[i]);
					cards[i] = new Card(0);
				}
			}
		}
	}
}
