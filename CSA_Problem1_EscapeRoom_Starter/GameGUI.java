import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JFrame;

import java.io.File;
import javax.imageio.ImageIO;

import java.util.Random;
import java.util.Scanner;

/**
 * A Game board on which to place and move players.
 * 
 * @author PLTW
 * @version 1.0
 */
public class GameGUI extends JComponent
{
  static final long serialVersionUID = 141L; // problem 1.4.1

  private static final int WIDTH = 510;
  private static final int HEIGHT = 360;
  private static final int SPACE_SIZE = 60;
  private static final int GRID_W = 8;
  private static final int GRID_H = 5;
  private static final int START_LOC_X = 15;
  private static final int START_LOC_Y = 15;
  
  // initial placement of player
  int x = START_LOC_X; 
  int y = START_LOC_Y;

  // grid image to show in background
  private Image bgImage;

  // player image and info
  private Image player;
  private Point playerLoc;
  private int playerSteps;

  // walls, prizes, traps
  private int totalWalls;
  private Rectangle[] walls; 
  private Image prizeImage;
  private int totalPrizes;
  private Rectangle[] prizes;
  private int totalTraps;
  private Rectangle[] traps;

  // scores, sometimes awarded as (negative) penalties
  private int prizeVal = 10;
  private int trapVal = 5;
  private int endVal = 10;
  private int offGridVal = 5; // penalty only
  private int hitWallVal = 5;  // penalty only

  // game frame
  private JFrame frame;

  /**
   * Constructor for the GameGUI class.
   * Creates a frame with a background image and a player that will move around the board.
   */
  public GameGUI()
  {
    try {
      bgImage = ImageIO.read(new File("grid.png"));      
    } catch (Exception e) {
      System.err.println("Could not open file grid.png");
    }      
    try {
      prizeImage = ImageIO.read(new File("coin.png"));      
    } catch (Exception e) {
      System.err.println("Could not open file coin.png");
    }
  
    // player image, student can customize this image by changing file on disk
    try {
      player = ImageIO.read(new File("player.png"));      
    } catch (Exception e) {
      System.err.println("Could not open file player.png");
    }
    // save player location
    playerLoc = new Point(x,y);

    // create the game frame
    frame = new JFrame();
    frame.setTitle("EscapeRoom");
    frame.setSize(WIDTH, HEIGHT);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(this);
    frame.setVisible(true);
    frame.setResizable(false); 

    // set default config
    totalWalls = 20;
    totalPrizes = 3;
    totalTraps = 5;
  }

  /**
   * After a GameGUI object is created, this method adds the walls, prizes, and traps to the gameboard.
   * Note that traps and prizes may occupy the same location.
   */
  public void createBoard()
  {
    traps = new Rectangle[totalTraps];
    createTraps();
    
    prizes = new Rectangle[totalPrizes];
    createPrizes();

    walls = new Rectangle[totalWalls];
    createWalls();
  }

