package org.online.skyjo.service;

import org.online.skyjo.object.Board;
import org.online.skyjo.object.Deck;
import org.online.skyjo.object.Player;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.Objects;

import static org.online.skyjo.Constants.*;

@ApplicationScoped
public class PlayerService {

    @Inject
    DeckService deckService;

    public void pickCard(Player player, Deck deck) {
        player.setCardInHand(deckService.pickRandomCard(deck));
    }

    public void dropCard(Player player, Deck deck) {
        deck.getRemovedCards().add(player.getCardInHand());
        player.setCardInHand(null);
    }

    /**
     * Replaces the player's card at row and line by the card he has in hand and remove the old card.
     * @param player player that does the replacement
     * @param deck deck where to place the removed card
     * @param row row of the card to replace
     * @param line line of the card to replace
     */
    public void replaceCard(Player player, Deck deck, int row, int line) {
        Board board = player.getBoard();
        deck.getRemovedCards().add(board.getGrid()[row][line]);
        board.replaceCard(row, line, player.getCardInHand());
        player.setCardInHand(null);
    }

    /**
     * Make the player takes the last removed card.
     * @param player player that gets the card
     * @param deck deck from which the player takes the card
     */
    public void getLastCard(Player player, Deck deck) {
        player.setCardInHand(deck.getLastCard());
    }

    /**
     * Phase 1 of player's turn, he gets a card either from the deck or from the last removed card.
     * @param player player that takes a new card in his hand
     * @param choice choice whether to take the card from deck or removed cards
     * @param deck deck to take the card from
     */
    public void getCard(Player player, String choice, Deck deck) {
        if(Objects.equals(choice, PICK_FROM_REMOVED)) {
            getLastCard(player, deck);
        } else if (Objects.equals(choice, PICK_FROM_DECK)) {
            pickCard(player, deck);
        }
    }

    /**
     * Phase 2 of player's turn once he has a card he decides whether to discard it or place it on his board.
     * @param player player that is playing
     * @param choice choice whether to dicard the card or place it
     * @param deck deck to discard the card if chosen
     * @param row row of the place to put the card on if chosen
     * @param line line of the place to put the card on if chosen
     */
    public void playCard(Player player, String choice, Deck deck, int row, int line) {
        if(Objects.equals(choice, DROP_AND_REVEAL)) {
            dropCard(player, deck);
            player.getBoard().revealCard(row, line);
        } else if (Objects.equals(choice, REPLACE_CARD)) {
            replaceCard(player, deck, row, line);
        }
    }
}