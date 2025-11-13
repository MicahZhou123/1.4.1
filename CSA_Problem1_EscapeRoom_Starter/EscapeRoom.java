/*
* Problem 1: Escape Room
* 
* V1.0
* 10/10/2019
* Copyright(c) 2019 PLTW to present. All rights reserved
*/
import java.util.Scanner;

/**
 * Create an escape room game where the player must navigate
 * to the other side of the screen in the fewest steps, while
 * avoiding obstacles and collecting prizes.
 */
public class EscapeRoom
{

      // describe the game with brief welcome message
      // determine the size (length and width) a player must move to stay within the grid markings
      // Allow game commands:
      //    right, left, up, down: if you try to go off grid or bump into wall, score decreases
      //    jump over 1 space: you cannot jump over walls
      //    if you land on a trap, spring a trap to increase score: you must first check if there is a trap, if none exists, penalty
      //    pick up prize: score increases, if there is no prize, penalty
      //    help: display all possible commands
      //    end: reach the far right wall, score increase, game ends, if game ended without reaching far right wall, penalty
      //    replay: shows number of player steps and resets the board, you or another player can play the same board
      // Note that you must adjust the score with any method that returns a score
      // Optional: create a custom image for your player use the file player.png on disk
    
      /**** provided code:
      // set up the game
      boolean play = true;
      while (play)
      {
        // get user input and call game methods to play 
        play = false;
      }
      */

  public static void main(String[] args) 
  {      
    // welcome message
    System.out.println("Welcome to EscapeRoom!");
    System.out.println("Get to the other side of the room, avoiding walls and invisible traps,");
    System.out.println("pick up all the prizes.\n");
    
    GameGUI game = new GameGUI();
    game.createBoard();

    // size of move
    int m = 60; 
    // individual player moves
    int px = 0;
    int py = 0; 
    
    int score = 0;

    Scanner in = new Scanner(System.in);
    String[] validCommands = { "right", "left", "up", "down", "r", "l", "u", "d",
    "jump", "jr", "jumpleft", "jl", "jumpup", "ju", "jumpdown", "jd",
    "pickup", "p", "trap", "t", "quit", "q", "replay", "help", "?"};
  

boolean play = true;
while (play) 
{
  String cmd = UserInput.getValidInput(validCommands).toLowerCase();

  if (cmd.indexOf("right") == 0 || cmd.equals("r")) 
  {
    score++; 
    score += game.movePlayer(60, 0);
  } 
  else if (cmd.indexOf("left") == 0 || cmd.equals("l")) 
  {
    score++;
    score += game.movePlayer(-60, 0);
  } 
  else if (cmd.indexOf("up") == 0 || cmd.equals("u")) 
  {
    score++;
    score += game.movePlayer(0, -60);
  } 
  else if (cmd.indexOf("down") == 0 || cmd.equals("d")) 
  {
    score++;
    score += game.movePlayer(0, 60);
  }
  
  else if (cmd.indexOf("jump") == 0 || cmd.indexOf("jr") == 0) 
  {
    score++;  
    if (cmd.indexOf("left") > 0 || cmd.equals("jl")) 
    {
      score += game.movePlayer(-120, 0);
    } 
    else if (cmd.indexOf("up") > 0 || cmd.equals("ju")) 
    {
      score += game.movePlayer(0, -120);
    } 
    else if (cmd.indexOf("down") > 0 || cmd.equals("jd")) 
    {
      score += game.movePlayer(0, 120);
    } 
    else 
    {
      score += game.movePlayer(120, 0);
    }
  }
  else if (cmd.indexOf("pickup") == 0 || cmd.equals("p")) 
  {
    score += game.pickupPrize();
  }
  else if (cmd.indexOf("trap") == 0 || cmd.equals("t"))
  {
    score += game.springTrap(0, 0);
  }
  else if (cmd.indexOf("replay") == 0) 
  {
    System.out.println("=== REPLAY ===");
    System.out.println("Your score this round: " + score);
    System.out.println("Your step count: " + game.getSteps());
    System.out.println("Resetting the game...");

    score = 0;

    score += game.replay();
  }
  else if (cmd.indexOf("help") == 0 || cmd.indexOf("?") == 0) 
  {
    System.out.println("Commands:");
    System.out.println("  right/left/up/down or r/l/u/d - move one space");
    System.out.println("  jr/jl/ju/jd                   - jump two spaces");
    System.out.println("  pickup or p                   - pick up a prize");
    System.out.println("  trap or t                     - spring a trap on this space");
    System.out.println("  replay                        - reset and replay");
    System.out.println("  quit or q                     - quit the game");
  }
  else if (cmd.indexOf("quit") == 0 || cmd.equals("q")) 
  {
    play = false;
  }
  else 
  {
    System.out.println("Invalid command.");
    score -= 1;
  }
}

    
    score += game.endGame();

    System.out.println("score=" + score);
    System.out.println("steps=" + game.getSteps());
  }
}
