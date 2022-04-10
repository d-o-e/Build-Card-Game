/**
 * Deniz Erisgen
 * Assignment 6 Phase 2
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
      winner.append((playerScore > computerScore) ? "Player" : "Computer");
      winner.append('\n');
      winner.append("Player : ").append(playerScore).append("   Computer : ");
      winner.append(computerScore).append('\n');
      winner.append("Matched Suits: \n");
      winner.append(model.getWinningCardsString());

      JOptionPane scoreboard = new JOptionPane(winner,
            JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION);
      JDialog dialog = scoreboard.createDialog("Game Over");
      dialog.setVisible(true);
      System.exit(0);
   }

   public CardButtonListener getListener() {
      return new CardButtonListener();
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
      Hand compHand = model.getHand(0);
      int cardIndex = Assign6.random.nextInt(compHand.getNumCards());
      if (model.playedCards[1] != null) {
         for (int i = 0; i < compHand.getNumCards(); i++) {
            if (compHand.inspectCard(i).getSuit() == model.playedCards[1].getSuit()) {
               cardIndex = i;
            }
         }
      }

      model.playedCards[0] = playCard(0, cardIndex);
      view.removeFromPlayArea(0);
      view.addToPlayArea(model.playedCards[0], 0);
      view.removeFromComputerHand(0);
      if (Assign6.playerFirst) {
         model.updateScore(0);
         if (view.getCardCountFromComputerPnl() == 0) endTheGame();
      }
      view.validate();
      view.repaint();
   }

   public Card playCard(int playerID, int cardIndex) {
      return model.playCard(playerID, cardIndex);
   }

   public void initView() {
      view.setupTheLayoutAndPanels();
   }

   public void startTimer() {
      clockStopped = false;
      view.toggleTimerButton();
      timer.start();
   }

   public void flipClockSwitch() {
      clockStopped = !clockStopped;
   }

   /**
    * Inner Action Listener class to listen for card selections
    */
   class CardButtonListener implements ActionListener {

      @Override
      public void actionPerformed(ActionEvent event) {
         JButton clickedCard = (JButton) event.getSource();
         if (clickedCard == null) return;
         int cardIndex = view.findIndexOfCard(clickedCard.getIcon());
         view.removeFromPlayArea(1);
         view.addToPlayArea(findCard(1, cardIndex), 1);
         model.playedCards[1] = model.playCard(1, cardIndex);
         view.removeFromPlayerHand(cardIndex);

         if (!Assign6.playerFirst) {
            model.updateScore(1);
            if (view.getCardCountFromPlayerPnl() == 0) endTheGame();
         }
         computerPlay();
         view.validate();
         view.repaint();
      }

   }

   class GameTimer extends Thread {
      public GameTimer() {
         super();
      }

      private void doNothing() {
         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

      @Override
      public synchronized void run() {
         while (true) {
            if (!clockStopped) view.incrementTimer();
            doNothing();
         }
      }
   }

}