/**
 * @author Deniz Erisgen ©
 **/

import java.util.Random;

class BuildGame {
   static boolean playerFirst = false; //who goes first?
   static Random random = new Random(System.currentTimeMillis());

   public static void main(String[] args) {
      CardGameModel model = new CardGameModel();
      CardTableView view = new CardTableView();
      GameController BuildGame = new GameController(model, view);
      playerFirst = BuildGame.playerStarts(); // ask user for who will start
      if (!playerFirst) BuildGame.computerPlay();
      BuildGame.startTimer();
   }
}