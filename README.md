# Timed "Build" Game

1. Change the program into a Model-View-Controller Design Pattern. Keep the Card, Hand, and Deck classes intact. (This
   will simulate getting them from a repository and using them AS-IS)
2. Add a new part to the Suit-Match game by putting a timer on the side of the screen. It will be on a timer to update
   every second, but in order for you to still play the game, you will need to use multi- threading. (Timer class)
3. Design a new game.
4. Redraw the UML diagram so that it represents your new structure.

## Phase 1: Model-View-Controller Design Pattern

Start with your code from last week and redesign it so that it fits the Model-View-Controller design pattern. Make sure
that the game still works as it did last week. There is a sample for #4 on pg 709.

MVC Pattern stands for Model-View-Controller Pattern. This pattern is used to separate application's concerns.

- Model - Model represents an object carrying data. It can also have logic to update controller if its data changes.
- View - View represents the visualization of the data that model contains.
- Controller - Controller acts on both model and view. It controls the data flow into model object and updates the view
  whenever data changes. It keeps view and model separate.

## Phase 2: Multi threading Implementation

Multi-threading is nice to implement inside of a GUI program, so let's give it a try. Add a timer to the Suit-Match
game. It should sit on the side of the screen and count up from 0:00, updating every second. Of course the use of
multi-threading will be needed to make sure you can still play the game with the timer running.

- Display the timer box and numbers
- Create start and stop buttons to control the timer. (extra challenge: merge the two buttons into one start/stop
  button)
- Make a call to a doNothing() method that will use the sleep() method of the Thread class.
- Timer class (extends Thread) Overrides the run() method. Put all of the needed timer code in the run() method.
- You will need an actionPerformed() method in the main() class to create an object of the Timer class and call start().

Note: The method Thread.sleep can throw an InterruptedException , which is a checked exception — that is, it must be
either caught in a catch block or declared in a throws clause. The InterruptedException has to do with one thread
interrupting another thread. The book simply notes that an InterruptedException may be thrown by Thread.sleep and so
must be accounted for. The example uses a simple catch block. The class InterruptedException is in the java. lang
package and so requires no import statement.

## Phase 3: Make a new Game!

Time to create a new game called "BUILD". The timer created above will not directly impact the game, but will just be
running on the side. Here is how the game works:

1. Take turns with the computer putting a card on one of three stacks in the middle of the table.
2. Youcanputonacardthatisonevaluehigheroronevaluelower. (6ona5OR4ona5,Qona J OR T on a J, etc.)
3. After you play, you get another card from the deck in your hand.
4. The number of cards remaining in the deck should be displayed on the screen.
5. Keep going until the single deck is out of cards.
6. If you cannot play, click a button that says "I cannot play". The the computer gets a second turn. Same for you, a
   second turn if the computer cannot play. If neither of you can play, then the deck puts a new card on each of the
   three stacks in the middle of the table.
7. Display on the screen the number of "cannot plays" on the screen for both the player and the computer.
8. Whoever has the least number of "cannot plays", is the winner. Declare this at the end, when the deck is exhausted
   even though you will still have cards in your hand.
9. First use playCard() to get the card you want to play from playerIndex and at the cardIndex location. Then use the
   takeCard() to get a new card from end of the array. You will then need to reorder the labels by using setIcon().
10. The playCard() method that we added to the Hand class last week will now be used to remove the card at a location
    and slide all of the cards down one spot in the myCards array.

## Phase 4: Create the UML diagram

Redraw the UML diagram so that it represents your new structure. Be sure to clearly mark the Model portion, the View
portion, and the Controller portions.

## Submission

- Turn in one .txt file with your new phase 3 code that includes the timer from phase 2.
- No output need be included, like last week, since it is GUI.
- Draw UML diagram file.