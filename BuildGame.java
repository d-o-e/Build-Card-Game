/**
 * @author Deniz Erisgen ©
 **/

import java.util.Random;

class BuildGame {
   static boolean playerFirst; //who goes first?
   static Random random = new Random(System.currentTimeMillis());

   public static void main(String[] args) {
      CardGameModel model = new CardGameModel();
      CardTableView view = new CardTableView();
      GameController controller = new GameController(model, view);
	   BuildGame.playerFirst = controller.playerStarts(); // ask user for who will start
      if (!BuildGame.playerFirst) controller.computerPlay();
      controller.startTimer();
   }
}