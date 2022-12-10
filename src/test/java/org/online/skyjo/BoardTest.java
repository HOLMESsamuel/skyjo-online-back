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

	@Test
	void eliminateRow() {
		//prepare
		Card card00 = new Card(1);
		Card card01 = new Card(1);
		Card card02 = new Card(1);
		Card card03 = new Card(1);
		Card card10 = new Card(1);
		Card card11 = new Card(1);
		Card card12 = new Card(1);
		Card card13 = new Card(1);
		Card card20 = new Card(1);
		Card card21 = new Card(1);
		Card card22 = new Card(1);
		Card card23 = new Card(2);

		Card[][] grid = {{card00, card01, card02, card03},
						{card10, card11, card12, card13},
						{card20, card21, card22, card23}};

		Board board = new Board();
		board.setGrid(grid);

		//execute
		board.eliminateRow();

		//control
		assertAll(
				() -> assertEquals(0, board.getGrid()[0][0].getNumber()),
				() -> assertEquals(0, board.getGrid()[1][0].getNumber()),
				() -> assertEquals(0, board.getGrid()[2][0].getNumber()),
				() -> assertEquals(2, board.getGrid()[2][3].getNumber())
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
		Card card1 = new Card(numberCard1, isVisibleCard1);
		Card card2 = new Card(numberCard2, isVisibleCard2);
		Board board = new Board();
		Card[][] grid = { {card1}, {card2} };
		board.setGrid(grid);
		return board;
	}

}