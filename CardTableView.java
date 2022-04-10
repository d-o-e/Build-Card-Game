import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CardTableView extends JFrame {
   public GameController controller;
   private final int WINDOW_WIDTH = 900;
   private final int WINDOW_HEIGHT = 520;
   private final int numCardsPerHand = 7;
   private final int numPlayers = 2;

   // CarTable Panels
   private JPanel pnlComputerHand, pnlHumanHand, pnlPlayArea, pnlScoreBoard, pnlTimer, pnlTimeAndScore;
   //Label arrays that represent cards on window
   private JLabel[] computerLabels;
   private JLabel[] scoreboardLabels;
   private JLabel timerDisplay;
   private JButton[] humanLabels, playedCardLabels;
   private JButton timerButton, passRoundButton;
   private int time = 0;

   public CardTableView() {

      controller = new GameController();
      GUICard.loadCardIcons();
      // establish main frame in which program will run
      setTitle("Suits Match Card Table");
      setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
//      setResizable(false);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLocationRelativeTo(null);

      computerLabels = new JLabel[numCardsPerHand];
      scoreboardLabels = new JLabel[(numPlayers + 1) * 2];
      playedCardLabels = new JButton[3];
      humanLabels = new JButton[numCardsPerHand];
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
         humanLabels[i] = makeButtonFromCard(controller.findCard(1, i));
         humanLabels[i].addActionListener(controller.getListener());
      }

      // initializing placeholder cards icons and labels
      for (int i = 0; i < playedCardLabels.length; i++) {
         playedCardLabels[i] = new JButton(GUICard.getBackCardIcon());
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
      });

      // ADD LABELS TO PANELS -----------------------------------------
      timerButton.addActionListener(action -> {
         controller.flipClockSwitch();
         toggleTimerButton();
      });
      pnlTimer.add(timerButton);
//      timerDisplay.setBorder(new EmptyBorder(0, 5, 0, 0));
      pnlTimer.add(timerDisplay);

      for (JButton playedCard : playedCardLabels) pnlPlayArea.add(playedCard);

      for (JLabel scoreLabels : scoreboardLabels) pnlScoreBoard.add(scoreLabels);

      pnlScoreBoard.add(passRoundButton);

      for (JLabel computerCard : computerLabels) pnlComputerHand.add(computerCard);

      for (JButton playerCard : humanLabels) pnlHumanHand.add(playerCard);

      pnlComputerHand.setPreferredSize(pnlComputerHand.getPreferredSize());
      pnlHumanHand.setPreferredSize(pnlHumanHand.getPreferredSize());
      // show everything to the user
      this.setVisible(true);
   }

   public JButton makeButtonFromCard(Card card) {
      return new JButton(GUICard.iconCards[GUICard.valueAsInt(card)][GUICard.suitAsInt(card)]);
   }

   public void addToPlayArea(Card card, int index) {
      if (pnlPlayArea == null) return;
      pnlPlayArea.remove(index);
      pnlPlayArea.add(makeButtonFromCard(card), index);
   }

   public void removeFromComputerHand(int index) {
      pnlComputerHand.remove(index);
   }

   public void removeFromPlayerHand(int index) {
      pnlHumanHand.remove(index);
   }

   /**
    * Searches for cards icon in human buttons array
    *
    * @param cardIcon icon of the card that was selected
    * @return index of the card, if not found returns -1
    */
   int findIndexOfCard(Icon cardIcon) {
      int foundIndex = -1;
      for (int i = 0; i < humanLabels.length; i++) {
         if (humanLabels[i].getIcon().equals(cardIcon)) {
            foundIndex = i;
            System.arraycopy(humanLabels, i + 1, humanLabels, i, humanLabels.length - foundIndex - 1);
            humanLabels[humanLabels.length - 1] = null;
            return foundIndex;
         }
      }
      return foundIndex;
   }

   public void toggleTimerButton() {
      timerButton.setText(timerButton.getText().equals("START") ? "STOP" : "START");
      timerButton.validate();
      timerButton.repaint();
   }

   public void incrementTimer() {
      time++;
      int min = time / 60;
      int seconds = time - (60 * min);
      String timerDuration = String.format("%02d", min) + " : " + String.format("%02d", seconds);
      timerDisplay.setText(timerDuration);
      timerDisplay.validate();
      timerDisplay.repaint();
   }

   public void updateScoreboard() {
      JLabel computerPassCount = (JLabel) pnlScoreBoard.getComponent(1);
      JLabel cardsLeftInTheDeck = (JLabel) pnlScoreBoard.getComponent(3);
      JLabel playerPassCount = (JLabel) pnlScoreBoard.getComponent(5);

      computerPassCount.setText(String.valueOf(controller.retrieveScore(0)));
      cardsLeftInTheDeck.setText(String.valueOf(controller.cardsLeft()));
      playerPassCount.setText(String.valueOf(controller.retrieveScore(1)));
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