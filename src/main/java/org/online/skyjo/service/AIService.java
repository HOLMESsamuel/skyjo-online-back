package org.online.skyjo.service;

import org.online.skyjo.object.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;

import static org.online.skyjo.Constants.*;

/**
 * Contains all the methods used by bots to take decisions.
 */
@ApplicationScoped
public class AIService {

	@Inject
	BoardService boardService;

	@Inject
	PlayerService playerService;

	@Inject
	RandomProvider randomProvider;

	public Choice chooseCard(Game game, Player bot) {
		Deck deck = game.getDeck();
		ArrayList<Card> removedCards = deck.getRemovedCards();
		if(removedCards.isEmpty()) {
			return new Choice(PICK_FROM_DECK, 0, 0);
		} else {
			Card lastCard = removedCards.get(removedCards.size() - 1);
			float boardAiScore = computeAIScoreAfterPlacing(game, bot.getBoard());
			float[][] scoresIfPickFromRemovedCards = computeAIScoreOfAllPlacements(game, bot.getBoard(), lastCard);
			Choice bestChoice = findBestChoice(scoresIfPickFromRemovedCards, PICK_FROM_REMOVED);

			if(scoresIfPickFromRemovedCards[bestChoice.getRow()][bestChoice.getColumn()] - boardAiScore < -0.85){
				return bestChoice;
				//if it is still a good card we place it on another spot
			} else if(lastCard.getNumber() < 6) {
				int[] randomNotVisibleCardCoordinates = findRandomNotVisibleCard(bot);
				return new Choice(PICK_FROM_REMOVED, randomNotVisibleCardCoordinates[1], randomNotVisibleCardCoordinates[0]);
			}
			return new Choice(PICK_FROM_DECK, 0, 0);
		}
	}

	/**
	 * Returns the maximum number of visible cards amoung all players.
	 */
	protected int maxNumberOfVisibleCards(Game game) {
		int maxNumberOfVisibleCards = boardService.numberOfVisibleCards(game.getPlayers().get(0).getBoard());
		for(int i = 1; i<game.getPlayers().size(); i++) {
			maxNumberOfVisibleCards = Math.max(maxNumberOfVisibleCards, boardService.numberOfVisibleCards(game.getPlayers().get(i).getBoard()));
		}
		return maxNumberOfVisibleCards;
	}

	/**
	 * Computes a score for a board after we just placed a card on it to help for the decision.
	 */
	protected float computeAIScoreAfterPlacing(Game game, Board board){
		int numberOfVisibleCards = boardService.numberOfVisibleCards(board);
		int maxNumberOfVisibleCards = maxNumberOfVisibleCards(game);
		int boardScore = board.computeVisibleScore();

		return (float) (boardScore+maxNumberOfVisibleCards)/numberOfVisibleCards;
	}

	/**
	 * Place the card on each possible place of the board and computes the score to store it in the returned float array.
	 */
	protected float[][] computeAIScoreOfAllPlacements(Game game, Board board, Card card) {
		float[][] aiScoreGrid = new float[ROW_NUMBER][COLUMN_NUMBER];
		Deck mockDeck = new Deck();
		for(int i = 0; i<ROW_NUMBER; i++){
			for(int j = 0; j<COLUMN_NUMBER; j++) {
				Board copyBoard = board.deepCopy();
				if(copyBoard.getGrid()[i][j].isEliminated()) {
					//in that case it is impossible to replace the card
					aiScoreGrid[i][j] = Float.POSITIVE_INFINITY;
				} else {
					copyBoard.replaceCard(i, j, card);
					boardService.eliminateColumn(copyBoard, mockDeck);
					aiScoreGrid[i][j] = computeAIScoreAfterPlacing(game, copyBoard);
				}

			}
		}
		return aiScoreGrid;
	}

	/**
	 * Computes the score of the board when a card will be revealed on it to help for the decision.
	 */
	protected float computeAIScoreBeforeRevealing(Game game, Board board) {
		int numberOfVisibleCards = boardService.numberOfVisibleCards(board);
		int maxNumberOfVisibleCards = maxNumberOfVisibleCards(game);
		int boardScore = board.computeVisibleScore();

		return  ((float)boardScore/numberOfVisibleCards) + ((float)maxNumberOfVisibleCards/(numberOfVisibleCards+1));
	}

	public void placeOrDropCard(Game game, Player bot, Choice choice) {
		if(PICK_FROM_REMOVED.equals(choice.getChoiceString())) {
			playerService.replaceCard(bot, game.getDeck(), choice.getRow(), choice.getColumn());
		} else if (PICK_FROM_DECK.equals(choice.getChoiceString())) {
			float scoreIfDrop = computeAIScoreBeforeRevealing(game, bot.getBoard());
			float[][] scoresIfPlaceCard = computeAIScoreOfAllPlacements(game, bot.getBoard(), bot.getCardInHand());
			Choice bestChoice = findBestChoice(scoresIfPlaceCard, REPLACE_CARD);
			float bestScore = scoresIfPlaceCard[bestChoice.getRow()][choice.getColumn()];
			if(scoreIfDrop<bestScore) {
				dropAndRevealRandomCard(game, bot);
			} else {
				float boardAiScore = computeAIScoreAfterPlacing(game, bot.getBoard());
				if(scoresIfPlaceCard[bestChoice.getRow()][bestChoice.getColumn()] - boardAiScore < -0.85){
					playerService.playCard(bot, bestChoice.getChoiceString(), game.getDeck(), bestChoice.getRow(), bestChoice.getColumn());
					//if it is still a good card we place it on another spot
				} else {
					int[] randomNotVisibleCardCoordinates = findRandomNotVisibleCard(bot);
					playerService.playCard(bot, PICK_FROM_REMOVED, game.getDeck(), randomNotVisibleCardCoordinates[0], randomNotVisibleCardCoordinates[1]);
				}

			}
		}
	}

	protected int[] findRandomNotVisibleCard(Player bot) {
		int[] coordinates = new int[2];
		int row = randomProvider.getRandom().nextInt(ROW_NUMBER);
		int column = randomProvider.getRandom().nextInt(COLUMN_NUMBER);
		Card cardToReveal = bot.getBoard().getGrid()[row][column];
		while(cardToReveal.isVisible()){
			row = randomProvider.getRandom().nextInt(ROW_NUMBER);
			column = randomProvider.getRandom().nextInt(COLUMN_NUMBER);
			cardToReveal = bot.getBoard().getGrid()[row][column];
		}
		coordinates[0] = row;
		coordinates[1] = column;
		return coordinates;
	}
	protected void dropAndRevealRandomCard(Game game, Player bot) {
		playerService.dropCard(bot, game.getDeck());
		int[] randomUnvisibleCardCoordinates = findRandomNotVisibleCard(bot);
		bot.getBoard().revealCard(randomUnvisibleCardCoordinates[0], randomUnvisibleCardCoordinates[1]);
	}

	protected Choice findBestChoice(float[][] possibilities, String choiceType) {
		float bestScore = possibilities[0][0];
		Choice bestChoice = new Choice(choiceType, 0, 0);
		for(int i = 0; i<ROW_NUMBER; i++) {
			for (int j = 0; j < COLUMN_NUMBER; j++) {
				if(possibilities[i][j]<bestScore) {
					bestScore = possibilities[i][j];
					bestChoice.setRow(i);
					bestChoice.setColumn(j);
				}
			}
		}
		return bestChoice;
	}
}
