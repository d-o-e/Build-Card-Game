/**
 * Deniz Erisgen
 * Assignment 6 Phase 2
 * IDE: IntelliJ
 **/

public class CardGameModel {
   private static final int MAX_PLAYERS = 20;
   private int numPlayers = 2;
   Card[] playedCards = new Card[numPlayers]; //cards on the table
   private int numPacks = 1;            // # standard 52-card packs per deck
   private int numJokersPerPack = 2; // if 2 per pack & 3 packs per deck, get 6
   private int numUnusedCardsPerPack = 0;  // # cards removed from each pack
   private int numCardsPerHand = 7;        // # cards to deal each player
   Card[] winningCards = new Card[numPlayers * numCardsPerHand]; //stores matched cards
   private Deck deck;               // holds the initial full deck and gets
   private Hand[] handsOfPlayers; // one Hand for each player
   private int[] scoreboard;
   private Card[] unusedCardsPerPack;
   private boolean playerFirst = false;

   // constructor overload/default for game like bridge
   public CardGameModel() {
      int k;
      scoreboard = new int[numPlayers];
      // filter bad values
      numUnusedCardsPerPack = 0;
      // one of many ways to assure at least one full deal to all players
      numCardsPerHand = 7;

      // allocate
      this.unusedCardsPerPack = new Card[numUnusedCardsPerPack];
      handsOfPlayers = new Hand[numPlayers];
      for (k = 0; k < numPlayers; k++) handsOfPlayers[k] = new Hand();
      deck = new Deck();

      for (k = 0; k < numUnusedCardsPerPack; k++) {
         this.unusedCardsPerPack[k] = unusedCardsPerPack[k];
      }

      // prepare deck and shuffle
      deck.shuffle();
      deal();
   }

   /**
    * updates the score comparing 2 cards suits on table
    *
    * @param playerID
    */
   void updateScore(int playerID) {
      int index = 0;
      int otherID = (playerID == 1 ? 0 : 1);
      while (winningCards[index] != null) {
         index++;
         if (index >= winningCards.length) return;
      }
      if (playedCards[0].getSuit() == playedCards[1].getSuit()) {
         System.arraycopy(playedCards, 0, winningCards, index, playedCards.length);
         scoreboard[playerID]++;
      } else scoreboard[otherID]++;
   }

   public Hand getHand(int k) {
      // on error return automatic empty hand
      if (k < 0 || k >= numPlayers) {
         System.err.println("hand is empty");
         return new Hand();
      }
      return handsOfPlayers[k];
   }

   public void newGame() {
      // clear the hands
//      for (Hand hand : handsOfPlayers) hand.resetHand();

      // restock the deck
      deck.init(numPacks);

      // remove unused cards - can be checked and decrease number of cards
      for (Card unused : unusedCardsPerPack) deck.removeCard(unused);

      // add jokers
      for (int k = 0; k < numPacks; k++)
         for (int j = 0; j < numJokersPerPack; j++) {
            // can be checked and increase deck card count
            deck.addCard(new Card('X', Card.Suit.values()[j]));
         }
      // shuffle the cards
      deck.shuffle();
      deal();
   }

   public void deal() {
      // returns false if not enough cards, but deals what it can
      int k, j;

      // clear all hands
      if (handsOfPlayers != null) {
         for (j = 0; j < numPlayers; j++) handsOfPlayers[j].resetHand();
      }

      for (k = 0; k < numCardsPerHand; k++) {
         for (j = 0; j < numPlayers; j++) takeCard(j);
      }
      for (Hand hand : handsOfPlayers) hand.sort();
   }

   Card playCard(int playerIndex, int cardIndex) {
      // returns bad card if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1 ||
            cardIndex < 0 || cardIndex > numCardsPerHand - 1) {
         //Creates a card that does not work
         return new Card('M', Card.Suit.spades);
      }

      // return the card played
      return handsOfPlayers[playerIndex].playCard(cardIndex);

   }

   boolean takeCard(int playerIndex) {
      // returns false if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1 || deck == null)
         return false;

      // Are there enough Cards?
      if (deck.getNumCards() <= 0) return false;

      return handsOfPlayers[playerIndex].takeCard(deck.dealCard());
   }

   public int getTotalScoreOfPlayer(int playerID) {
      return scoreboard[playerID];
   }

   public String getWinningCardsString() {
      StringBuilder winner = new StringBuilder("");
      for (int i = 0; i < winningCards.length; i++) {
         if (winningCards[i] == null) break;
         winner.append(winningCards[i]);
         winner.append((i % 2 != 0) ? '\n' : " - ");
      }
      return winner.toString();
   }

}