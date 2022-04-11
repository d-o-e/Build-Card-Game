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
      int indexToPlay, indexToPut;
      Card[] cardsOnPlayArea = model.getCardsOnStacks();
      // TODO: 4/10/2022 computer Logical play cardIndex to be played random for now
      indexToPlay = Assign6.random.nextInt(model.getHand(0).getNumCards());
      indexToPut = Assign6.random.nextInt(3);
      playCardTo(0, indexToPlay, indexToPut);
   }

   public boolean playCardTo(int playerID, int cardIndex, int indexTo) {
      // TODO: 4/11/2022 add validation method
      if (playerID == 0) view.removeFromComputerHand();
      Card cardToPlay = model.playCard(playerID, cardIndex);
      view.addToPlayArea(cardToPlay, indexTo);
      model.getCardsOnStacks()[indexTo] = cardToPlay;
      return true;
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

   public void playerPassed(int playerID) {
      model.updatePassCounter(playerID);
   }

   public int retrieveScore(int playerID) {
      return model.getTotalScoreOfPlayer(playerID);
   }

   public int cardsLeft() {
      return model.cardsLeftInDeck();
   }

   public CardButtonListener getCardListener() {
      return new CardButtonListener();
   }

   public int playerCardsLeft(int playerID) {
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
            JButton x = (JButton) event.getSource();
            int stackIndex = view.findIndexOfCard(x.getIcon(), true);
            playCardTo(1, firstButtonIndex, stackIndex);
            firstButtonIndex = -1;
            view.deselectAllButtons();
         }
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