  /**
   * Increment/decrement the player location by the amount designated.
   * This method checks for bumping into walls and going off the grid,
   * both of which result in a penalty.
   * <P>
   * precondition: amount to move is not larger than the board, otherwise player may appear to disappear
   * postcondition: increases number of steps even if the player did not actually move (e.g. bumping into a wall)
   * <P>
   * @param incrx amount to move player in x direction
   * @param incry amount to move player in y direction
   * @return total score change from this move
   */
  public int movePlayer(int incrx, int incry)
  {
    int newX = x + incrx;
    int newY = y + incry;
      
    // increment regardless of whether player really moves
    playerSteps++;

    // check if off grid horizontally and vertically
    if ( (newX < 0 || newX > WIDTH - SPACE_SIZE) ||
         (newY < 0 || newY > HEIGHT - SPACE_SIZE) )
    {
      System.out.println ("OFF THE GRID!");
      return -offGridVal;
    }

    // determine if a wall is in the way
    for (Rectangle r: walls)
    {
      // this rect. location
      int startX =  (int)r.getX();
      int endX  =  (int)r.getX() + (int)r.getWidth();
      int startY =  (int)r.getY();
      int endY = (int) r.getY() + (int)r.getHeight();

      // moving RIGHT, check to the right
      if ((incrx > 0) && (x <= startX) && (startX <= newX) &&
          (y >= startY) && (y <= endY))
      {
        System.out.println("A WALL IS IN THE WAY");
        return -hitWallVal;
      }
      // moving LEFT, check to the left
      else if ((incrx < 0) && (x >= startX) && (startX >= newX) &&
               (y >= startY) && (y <= endY))
      {
        System.out.println("A WALL IS IN THE WAY");
        return -hitWallVal;
      }
      // moving DOWN check below
      else if ((incry > 0) && (y <= startY) && (startY <= newY) &&
               (x >= startX) && (x <= endX))
      {
        System.out.println("A WALL IS IN THE WAY");
        return -hitWallVal;
      }
      // moving UP check above
      else if ((incry < 0) && (y >= startY) && (startY >= newY) &&
               (x >= startX) && (x <= endX))
      {
        System.out.println("A WALL IS IN THE WAY");
        return -hitWallVal;
      }     
    }

    // all is well, move player
    x += incrx;
    y += incry;

    int trapScore = 0;

    // check if the new location has a trap
    double px = x;
    double py = y;

    for (Rectangle r : traps)
    {
      if (r.getWidth() > 0 && r.contains(px, py))
      {
        System.out.println("YOU STEPPED ON A TRAP! Spring the trap? (y/n)");

        Scanner sc = new Scanner(System.in);
        String ans = sc.nextLine().trim().toLowerCase();

        if (ans.startsWith("y"))
        {
          r.setSize(0, 0);   // trap is now sprung
          System.out.println("TRAP SPRUNG! +" + trapVal + " points.");
          trapScore += trapVal;
        }
        else
        {
          r.setSize(0, 0);   // still remove the trap
          System.out.println("You ignored the trap... -" + trapVal + " points.");
          trapScore -= trapVal;
        }

        // there can only be one trap at this tile; we're done
        break;
      }
    }

    repaint();
    return trapScore;
  }

  public boolean isTrap(int newx, int newy)
  {
    double px = playerLoc.getX() + newx;
    double py = playerLoc.getY() + newy;

    for (Rectangle r: traps)
    {

      if (r.getWidth() > 0)
      {
        // if new location of player has a trap, return true
        if (r.contains(px, py))
        {
          System.out.println("A TRAP IS AHEAD");
          return true;
        }
      }
    }
    return false;
  }


  public int springTrap(int newx, int newy)
  {
    double px = playerLoc.getX() + newx;
    double py = playerLoc.getY() + newy;

    for (Rectangle r: traps)
    {
      if (r.contains(px, py))
      {
        if (r.getWidth() > 0)
        {
          r.setSize(0,0);
          System.out.println("TRAP IS SPRUNG!");
          return trapVal;
        }
      }
    }
    // no trap here, penalty
    System.out.println("THERE IS NO TRAP HERE TO SPRING");
    return -trapVal;
  }

  /**
   * Pickup a prize and score points. If no prize is in that location, this results in a penalty.
   * <P>
   * @return positive score if a location had a prize to be picked up, otherwise a negative penalty
   */
  public int pickupPrize()
  {
    double px = playerLoc.getX();
    double py = playerLoc.getY();

    for (Rectangle p: prizes)
    {
      // if location has a prize, pick it up
      if (p.getWidth() > 0 && p.contains(px, py))
      {
        System.out.println("YOU PICKED UP A PRIZE!");
        p.setSize(0,0);
        repaint();
        return prizeVal;
      }
    }
    System.out.println("OOPS, NO PRIZE HERE");
    return -prizeVal;  
  }

  /**
   * Return the numbers of steps the player has taken.
   * <P>
   * @return the number of steps
   */
  public int getSteps()
  {
    return playerSteps;
  }
  
  /**
   * Set the designated number of prizes in the game.
   */
  public void setPrizes(int p) 
  {
    totalPrizes = p;
  }
  
  /**
   * Set the designated number of traps in the game.
   */
  public void setTraps(int t) 
  {
    totalTraps = t;
  }
  
  /**
   * Set the designated number of walls in the game.
   */
  public void setWalls(int w) 
  {
    totalWalls = w;
  }

