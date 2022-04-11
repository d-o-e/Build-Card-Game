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
      int indexToPlay, indexToPut = 0;
      Hand compHand = model.getHand(0);
      Card[] playAreaCards = model.cardsOnTable();
      // cardIndex to be played random for now
      indexToPlay = Assign6.random.nextInt(compHand.getNumCards());
      indexToPut = Assign6.random.nextInt(2);
      // TODO: 4/10/2022 computer Logical play
      playAreaCards[indexToPut] = playCard(0, indexToPlay);
      view.addToPlayArea(compHand.playCard(indexToPlay), indexToPut);
      view.removeFromComputerHand(0);
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

   /**
    * Inner Action Listener class to listen for card selections
    */
   class CardButtonListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent event) {
         // TODO: 4/10/2022 design a way that it works with stacks and cards
         // if it's a toggle button
         if (event.getSource() instanceof JToggleButton) System.out.println("TTTTTTTTTTT");
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