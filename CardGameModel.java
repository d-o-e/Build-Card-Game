/**
 * @author Deniz Erisgen ©
 **/

import java.util.Random;

class CardGameModel {
   public static final int MAX_CARD_COUNT = 7;
   public static final int NUM_PLAYERS = 2;
   private Card[] cardsOnStacks = new Card[3]; //cards on the table
   private int numPacks = 1;            // # standard 52-card packs per deck
   private int numCardsPerHand = 7;        // # cards to deal each player
   private Deck deck;               // holds the initial full deck and gets
   private Hand[] handsOfPlayers; // one Hand for each player
   private int[] passCount;

   /**
    * constructor overload/default for game like bridge
    */
   CardGameModel() {
      int k;
      passCount = new int[NUM_PLAYERS];
      handsOfPlayers = new Hand[NUM_PLAYERS];
      // allocate
      for (k = 0; k < NUM_PLAYERS; k++) handsOfPlayers[k] = new Hand();
      deck = new Deck();
      newGame();
   }

   /**
    * Initialize a new game,shuffle and deal
    */
   void newGame() {
      // restock the deck
      deck.init(numPacks);

      // shuffle the cards
      deck.shuffle();
      deal();
   }

   /**
    * Deal cards to all hands and sort them
    */
   void deal() {
      // returns false if not enough cards, but deals what it can
      int k, j;

      // clear all hands
      if (handsOfPlayers != null) {
         for (k = 0; k < NUM_PLAYERS; k++) handsOfPlayers[k].resetHand();
      }

      for (k = 0; k < numCardsPerHand; k++) {
         for (j = 0; j < NUM_PLAYERS; j++) takeCard(j);
      }

      if (handsOfPlayers != null) for (Hand hand : handsOfPlayers) hand.sort();
   }

   /**
    * updates the score for playerID
    *
    * @param playerID (int) 0 is Computer
    */
   public void updatePassCounter(int playerID) {
      passCount[playerID]++;
   }

   /**
    * Accesor for hand
    *
    * @param k hand number
    * @return hand object
    */
   Hand getHand(int k) {
      // on error return automatic empty hand
      if (k < 0 || k >= NUM_PLAYERS) {
         return new Hand();
      }
      return handsOfPlayers[k];
   }

   /**
    * Plays the card from player at cardIndex
    *
    * @param playerID  player index (int)
    * @param cardIndex card index (int)
    * @return the card at index of if not found invalid card
    */
   Card playCard(int playerID, int cardIndex) {
      // returns bad card if either argument is bad
      if (playerID < 0 || playerID > NUM_PLAYERS - 1 ||
            cardIndex < 0 || cardIndex > numCardsPerHand - 1) {
         //Creates a card that does not work
         return new Card('M', Card.Suit.spades);
      }
      // return the card played
      return handsOfPlayers[playerID].playCard(cardIndex);
   }

   /**
    * Player takes a card
    *
    * @param playerID that rep. player
    * @return true if succeeds
    */
   private boolean takeCard(int playerID) {
      // returns false if either argument is bad
      if (playerID < 0 || playerID > NUM_PLAYERS - 1 || deck == null)
         return false;

      // Are there enough Cards?
      if (deck.getNumCards() <= 0) return false;

      return handsOfPlayers[playerID].takeCard(deck.dealCard());
   }

   int getTotalScoreOfPlayer(int playerID) {
      return passCount[playerID];
   }

   int cardsLeftInDeck() {
      return deck.getNumCards();
   }

   /**
    * Adds card to table stack
    *
    * @param card    to be added
    * @param indexTo (int) index of stack
    */
   void addToPlayStack(Card card, int indexTo) {
      cardsOnStacks[indexTo] = card;
   }

   /**
    * Deal a card to player
    *
    * @param playerID (int) rep. the reciver
    * @return card dealt
    */
   Card dealACardTo(int playerID) {
      Card dealtCard = deck.dealCard();
      handsOfPlayers[playerID].takeCard(dealtCard);
      return dealtCard;
   }

   /**
    * Checks if the planned move is valid
    *
    * @param firstButtonIndex (int) index of card planning to play
    * @param stackIndex       (int) index of stack planning to place
    * @return true if it is a valid move
    */
   boolean isAValidMove(int firstButtonIndex, int stackIndex) {
      char cardOnStack = cardsOnStacks[stackIndex].getValue();
      char playedCard = getHand(1).inspectCard(firstButtonIndex).getValue();
      int stackValueIndex, cardValueIndex;
      stackValueIndex = cardValueIndex = -1;
      for (int i = 0; i < Card.valueRanks.length; i++) {
         if (cardOnStack == Card.valueRanks[i]) stackValueIndex = i;
         if (playedCard == Card.valueRanks[i]) cardValueIndex = i;
      }
      // TODO: 4/11/2022 more rules can be determined
      return gameRule(cardValueIndex, stackValueIndex);
   }

