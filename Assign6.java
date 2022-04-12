/**
 * Deniz Erisgen
 * Assignment 6 Phase 3
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
//      playerFirst = SuitMatch.playerStarts(); // ask user for who will start
//      if (!playerFirst) SuitMatch.computerPlay();
      SuitMatch.computerPlay();
      SuitMatch.startTimer();
   }
}