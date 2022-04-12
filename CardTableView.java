/**
 * Deniz Erisgen
 * Assignment 6 Phase 3
 * IDE: IntelliJ
 **/

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CardTableView extends JFrame {
   public GameController controller;
   private final int WINDOW_WIDTH = 900;
   private final int WINDOW_HEIGHT = 540;
   private final int numCardsPerHand = CardGameModel.MAX_CARD_COUNT;
   private final int numPlayers = CardGameModel.NUM_PLAYERS;

   // CarTable Panels
   private JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea, pnlScoreBoard, pnlTimer, pnlTimeAndScore;
   //Label arrays that represent cards on window
   private JLabel[] computerLabels, scoreboardLabels;
   private JLabel timerDisplay;
   public JButton[] playedCardStacks;
   private JToggleButton[] humanCardLabels;
   private JButton timerButton, passRoundButton;

   public CardTableView() {
      controller = new GameController();
      GUICard.loadCardIcons();
      // establish main frame in which program will run
      setTitle("Suits Match Card Table");
      setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
      setResizable(false);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);

      computerLabels = new JLabel[numCardsPerHand];
      scoreboardLabels = new JLabel[(numPlayers + 1) * 2];
      playedCardStacks = new JButton[3];
      humanCardLabels = new JToggleButton[numCardsPerHand];
      passRoundButton = new JButton("PASS");

      timerDisplay = new JLabel();
      timerButton = new JButton("START");
   }

   /**
    * Displays a popup asking for input
    *
    * @return decision of user (bool) true only if user clicks OK
    */
   boolean askForStart() {
      String title = "Start Game";
      String prompt = "Would you like to start?";
      JDialog dialog = new JDialog();
      int answer = JOptionPane.showConfirmDialog(dialog, prompt, title,
            JOptionPane.YES_NO_OPTION);
      return answer == 0;
   }

   public void setupTheLayoutAndPanels() {
      setLayout(new GridBagLayout());
      GridBagConstraints gridConstraints = new GridBagConstraints();
      gridConstraints.fill = GridBagConstraints.HORIZONTAL;
      // Top JFrame : Computer Hand
      gridConstraints.gridx = 0;
      gridConstraints.gridy = 0;
      if (getTitle().isBlank()) setTitle("Card Table");

      pnlComputerHand = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnlComputerHand.setBorder(BorderFactory.createTitledBorder("Computer Hand"));
      add(pnlComputerHand, gridConstraints);

      // Middle JFrame : Playing Area
      gridConstraints.gridx = 0;
      gridConstraints.gridy = 1;
      gridConstraints.ipady = 20;
      pnlPlayArea = new JPanel(new GridLayout(1, 3));
      pnlPlayArea.setBorder(BorderFactory.createTitledBorder("Playing Area"));
      add(pnlPlayArea, gridConstraints);

      // Timer Display and Button
      pnlTimeAndScore = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnlTimer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
      pnlTimer.setBorder(BorderFactory.createTitledBorder("Time"));
      pnlTimeAndScore.add(pnlTimer);

      // Scoreboard area
      pnlScoreBoard = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
      pnlScoreBoard.setBorder(BorderFactory.createTitledBorder("Scoreboard"));
      pnlTimeAndScore.add(pnlScoreBoard);

      gridConstraints.gridx = 0;
      gridConstraints.gridy = 2;
      gridConstraints.ipady = 0;
      add(pnlTimeAndScore, gridConstraints);

      // Bottom JFrame : Player Hand
      gridConstraints.gridx = 0;
      gridConstraints.gridy = 3;
      pnlHumanHand = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnlHumanHand.setBorder(BorderFactory.createTitledBorder("Your Hand"));
      add(pnlHumanHand, gridConstraints);

      // CREATE LABELS ----------------------------------------------------
      // Getting cards from hands and creating matching UI elements
      timerDisplay = new JLabel("00 : 00");

      for (int i = 0; i < numCardsPerHand; i++) {
         computerLabels[i] = new JLabel(GUICard.getBackCardIcon());
         humanCardLabels[i] = makeToggleButtonFromCard(controller.findCard(1, i));
      }

      // initializing placeholder cards icons and labels
      for (int i = 0; i < playedCardStacks.length; i++) {
         playedCardStacks[i] = new JButton(GUICard.getBackCardIcon());
         playedCardStacks[i].addActionListener(controller.getCardListener());
      }

      // initializing Scoreboard labels
      scoreboardLabels[0] = new JLabel("Computer Passed:");
      scoreboardLabels[1] = new JLabel(String.valueOf(controller.retrieveScore(0)));
      scoreboardLabels[2] = new JLabel("Cards left:");
      scoreboardLabels[3] = new JLabel(String.valueOf(controller.cardsLeft()));
      scoreboardLabels[4] = new JLabel("Player Passed:");
      scoreboardLabels[5] = new JLabel(String.valueOf(controller.retrieveScore(1)));

      passRoundButton.addActionListener(action -> {
         controller.playerPassed(1); // 1 : is player
         updateScoreboard();
         // TODO: 4/10/2022 computer plays
      });

      // ADD LABELS TO PANELS -----------------------------------------
      timerButton.addActionListener(action -> {
         controller.flipClockSwitch();
         toggleTimerButton();
      });

      pnlTimer.add(timerButton);
//      timerDisplay.setBorder(new EmptyBorder(0, 5, 0, 0));
      pnlTimer.add(timerDisplay);

      for (JButton playedCard : playedCardStacks) pnlPlayArea.add(playedCard);

      for (JLabel scoreLabels : scoreboardLabels) pnlScoreBoard.add(scoreLabels);

      pnlScoreBoard.add(passRoundButton);

      for (JLabel computerCard : computerLabels) pnlComputerHand.add(computerCard);

      for (JToggleButton playerCard : humanCardLabels) pnlHumanHand.add(playerCard);

      pnlComputerHand.setPreferredSize(pnlComputerHand.getPreferredSize());
      pnlHumanHand.setPreferredSize(pnlHumanHand.getPreferredSize());
      // show everything to the user
      this.setVisible(true);
   }

   public JButton makeButtonFromCard(Card card) {
      JButton newStackButton = new JButton(GUICard.iconCards[GUICard.valueAsInt(card)][GUICard.suitAsInt(card)]);
      newStackButton.addActionListener(controller.getCardListener());
      return newStackButton;
   }

   public JToggleButton makeToggleButtonFromCard(Card card) {
      JToggleButton newCardButton = new JToggleButton(GUICard.iconCards[GUICard.valueAsInt(card)][GUICard.suitAsInt(card)]);
      newCardButton.addActionListener(controller.getCardListener());
      return newCardButton;
   }

   public void addToPlayArea(int playerID, Card card, int index) {
      if (pnlPlayArea == null) return;
      JButton playedCard = makeButtonFromCard(card);
      if (playerID == 0) removeFromComputerHandPanel();
      else removeFromPlayerHand(findIndexOfCard(playedCard.getIcon(), false));

      pnlPlayArea.remove(index);
      pnlPlayArea.add(playedCard, index);
      playedCardStacks[index] = playedCard;
   }

   public void addToPlayerHand(int playerID, Card deal) {
      if (playerID == 0) {
         pnlComputerHand.add(new JButton(GUICard.getBackCardIcon()));
      } else {
         JToggleButton newCard = makeToggleButtonFromCard(deal);
         pnlHumanHand.add(newCard, numCardsPerHand - 1);
         humanCardLabels[numCardsPerHand - 1] = newCard;
         updateScoreboard();
      }
   }

   public void removeFromComputerHandPanel() {
      pnlComputerHand.remove(computerLabels.length - 1);
      computerLabels[computerLabels.length - 1] = null;
   }

   public void removeFromPlayerHand(int index) {
      pnlHumanHand.remove(index);
      humanCardLabels[index] = null;
      System.arraycopy(humanCardLabels, index + 1, humanCardLabels, index, humanCardLabels.length - index - 1);
      humanCardLabels[humanCardLabels.length - 1] = null;
   }

   /**
    * Searches for cards icon in human buttons array
    *
    * @param cardIcon      icon of the card that was selected
    * @param searchInStack pass in true to seach in stack
    * @return index of the card, if not found returns -1
    */
   int findIndexOfCard(Icon cardIcon, boolean searchInStack) {
      if (searchInStack) {
         for (int i = 0; i < playedCardStacks.length; i++) {
            if (playedCardStacks[i].getIcon().equals(cardIcon)) {
               return i;
            }
         }
      } else {
         for (int i = 0; i < humanCardLabels.length; i++) {
            if (humanCardLabels[i].getIcon().equals(cardIcon)) {
               return i;
            }
         }
      }
      return -1;
   }

   public void toggleTimerButton() {
      timerButton.setText(timerButton.getText().equals("START") ? "STOP" : "START");
      timerButton.validate();
      timerButton.repaint();
   }

   public void updateScoreboard() {
      JLabel computerPassCount = (JLabel) pnlScoreBoard.getComponent(1);
      JLabel cardsLeftInTheDeck = (JLabel) pnlScoreBoard.getComponent(3);
      JLabel playerPassCount = (JLabel) pnlScoreBoard.getComponent(5);

      computerPassCount.setText(String.valueOf(controller.retrieveScore(0)));
      cardsLeftInTheDeck.setText(String.valueOf(controller.cardsLeft()));
      playerPassCount.setText(String.valueOf(controller.retrieveScore(1)));
   }

   public void deselectAllButtons() {
      if (humanCardLabels != null) {
         for (int i = 0; i < controller.playerCardsLeft(1); i++) {
            humanCardLabels[i].setSelected(false);
         }
      }
   }

   public void updateTimer(String timerDuration) {
      timerDisplay.setText(timerDuration);
   }

   static class GUICard {
      // card Icons, A thru K + joker
      private static final Icon[][] iconCards = new ImageIcon[14][4];
      static boolean iconsLoaded = false;
      private static Icon iconBack;

      public GUICard() {
         if (!iconsLoaded) loadCardIcons();
      }

      /**
       * Reads all the cards in images folder to iconCards 2D array
       */
      static void loadCardIcons() {
         File[] iconFiles = new File("images/").listFiles();
         if (iconFiles == null) {
            iconsLoaded = false;
            return;
         }
         for (int i = 0; i < iconFiles.length; i++) {
            if (iconFiles[i].getName().equals("BK.gif")) {
               iconBack = new ImageIcon("images/" + iconFiles[i].getName());
               while (i < iconFiles.length - 1) {
                  iconFiles[i] = iconFiles[i + 1];
                  i++;
               }
               break;
            }
         }

         for (int row = 0, column = 0, iconCount = 0; ; column++) {
            if (column == 4) {
               row++;
               column = 0;
            }
            iconCards[(row)][(column)] =
                  new ImageIcon("images/" + iconFiles[iconCount++].getName());
            if (iconCount == iconFiles.length - 1) break;
         }
         iconsLoaded = true;
      }

      static public Icon getBackCardIcon() {
         return new ImageIcon(iconBack.toString());
      }

      /**
       * Calculates suit value of a card
       *
       * @param card evaluated
       * @return int value of card's suit, joker returns -1
       */
      private static int suitAsInt(Card card) {
         if (card.getSuit() == Card.Suit.clubs) return 0;
         else if (card.getSuit() == Card.Suit.diamonds) return 1;
         else if (card.getSuit() == Card.Suit.hearts) return 2;
         else if (card.getSuit() == Card.Suit.spades) return 3;
         else return -1;
      }

      /**
       * Calculates value of a card
       *
       * @param card evaluated
       * @return int index value of card
       */
      private static int valueAsInt(Card card) {
         if (card == null) return -1;
         if (Character.isDigit(card.getValue()))
            return (Character.getNumericValue(card.getValue()) - 2);
         else return switch (card.getValue()) {
            case 'X' -> 13;
            case 'A' -> 8;
            case 'K' -> 10;
            case 'Q' -> 11;
            case 'J' -> 9;
            case 'T' -> 12;
            default -> -1;
         };
      }

   }

}