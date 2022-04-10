/**
 * Deniz Erisgen
 * Assignment 6 Phase 2
 * IDE: IntelliJ
 **/

import java.util.Random;

class Assign6 {
   static boolean playerFirst = false; //who goes first?
   static Random random = new Random(System.currentTimeMillis());

   public static void main(String[] args) {
      CardGameModel model = new CardGameModel();
      CardTableView view = new CardTableView();
      GameController SuitMatch = new GameController(model, view);
      SuitMatch.initView();
      playerFirst = SuitMatch.playerStarts(); // ask user for who will start
      if (!playerFirst) SuitMatch.computerPlay();
      SuitMatch.startTimer();
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
      if (numCards == 0 || cardIndex > numCards
            || inspectCard(cardIndex).getCardError()) {
         return new Card('M', Card.Suit.spades);
      }
      Card playedCard = new Card(myCards[cardIndex]);
      System.arraycopy(myCards, cardIndex + 1, myCards, cardIndex, numCards - cardIndex);
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
      init(1);
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

   public boolean addCard(Card card) {
      int count = 0;
      for (Card tempCard : cards) {
         if (tempCard.equals(card)) count++;
      }
      if (count > MAX_CARDS_PACK) return false;
      else cards[--topCard] = new Card(card);
      return true;
   }

   public boolean removeCard(Card card) {
      for (Card tempCard : cards) {
         if (tempCard.equals(card)) {
            cards[topCard--] = null;
            return true;
         }
      }
      return false;
   }

}