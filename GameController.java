/**
 * @author Deniz Erisgen ©
 **/

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GameController {
	int doublePass; // to keep track of double pass
	private CardGameModel model;
	private CardTableView view;
	private boolean clockStopped = true;
	private GameTimer timer;

	GameController() {
	}

	GameController(CardGameModel model, CardTableView view) {
		this.model = model;
		this.view = view;
		view.controller = this;
		timer = new GameTimer(5); // 5 minute timer
		view.setupTheLayoutAndPanels();
	}

	/**
	 * Ends the game and shows Scores and cards those were a match
	 */
	void endTheGame() {
		clockStopped = true;
		int playerScore, computerScore;
		computerScore = model.getTotalScoreOfPlayer(0);
		playerScore = model.getTotalScoreOfPlayer(1);
		StringBuilder winner = new StringBuilder();
		if (computerScore == playerScore) winner.append("It is a draw");
		else {
			winner.append("Winner is ")
				.append((playerScore < computerScore) ? "Player" : "Computer");
		}
		winner.append('\n').append("Player : ").append(playerScore)
			.append(" Computer : ").append(computerScore).append('\n');
		JOptionPane scoreboard = new JOptionPane(winner,
			JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
		JDialog dialog = scoreboard.createDialog("Game Over");
		dialog.setVisible(true);
		System.exit(0);
	}

	/**
	 * Retrieves card from a player at an index
	 *
	 * @param playerID  (int) 1 for player 0 for computer
	 * @param cardIndex index of the card in player hand
	 * @return a clone of the card at index
	 */
	@SuppressWarnings("SameParameterValue")
	Card findCard(int playerID, int cardIndex) {
		return model.getHand(playerID).inspectCard(cardIndex);
	}

	/**
	 * Retrieves choice from view
	 *
	 * @return true if choice is yes
	 */
	boolean playerStarts() {
		return view.askForStart();
	}

	/**
	 * Computer tries to play a round
	 */
	void computerPlay() {
		int[] indexes = model.lookForAMove();
		if (indexes == null) {
			doublePass++;
			playerPassed(0);
			return;
		}
		if (playCardTo(0, indexes[0], indexes[1])) doublePass = 0;
		view.updateScoreboard();
	}

	/**
	 * Plays card from a player to a stack
	 *
	 * @param playerID  (int) 1 for player 0 for computer
	 * @param cardIndex (int) index of card in hand
	 * @param indexTo   (int) stack index to place the card
	 * @return true if successful
	 */
	boolean playCardTo(int playerID, int cardIndex, int indexTo) {
		if (playerID <0 || cardIndex<0 || indexTo <0) return false;
		Card cardToPlay = model.playCard(playerID, cardIndex);
		model.addToPlayStack(cardToPlay, indexTo);
		view.addToPlayArea(playerID, cardToPlay, indexTo);
		if (cardsLeft() > 0) view.addToPlayerHand(playerID, model.dealACardTo(playerID));
		else endTheGame(); // no cards left in deck ends the game
		return true;
	}

	void startTimer() {
		clockStopped = false;
		view.toggleTimerButton();
		timer.start();
	}

	void flipClockSwitch() {
		clockStopped = !clockStopped;
	}

	/**
	 * Player pass a round
	 *
	 * @param playerID (int) 1 for player 0 for computer
	 */
	void playerPassed(int playerID) {
		if (doublePass == 2) {
			dealNewCardsToStacks();
			doublePass = 0;
		} else if (playerID != 0) {
			computerPlay();
		}
		model.updatePassCounter(playerID);
	}

	private void dealNewCardsToStacks() {
		model.refreshCardStack();
		view.refreshStacks(model.getCardsOnStacks());
	}

	/**
	 * Gets the player score
	 *
	 * @param playerID (int) 1 for player 0 for computer
	 * @return (int) total score of player
	 */
	int retrieveScore(int playerID) {
		return model.getTotalScoreOfPlayer(playerID);
	}

	int cardsLeft() {
		return model.cardsLeftInDeck();
	}

	CardButtonListener getCardListener() {
		return new CardButtonListener();
	}

	@SuppressWarnings("SameParameterValue")
	int playerCardsLeft(int playerID) {
		return model.getHand(playerID).getNumCards();
	}

	/**
	 * Inner Action Listener class to listen for card selections
	 */
	class CardButtonListener implements ActionListener {
		static int firstButtonIndex = -1;

		@Override
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() instanceof JToggleButton) {
				if (CardButtonListener.firstButtonIndex != -1) {
					view.deselectAllButtons();
					CardButtonListener.firstButtonIndex = -1;
				} else {
					CardButtonListener.firstButtonIndex = view.findIndexOfCard(((JToggleButton) event.getSource()).getIcon(), false);
				}
			} else if (CardButtonListener.firstButtonIndex != -1) {
				Icon stackIcon = ((JButton) event.getSource()).getIcon();
				int stackIndex = view.findIndexOfCard(stackIcon, true);
				if (stackIcon.toString().contains("BK") ||
					    model.isAValidMove(CardButtonListener.firstButtonIndex, stackIndex)) {
					playCardTo(1, CardButtonListener.firstButtonIndex, stackIndex);
					doublePass = 0;
					computerPlay();
				}
				CardButtonListener.firstButtonIndex = -1;
				view.deselectAllButtons();
			}
			view.validate();
			view.repaint();
		}
	}

	class GameTimer extends Thread {
		private int time;
		private final int duration;

		public GameTimer(int minutes) {
			this.duration = minutes;
		}

		/**
		 * Sleep for 1 second
		 */
		private void doNothing() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Increment time and updates timer display
		 */
		private void incrementTimer() {
			time++;
			int min = time / 60;
			if (min == duration) endTheGame();
			int seconds = time - (60 * min);
			String timerDuration = String.format("%02d", min) + " : "
				                       + String.format("%02d", seconds);
			view.updateTimer(timerDuration);
		}

		@Override
		public synchronized void run() {
			while (true) {
				if (!clockStopped) incrementTimer();
				doNothing();
			}
		}
	}

}