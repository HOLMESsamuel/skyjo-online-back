package org.online.skyjo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.online.skyjo.object.Board;
import org.online.skyjo.object.Card;
import org.online.skyjo.object.Deck;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.online.skyjo.Constants.LINE_NUMBER;
import static org.online.skyjo.Constants.ROW_NUMBER;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

	@InjectMocks
	BoardService boardService;

	@Mock
	DeckService deckService;

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
		Card card22 = new Card(2);
		Card card23 = new Card(2);

		//to verify that all cards in the column needs to be visible to be eliminated
		card00.setVisible(true);

		//to verify the column is eliminated if all 3 cards are equal and visible
		card01.setVisible(true);
		card11.setVisible(true);
		card21.setVisible(true);

		//to verify even if all cards in column are visible they need to be the same value to be eliminated
		card02.setVisible(true);
		card12.setVisible(true);
		card22.setVisible(true);

		Card[][] grid = {{card00, card01, card02, card03},
				{card10, card11, card12, card13},
				{card20, card21, card22, card23}};

		Deck deck = new Deck();
		Board board = new Board();
		board.setGrid(grid);

		//execute
		boardService.eliminateColumn(board, deck);

		//control
		assertAll(
				//to verify that all cards in the column needs to be visible to be eliminated
				() -> assertEquals(1, board.getGrid()[0][0].getNumber()),
				//to verify the column is eliminated if all 3 cards are equal and visible
				() -> assertEquals(0, board.getGrid()[0][1].getNumber()),
				() -> assertTrue(board.getGrid()[0][1].isEliminated()),
				() -> assertEquals(0, board.getGrid()[1][1].getNumber()),
				() -> assertTrue(board.getGrid()[1][1].isEliminated()),
				() -> assertEquals(0, board.getGrid()[2][1].getNumber()),
				() -> assertTrue(board.getGrid()[2][1].isEliminated()),
				//to verify even if all cards in column are visible they need to be the same value to be eliminated
				() -> assertEquals(1, board.getGrid()[0][2].getNumber()),
				() -> assertFalse(board.getGrid()[0][2].isEliminated()),
				() -> assertEquals(1, board.getGrid()[1][2].getNumber()),
				() -> assertFalse(board.getGrid()[1][2].isEliminated()),
				() -> assertEquals(2, board.getGrid()[2][2].getNumber()),
				() -> assertFalse(board.getGrid()[2][2].isEliminated()),
				//verify that if cards are not visible and not equal they are not eliminated
				() -> assertEquals(1, board.getGrid()[0][3].getNumber()),
				() -> assertFalse(board.getGrid()[0][3].isEliminated()),
				() -> assertEquals(1, board.getGrid()[1][3].getNumber()),
				() -> assertFalse(board.getGrid()[1][3].isEliminated()),
				() -> assertEquals(2, board.getGrid()[2][3].getNumber()),
				() -> assertFalse(board.getGrid()[2][3].isEliminated()),
				//verify that the eliminated cards are dropped in the removed cards
				() -> assertEquals(1, deck.getRemovedCards().get(0).getNumber())
		);
	}

	@Test
	void initiateBoard() {
		Deck deck = new Deck();
		Card card = new Card(12);
		when(deckService.pickRandomCard(any())).thenReturn(card);

		Board board = boardService.initiateBoard(deck);

		assertAll(
				() -> assertEquals(0, board.getScore()),
				() -> assertNotNull(board.getGrid()),
				() -> assertEquals(LINE_NUMBER, board.getGrid().length),
				() -> assertEquals(ROW_NUMBER, board.getGrid()[0].length),
				() -> assertEquals(card, board.getGrid()[0][0])
		);
	}
}