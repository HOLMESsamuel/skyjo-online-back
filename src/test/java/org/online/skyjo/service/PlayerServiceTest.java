package org.online.skyjo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.online.skyjo.object.Board;
import org.online.skyjo.object.Card;
import org.online.skyjo.object.Deck;
import org.online.skyjo.object.Player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @InjectMocks
    PlayerService playerService;

    @Mock
    DeckService deckService;

    @Mock
    Card card;

    @Mock
    Deck deck;

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
        when(deck.getLastCard()).thenReturn(card);

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

}