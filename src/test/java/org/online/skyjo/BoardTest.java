package org.online.skyjo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.online.skyjo.object.Board;
import org.online.skyjo.object.Card;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

	@ParameterizedTest
	@CsvSource(value = { "false, false, false", "false, true, false", "true, true, true" })
	void isVisibleTest(boolean card1Visible, boolean card2Visible, boolean expectedResult) {
		Board board = createTwoCardsTestBoard(1, card1Visible, 12, card2Visible);

		assertEquals(expectedResult, board.isVisible());
	}

	@Test
	void computeScore() {
		Board board = createTwoCardsTestBoard(12, true, -1, false);

		assertEquals(11, board.computeScore());
	}

	@Test
	void computeVisibleScore() {
		Board board = createTwoCardsTestBoard(12, true, -1, false);

		assertEquals(12, board.computeVisibleScore());
	}

	@Test
	void revealCard() {
		Board board = createTwoCardsTestBoard(12, false, 12, false);

		board.revealCard(0, 0);

		assertTrue(board.getGrid()[0][0].isVisible());
	}

	@Test
	void replaceCard() {
		Board board = createTwoCardsTestBoard(12, false, 12, false);
		Card card = new Card(10);

		board.replaceCard(1, 0, card);

		assertAll(
				() -> assertEquals(10, board.getGrid()[1][0].getNumber()),
				() -> assertTrue(board.getGrid()[1][0].isVisible())
		);
	}

	/**
	 * Creates a board with two cars having the given parameters.
	 * @param numberCard1 number of the first card
	 * @param isVisibleCard1 visibility of the first card
	 * @param numberCard2 number of the second card
	 * @param isVisibleCard2 visibility of the second card
	 * @return a board with the two cars in its grid
	 */
	private Board createTwoCardsTestBoard(int numberCard1, boolean isVisibleCard1, int numberCard2, boolean isVisibleCard2) {
		Card card1 = new Card(numberCard1, isVisibleCard1, false);
		Card card2 = new Card(numberCard2, isVisibleCard2, false);
		Board board = new Board();
		Card[][] grid = { {card1}, {card2} };
		board.setGrid(grid);
		return board;
	}

}