   /***
    * Principal rule of the game
    * @param cardValueIndex (int) index of card played in value array
    * @param stackValueIndex (int) index of card on stack in value array
    * @return true if conforms requirements
    */
   private boolean gameRule(int cardValueIndex, int stackValueIndex) {
      if (cardValueIndex == 0 || stackValueIndex == 0) return true; //joker
      else if (stackValueIndex == (cardValueIndex + 1) || stackValueIndex == (cardValueIndex - 1)) return true;
      else if (stackValueIndex == 13 && cardValueIndex == 1) return true;
      else return (stackValueIndex == 1 && cardValueIndex == 13);
   }

   private Card[] getComputerCardsArray() {
      Card[] compHand = new Card[handsOfPlayers[0].getNumCards()];
      for (int i = 0; i < compHand.length; i++) {
         compHand[i] = handsOfPlayers[0].inspectCard(i);
      }
      return compHand;
   }

   /**
    * Calculates value index of cards in a card array
    *
    * @param cards array
    * @return Array of ints with value index
    */
   private int[] getRankValueIndexes(Card[] cards) {
      char[] charValues = new char[cards.length];
      for (int i = 0; i < charValues.length; i++) {
         if (cards[i] == null) charValues[i] = 'X';
         else charValues[i] = cards[i].getValue();
      }

      int[] valueIndexes = new int[cards.length];
      for (int i = 0; i < valueIndexes.length; i++) {
         for (int j = 0; j < Card.valueRanks.length; j++) {
            if (Card.valueRanks[j] == charValues[i]) valueIndexes[i] = j;
         }
      }
      return valueIndexes;
   }

   /**
    * Look for a move that obey game rules
    *
    * @return int array size 2 with first:card index, second:stack index.
    * If no moves returns null
    */
   int[] lookForAMove() {
      int[] possibleMoves = new int[2];
      int[] stackIndexes = getRankValueIndexes(cardsOnStacks);
      for (int i = 0; i < stackIndexes.length; i++) {
         if (stackIndexes[i] == 0) {
            possibleMoves[0] = BuildGame.random.nextInt(getHand(0).getNumCards());
            possibleMoves[1] = i;
            return possibleMoves;
         }
      }

      int[] cardIndexes = getRankValueIndexes(getComputerCardsArray());
      for (int i = 0; i < cardIndexes.length; i++) {
         for (int j = 0; j < stackIndexes.length; j++) {
            if (gameRule(cardIndexes[i], stackIndexes[j])) {
               possibleMoves[0] = i;
               possibleMoves[1] = j;
               return possibleMoves;
            }
         }
      }
      return null;
   }

   /**
    * Deals new cards to the stacks
    */
   public void refreshCardStack() {
      if (cardsLeftInDeck() >= cardsOnStacks.length) {
         for (int i = 0; i < cardsOnStacks.length; i++) {
            cardsOnStacks[i] = deck.dealCard();
         }
      }
   }

   Card[] getCardsOnStacks() {
      Card[] stack = new Card[cardsOnStacks.length];
      System.arraycopy(cardsOnStacks, 0, stack, 0, cardsOnStacks.length);
      return stack;
   }
}

class Card {
   public static char[] valueRanks = {
         'X', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'
   };

   private char value;
   private Suit suit;
   private boolean cardError;

   /**
    * default values: value = Ace, suit = spades
    */
   public Card() {
      cardError = !(set('A', Suit.spades));
   }

   public Card(char value, Suit suit) {
      cardError = !(set(value, suit));
   }

   /**
    * deep copy constructor
    *
    * @param origCard card to be copied
    */
   public Card(Card origCard) {
      if (origCard == null) {
         cardError = true;
         return;
      }
      cardError = !(set(origCard.getValue(), origCard.getSuit()));
   }

   /**
    * Sorts the cards based on their position in rankValue array
    *
    * @param cards     array of cards
    * @param arraySize size of the array
    */
   static void arraySort(Card[] cards, int arraySize) {
      for (int i = 0; i < arraySize - 1; i++) {
         for (int j = 0; j < arraySize - i - 1; j++) {
            if (cards[j].rankValue() > cards[j + 1].rankValue()) {
               Card temp = cards[j];
               cards[j] = cards[j + 1];
               cards[j + 1] = temp;
            }
         }
      }
   }

   /**
    * @return String that represents the card
    */
   public String toString() {
      return (this.cardError) ? "[ invalid ]" : this.value + " of " + this.suit;
   }

   private boolean isValid(char value, Suit suit) {
      for (char valid : valueRanks) {
         if (valid == value) return true;
      }
      return false;
   }

   /**
    * set card values mutator
    *
    * @param value of the card
    * @param suit  of the card
    * @return true if successful
    */
   public boolean set(char value, Suit suit) {
      if (isValid(value, suit)) {
         this.value = value;
         this.suit = suit;
         return true;
      } else return false;
   }

   public Suit getSuit() {
      return suit;
   }

   public char getValue() {
      return value;
   }

   public boolean getCardError() {
      return cardError;
   }

