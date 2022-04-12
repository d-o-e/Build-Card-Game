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

   public GameController() {
   }

   public GameController(CardGameModel model, CardTableView view) {
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
      StringBuilder winner = new StringBuilder("Winner is ");
      winner.append((playerScore > computerScore) ? "Player" : "Computer")
            .append('\n').append("Player : ").append(playerScore)
            .append(" Computer : ").append(computerScore).append('\n');
      JOptionPane scoreboard = new JOptionPane(winner,
            JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
      JDialog dialog = scoreboard.createDialog("Game Over");
      dialog.setVisible(true);
      System.exit(0);
   }


   public Card findCard(int playerID, int cardIndex) {
      return model.getHand(playerID).inspectCard(cardIndex);
   }

   boolean playerStarts() {
      return view.askForStart();
   }

   /**
    * Computer plays a card and tries to match the suit,
    * if it can't will play random card
    */
   public void computerPlay() {
      int[] indexes = model.lookForAMove();
      System.out.println("can play" + indexes[0] + " to " + indexes[1]);
      // TODO: 4/10/2022 computer Logical play cardIndex to be played random for now
      if (indexes == null) model.updatePassCounter(0);
      playCardTo(0, indexes[0], indexes[1]);

   }

   public boolean playCardTo(int playerID, int cardIndex, int indexTo) {
      Card cardToPlay = model.playCard(playerID, cardIndex);
      model.getCardsOnStacks()[indexTo] = cardToPlay;
      view.addToPlayArea(playerID, cardToPlay, indexTo);
      if (cardsLeft() > 0) view.addToPlayerHand(playerID, model.dealACardTo(playerID));
      return true;
   }

   public void initView() {
      view.setupTheLayoutAndPanels();
   }

   void startTimer() {
      clockStopped = false;
      view.toggleTimerButton();
      timer.start();
   }

   public void flipClockSwitch() {
      clockStopped = !clockStopped;
   }

   void playerPassed(int playerID) {
      model.updatePassCounter(playerID);
   }

   int retrieveScore(int playerID) {
      return model.getTotalScoreOfPlayer(playerID);
   }

   public int cardsLeft() {
      return model.cardsLeftInDeck();
   }

   public CardButtonListener getCardListener() {
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