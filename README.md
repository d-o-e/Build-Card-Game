# Timed "Build" Game
- Model - Model represents an object carrying data. It can also have logic to update controller if its data changes.
- View - View represents the visualization of the data that model contains.
- Controller - Controller acts on both model and view. It controls the data flow into model object and updates the view
  whenever data changes. It keeps view and model separate.

## Here is how the game works:

- Take turns with the computer putting a card on one of three stacks in the middle of the table.
- You can put on a card that is one value higher or one value lower. (6 on a 5, 4 on a 5, A on a K, JOKER goes
  everywhere)
- After you play, you get another card from the deck in your hand.
- The number of cards remaining in the deck should be displayed on the screen.
- Keep going until the single deck is out of cards.
- If you cannot play, click a button that says "Pass". The computer gets a second turn. Same for you, a
  second turn if the computer cannot play. If neither of you can play, then the deck puts a new card on each of the
  three stacks in the middle of the table.
- Display on the screen the number of "passes" on the screen for both the player and the computer.
- Whoever has the least number of "passes", is the winner. Declare this at the end, when the deck is exhausted
  even though you will still have cards in your hand.