  /**
   * Reset the board to replay existing game. If the player has reached the far
   * right wall, a brand new board is generated. Otherwise, the same map is
   * replayed with prizes and traps reactivated.
   * <P>
   * @return positive score for reaching the far right wall, penalty otherwise
   */
  public int replay()
  {
    int win = playerAtEnd();  

    if (win > 0) {
      System.out.println("Starting a NEW map!");
      createBoard();
    } else {
      System.out.println("Replaying the SAME map.");
      for (Rectangle p : prizes) {
        p.setSize(SPACE_SIZE/3, SPACE_SIZE/3);
      }
      for (Rectangle t : traps) {
        t.setSize(SPACE_SIZE/3, SPACE_SIZE/3);
      }
    }

    x = START_LOC_X;
    y = START_LOC_Y;
    playerSteps = 0;
    repaint();

    return win;
  }

  /**
   * End the game, checking if the player made it to the far right wall.
   */
  public int endGame() 
  {
    int win = playerAtEnd();
  
    setVisible(false);
    frame.dispose();
    return win;
  }

  /*------------------- public methods not to be called as part of API -------------------*/

  /** 
   * For internal use and should not be called directly: Uses graphics buffer to paint board elements.
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;

    // draw grid
    g.drawImage(bgImage, 0, 0, null);

    // add (invisible) traps
    for (Rectangle t : traps)
    {
      g2.setPaint(Color.WHITE); 
      g2.fill(t);
    }

    // add prizes
    for (Rectangle p : prizes)
    {
      // picked up prizes are 0 size so don't render
      if (p.getWidth() > 0) 
      {
        int px = (int)p.getX();
        int py = (int)p.getY();
        g.drawImage(prizeImage, px, py, null);
      }
    }

    // add walls
    for (Rectangle r : walls) 
    {
      g2.setPaint(Color.BLACK);
      g2.fill(r);
    }
   
    // draw player, saving its location
    g.drawImage(player, x, y, 40,40, null);
    playerLoc.setLocation(x,y);
  }

  /*------------------- private methods -------------------*/

  /*
   * Add randomly placed prizes to be picked up.
   * Note:  prizes and traps may occupy the same location, with traps hiding prizes
   */
  private void createPrizes()
  {
    int s = SPACE_SIZE; 
    Random rand = new Random();
    for (int numPrizes = 0; numPrizes < totalPrizes; numPrizes++)
    {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);

      Rectangle r;
      r = new Rectangle((w*s + 15),(h*s + 15), 15, 15);
      prizes[numPrizes] = r;
    }
  }

  /*
   * Add randomly placed traps to the board. They will be painted white and appear invisible.
   * Note:  prizes and traps may occupy the same location, with traps hiding prizes
   */
  private void createTraps()
  {
    int s = SPACE_SIZE; 
    Random rand = new Random();
    for (int numTraps = 0; numTraps < totalTraps; numTraps++)
    {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);

      Rectangle r;
      r = new Rectangle((w*s + 15),(h*s + 15), 15, 15);
      traps[numTraps] = r;
    }
  }

  /*
   * Add walls to the board in random locations 
   */
  private void createWalls()
  {
    int s = SPACE_SIZE; 

    Random rand = new Random();
    for (int numWalls = 0; numWalls < totalWalls; numWalls++)
    {
      int h = rand.nextInt(GRID_H);
      int w = rand.nextInt(GRID_W);

      Rectangle r;
      if (rand.nextInt(2) == 0) 
      {
        // vertical wall
        r = new Rectangle((w*s + s - 5),h*s, 8,s);
      }
      else
      {
        // horizontal
        r = new Rectangle(w*s,(h*s + s - 5), s, 8);
      }
      walls[numWalls] = r;
    }
  }

  /**
   * Checks if player is at the far right of the board 
   * @return positive score for reaching the far right wall, penalty otherwise
   */
  private int playerAtEnd() 
  {
    int score;

    double px = playerLoc.getX();
    if (px > (WIDTH - 2*SPACE_SIZE))
    {
      System.out.println("YOU MADE IT!");
      score = endVal;
    }
    else
    {
      System.out.println("OOPS, YOU QUIT TOO SOON!");
      score = -endVal;
    }
    return score;
  }
}
