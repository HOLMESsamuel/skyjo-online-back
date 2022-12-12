package org.online.skyjo.service;

import org.online.skyjo.object.Deck;
import org.online.skyjo.object.Player;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
     * Make the player takes the last removed card.
     * @param player player that gets the card
     * @param deck deck from which the player takes the card
     */
    public void getLastCard(Player player, Deck deck) {
        player.setCardInHand(deck.getLastCard());
    }
}
