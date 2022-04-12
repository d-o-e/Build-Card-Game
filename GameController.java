/**
 * Deniz Erisgen
 * Assignment 6 Phase 3
 * IDE: IntelliJ
 **/

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GameController {
   private CardGameModel model;
   private CardTableView view;
   private boolean clockStopped = true;
   private GameTimer timer;
   int doublePass = 0;

   GameController() {
   }

   GameController(CardGameModel model, CardTableView view) {
      this.model = model;
      this.view = view;
      view.controller = this;
      timer = new GameTimer();
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


   Card findCard(int playerID, int cardIndex) {
      return model.getHand(playerID).inspectCard(cardIndex);
   }

   boolean playerStarts() {
      return view.askForStart();
   }

   /**
    * Computer plays a card and tries to match the suit,
    * if it can't will play random card
    */
   void computerPlay() {
      int[] indexes = model.lookForAMove();
      if (indexes == null) {
         doublePass++;
         playerPassed(0);
         System.err.println("Comp pass");
         return;
      }
      System.out.println("can play" + indexes[0] + " to " + indexes[1]);
      if (playCardTo(0, indexes[0], indexes[1])) doublePass = 0;
      view.updateScoreboard();
   }

   boolean playCardTo(int playerID, int cardIndex, int indexTo) {
      Card cardToPlay = model.playCard(playerID, cardIndex);
      model.addToPlayStack(cardToPlay, indexTo);
      view.addToPlayArea(playerID, cardToPlay, indexTo);
      if (cardsLeft() > 0) view.addToPlayerHand(playerID, model.dealACardTo(playerID));
      else endTheGame();
      return true;
   }

   void initView() {
      view.setupTheLayoutAndPanels();
   }

   void startTimer() {
      clockStopped = false;
      view.toggleTimerButton();
      timer.start();
   }

   void flipClockSwitch() {
      clockStopped = !clockStopped;
   }

   void playerPassed(int playerID) {
      if (doublePass == 2) {
         dealNewCardsToStacks();
         System.err.println("deal new stacks");
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

   int retrieveScore(int playerID) {
      return model.getTotalScoreOfPlayer(playerID);
   }

   int cardsLeft() {
      return model.cardsLeftInDeck();
   }

   CardButtonListener getCardListener() {
      return new CardButtonListener();
   }

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
               firstButtonIndex = -1;
            } else {
               firstButtonIndex = view.findIndexOfCard(((JToggleButton) event.getSource()).getIcon(), false);
            }
         } else if (CardButtonListener.firstButtonIndex != -1) {
            Icon stackIcon = ((JButton) event.getSource()).getIcon();
            int stackIndex = view.findIndexOfCard(stackIcon, true);
            if (stackIcon.toString().contains("BK") ||
                  model.isAValidMove(firstButtonIndex, stackIndex)) {
               playCardTo(1, firstButtonIndex, stackIndex);
               doublePass = 0;
               computerPlay();
            }
            firstButtonIndex = -1;
            view.deselectAllButtons();
         }
         view.validate();
         view.repaint();
      }
   }

   class GameTimer extends Thread {
      private int time = 0;

      private void doNothing() {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

      private void incrementTimer() {
         time++;
         int min = time / 60;
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