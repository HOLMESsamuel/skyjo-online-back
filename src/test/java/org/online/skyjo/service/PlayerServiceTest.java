package org.online.skyjo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.online.skyjo.object.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.online.skyjo.Constants.FINISH;
import static org.online.skyjo.Constants.READY;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @InjectMocks
    PlayerService playerService;

    @Mock
    BoardService boardService;

    @Mock
    DeckService deckService;

    @Mock
    Card card;

    @Mock
    Deck deck;

    @Mock
    Board board;

    @Test
    void pickCard() {
        Deck deck = new Deck();
        Player player = new Player();

        when(deckService.pickRandomCard(any())).thenReturn(card);

        playerService.pickCard(player, deck);

        assertEquals(card, player.getCardInHand());
    }

    @Test
    void dropCard() {
        Deck deck = new Deck();
        Player player = new Player();
        player.setCardInHand(card);

        playerService.dropCard(player, deck);

        assertAll(
                () -> assertNull(player.getCardInHand()),
                () -> assertEquals(card, deck.getRemovedCards().get(0))
        );
    }

    @Test
    void getLastCard() {
        Player player = new Player();
        when(deckService.getLastCard(any())).thenReturn(card);

        playerService.getLastCard(player, deck);

        assertEquals(card, player.getCardInHand());
    }

    @Test
    void replaceCard() {
        //prepare a deck and a player with a two card grid
        Deck deck = new Deck();
        Player player = new Player();
        Card card1 = new Card(1);
        Card card2 = new Card(2);
        Card[][] grid = {{card2}, {card2}};
        Board board = new Board();
        board.setGrid(grid);
        player.setBoard(board);
        player.setCardInHand(card1);

        playerService.replaceCard(player, deck, 0, 0);

        assertAll(
                () -> assertEquals(card1, player.getBoard().getGrid()[0][0]),
                () -> assertEquals(card2, player.getBoard().getGrid()[1][0]),
                () -> assertEquals(card2, deck.getRemovedCards().get(0)),
                () -> assertNull(player.getCardInHand())
        );

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void stateFinished(boolean boardVisible) {
        when(board.isVisible()).thenReturn(boardVisible);
        Player player = new Player();
        player.setBoard(board);

        playerService.stateFinished(player);

        if(boardVisible) {
            assertEquals(FINISH, player.getState());
        } else {
            assertNotEquals(FINISH, player.getState());
        }
    }

    @Test
    void initiatePlayer() {
        Deck deck = new Deck();
        String name = "name";
        Board board = new Board();
        when(boardService.initiateBoard(any())).thenReturn(board);

        Player player = playerService.initiatePlayer(name, deck);

        assertAll(
                () -> assertEquals(name, player.getName()),
                () -> verify(boardService).initiateBoard(deck),
                () -> assertEquals(board, player.getBoard()),
                () -> assertEquals(0, player.getScore())
        );
    }

    @Test
    void resetPlayerForNextGame() {
        //prepare
        Game game = Mockito.mock(Game.class);
        when(game.getDeck()).thenReturn(new Deck());
        int score = 12;
        Player player = new Player();
        player.setCardInHand(new Card(0));
        player.setPlayerTurn(true);
        player.setState(READY);
        player.setScore(score);


        //execute
        playerService.resetPlayerForNextGame(player, game);

        //control
        assertAll(
                () -> assertFalse(player.isPlayerTurn()),
                () -> assertNull(player.getCardInHand()),
                () -> assertNull(player.getState()),
                () -> assertEquals(score, player.getScore())
        );


    }

}