   /**
    * checks if members have same values without any cardErrors
    *
    * @param card to check
    * @return true if successful
    */
   public boolean equals(Card card) {
      if (card == null || card.getCardError() || this.cardError) return false;
      return (this.value == card.value && this.suit == card.suit);
   }

   /**
    * Searches for cards index in valueRanks array
    *
    * @return index as Int
    */
   private int rankValue() {
      for (int i = 0; i < valueRanks.length; i++) {
         if (valueRanks[i] == this.value) return i;
      }
      return 0;
   }

   enum Suit {spades, hearts, diamonds, clubs}
}

class Hand {
   public static final int MAX_CARDS = 50; // or 100

   private Card[] myCards;
   private int numCards;

   public Hand() {
      myCards = new Card[MAX_CARDS];
      numCards = 0;
   }

   /**
    * Allocates the masterPack only ONCE
    */
   public void resetHand() {
      myCards = new Card[Hand.MAX_CARDS];
      numCards = 0;
   }

   /**
    * adds a card to the next available position in the myCards array
    *
    * @param card that will be added to myCards array
    * @return true if successful
    */
   public boolean takeCard(Card card) {
      if (numCards < MAX_CARDS) {
         myCards[numCards++] = new Card(card);
         return true;
      } else
         return false;
   }

   /**
    * returns a card from myCards array
    *
    * @param cardIndex of the card
    * @return last Card object or a null Card
    */
   public Card playCard(int cardIndex) {
      Card playedCard = new Card(myCards[cardIndex]);
      System.arraycopy(myCards, cardIndex + 1, myCards, cardIndex, numCards - cardIndex - 1);
      myCards[--numCards] = null;
      return playedCard;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder("Hand = (");
      int cardPerLine = 0;
      for (Card eachCard : myCards) {
         if (eachCard != null) {
            builder.append(' ').append(eachCard).append(',');
            cardPerLine++;
            if (cardPerLine == 5) {
               builder.append('\n');
               cardPerLine = 0;
            }

         }
      }
      builder.replace(builder.lastIndexOf(","), builder.length(), " )\n");
      return builder.toString();
   }

   /**
    * @return the number of cards
    */
   public int getNumCards() {
      return numCards;
   }

   /**
    * Checks card in myCards array
    *
    * @param k index of the card
    * @return Card or if cardError = true,
    * returns a card with error set to true
    */
   public Card inspectCard(int k) {
      return (myCards[k].getCardError()) ?
            new Card(' ', null) : new Card(myCards[k]);
   }

   public void sort() {
      Card.arraySort(myCards, numCards);
   }
}

class Deck {
   public static final int MAX_CARDS_PACK = 6;
   private static Card[] masterPack; //containing exactly 52 card references

   private Card[] cards;
   private int topCard; //index of top card = number of cards

   public Deck() {
      if (masterPack == null) allocateMasterPack();
   }

   public Deck(int numPacks) {
      if (masterPack == null) allocateMasterPack();
      init(numPacks);
   }

   /**
    * Allocates the masterPack only ONCE
    */
   private static void allocateMasterPack() {
      char[] masterCardValues = {
            'X', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'
      };
      Card.Suit[] masterSuits = Card.Suit.values();
      Card[] master = new Card[masterCardValues.length * masterSuits.length];
      int count = 0;
      for (Card.Suit masterSuit : masterSuits) {
         for (char masterCardValue : masterCardValues) {
            master[count] = new Card(masterCardValue, masterSuit);
            count++;
         }
      }
      masterPack = master;
   }

   /**
    * Mixes up the cards using random number generator.
    */
   public void shuffle() {
      Random random = new Random(System.currentTimeMillis());
      for (int i = 0; i < cards.length; i++) {
         int randomIndex = random.nextInt(cards.length);
         while (i == randomIndex) randomIndex = random.nextInt(cards.length);
         Card temp = cards[randomIndex];
         cards[randomIndex] = cards[i];
         cards[i] = temp;
      }
   }

   /**
    * @return The top card,
    * Return a card with cardError = true if no more cards in deck
    */
   public Card dealCard() {
      if (topCard == 0) return new Card(' ', null);
      Card top = new Card(inspectCard(--topCard));
      cards[topCard] = null;
      return top;
   }

   /**
    * Checks card in cards array
    *
    * @param k index of the card
    * @return a copy Card or if cardError = true,
    * returns a card with error set to true
    */
   public Card inspectCard(int k) {
      return (cards[k].getCardError()) ?
            new Card(' ', null) : new Card(cards[k]);
   }

   /**
    * Allocates the masterPack only ONCE
    *
    * @param numPacks in deck
    */
   public void init(int numPacks) {
      if (numPacks == 0) numPacks = 1;

      if (numPacks > MAX_CARDS_PACK) numPacks = 6;
      topCard = (52 * numPacks) + 4; // add spots for jokers
      cards = new Card[topCard];
      for (int i = 0; i < topCard; i++) {
         cards[i] = new Card(masterPack[(i % masterPack.length)]);
      }
   }

   /**
    * @return index of top card (number of cards)
    */
   public int getNumCards() {
      return topCard;
   }

}