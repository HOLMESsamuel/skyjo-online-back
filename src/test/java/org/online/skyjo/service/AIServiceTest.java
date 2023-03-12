package org.online.skyjo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.online.skyjo.object.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceTest {

	@InjectMocks
	@Spy
	AIService aiService;

	@Mock
	BoardService boardService;

	@Test
	void maxNumberOfVisibleCards() {
		Board boardWith2VisibleCards = new Board();
		Board boardWith5VisibleCards = new Board();
		when(boardService.numberOfVisibleCards(boardWith2VisibleCards)).thenReturn(2);
		when(boardService.numberOfVisibleCards(boardWith5VisibleCards)).thenReturn(5);
		Player player1 = new Player();
		player1.setBoard(boardWith2VisibleCards);
		Player player2 = new Player();
		player2.setBoard(boardWith5VisibleCards);
		Game game = new Game();
		ArrayList<Player> players = new ArrayList<>();
		players.add(player1);
		players.add(player2);
		game.setPlayers(players);

		assertEquals(5, aiService.maxNumberOfVisibleCards(game));
	}

	@Test
	void computeAIScoreAfterPlacing() {
		Game game = new Game();
		Board board = mock(Board.class);
		when(board.computeVisibleScore()).thenReturn(15);
		when(boardService.numberOfVisibleCards(board)).thenReturn(10);
		doReturn(10).when(aiService).maxNumberOfVisibleCards(game);

		assertEquals(2.5, aiService.computeAIScoreAfterPlacing(game, board));
	}

	@Test
	void computeAIScoreBeforeRevealing() {
		Game game = new Game();
		Board board = mock(Board.class);
		when(board.computeVisibleScore()).thenReturn(2);
		when(boardService.numberOfVisibleCards(board)).thenReturn(2);
		doReturn(3).when(aiService).maxNumberOfVisibleCards(game);

		assertEquals(2.0, aiService.computeAIScoreBeforeRevealing(game, board));
	}
}