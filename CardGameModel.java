/**
 * Deniz Erisgen
 * Assignment 6 Phase 3
 * IDE: IntelliJ
 **/

public class CardGameModel {
   private static final int MAX_PLAYERS = 20;
   private int numPlayers = 2;
   private Card[] playedCards = new Card[numPlayers]; //cards on the table
   private int numPacks = 1;            // # standard 52-card packs per deck
   private int numJokersPerPack = 2; // if 2 per pack & 3 packs per deck, get 6
   private int numUnusedCardsPerPack;  // # cards removed from each pack
   private int numCardsPerHand = 7;        // # cards to deal each player
   private Deck deck;               // holds the initial full deck and gets
   private Hand[] handsOfPlayers; // one Hand for each player
   private int[] passCount;
   private Card[] unusedCardsPerPack;

   // constructor overload/default for game like bridge
   public CardGameModel() {
      int k;
      passCount = new int[numPlayers];
      numUnusedCardsPerPack = 0;

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
    * updates the score for playerID
    *
    * @param playerID
    */
   public void updatePassCounter(int playerID) {
      passCount[playerID]++;
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

   private boolean takeCard(int playerIndex) {
      // returns false if either argument is bad
      if (playerIndex < 0 || playerIndex > numPlayers - 1 || deck == null)
         return false;

      // Are there enough Cards?
      if (deck.getNumCards() <= 0) return false;

      return handsOfPlayers[playerIndex].takeCard(deck.dealCard());
   }

   public int getTotalScoreOfPlayer(int playerID) {
      return passCount[playerID];
   }

   public int cardsLeftInDeck() {
      return deck.getNumCards();
   }

   public Card[] cardsOnTable() {
      return playedCards;
   }
}