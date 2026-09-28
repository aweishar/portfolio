import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

//Andrew Weishar
//Program description
//Mar 15, 2024
//

public class BrickBreaker extends JPanel implements KeyListener
{
   
   private static final long serialVersionUID = 1L;
   private Sprite ball, paddle1;//, brick1, brick2;
   private ArrayList<Sprite> bricks;
   public static final int PANEL_WIDTH = 800;
   public static final int PANEL_HEIGHT = 600;
   public static final int TOTAL_LIVES = 3;
   private int score, lives, level;
   private boolean playing, gameOver;
   private String message;
   private Clip hitSound, gameEndSound, niceShotAudio;
   Image player2Image;
   
   //class constructor
   public BrickBreaker()
   {
      this.addKeyListener(this);
      this.setFocusable(true);
     
      try {
         hitSound = AudioSystem.getClip(); //initialize a sound clip object
         hitSound.open(AudioSystem.getAudioInputStream(new File("src/iceBreaking.wav"))); //use the filename for your audio
         
         gameEndSound = AudioSystem.getClip(); //initialize a sound clip object
         gameEndSound.open(AudioSystem.getAudioInputStream(new File("src/screaming.wav")));
         
         niceShotAudio = AudioSystem.getClip();
         niceShotAudio.open(AudioSystem.getAudioInputStream(new File("src/windSound.wav")));
      } catch (Exception e) {e.printStackTrace();}
 
      Image playerImage = new ImageIcon(this.getClass().getResource("snowflake.png")).getImage();
      player2Image = new ImageIcon(this.getClass().getResource("iceBlock.png")).getImage();
      Image player1Image = new ImageIcon(this.getClass().getResource("icePick.png")).getImage();
      ball = new Sprite(playerImage);
      ball.setXtoMiddle(0, PANEL_WIDTH);
      ball.setYtoMiddle(0, PANEL_HEIGHT);
      ball.setBoundaries(0, PANEL_WIDTH, 0, PANEL_HEIGHT);

      paddle1 = new Sprite(PANEL_WIDTH/2-50, PANEL_HEIGHT-35, 100, 25, 1, 1, 37, 39, 38, 40, player1Image);
      paddle1.setBoundaries(0, PANEL_WIDTH, 0, PANEL_HEIGHT);
     
      bricks = new ArrayList<Sprite>();
      level = 1;
      resetBricks(level);
      
      resetBricks(1);
     
      Timer timer = new Timer(1, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            paddle1.update_paddle_brickbreaker();
            if(playing) {
               message = "";

               //do the playing stuff... updates, collision, etc.
               //check if playing should stop and point is scored
                     //Does score value result in an end of the game
               
//               System.out.println(ball.isOffBrickBreakerPanel());

               ball.update_BrickbreakerBall();
               if(ball.isOffPanel())
               {
                  lives--;
                  playing = false;
                  if((TOTAL_LIVES + lives) == 0)
                  {
                     gameOver = true;
                  gameEndSound.setFramePosition(0);
                  gameEndSound.start();
                  }
                  else
                  {
                     ball.resetPongBall();
                     niceShotAudio.setFramePosition(0);
                     niceShotAudio.start();
                  }
               }
               
               if(ball.checkAndReactToCollisionWith(paddle1.getBounds()))
               {
//                  hitSound.setFramePosition(0);
//               hitSound.start();
               }
               for(int i = bricks.size() - 1; i >= 0; i--)
               {
                  if(ball.checkAndReactToCollisionWith(bricks.get(i).getBounds()))
                  {
                     hitSound.setFramePosition(0);
                     hitSound.start();
                     bricks.get(i).setHits(bricks.get(i).getHits() - 1);
                     if(bricks.get(i).getHits() == 0) //if # of hits = 0 remove brick
                        bricks.remove(i);
                     score++;
                  }
               }
               
               //check for clearing the level
               if(bricks.size() == 0) {
                  level++;
                  resetBricks(level);
               }
          }
          else if(!gameOver) {
               //Not playing but not over... prepare for the next "round"... put the ball in the middle of the panel
             message = "Press <Space> to serve";
          }
          else {
               //The game is over... do game over stuff
             message = "AVALANCHE! Game Over";
          }
                     
            repaint();
         }
         
      });
      timer.start();
   } //end of constructor
   
   public void resetBricks(int rows)
   {
      bricks = new ArrayList<Sprite>();
      for(int r = 0; r < rows; r++)
         for(int i = 0; i < 8; i++)
         {
            Sprite b = new Sprite(i*100, 100+r*20, 100, 20, 0, 0, 1, player2Image);
            b.setHits(level); //set # of hits to the level
            bricks.add(b);
         }
   }

   public void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D) g;
     
      //backgrond
      Image background = new ImageIcon(this.getClass().getResource("snowBackground.jpg")).getImage();
      g2.drawImage(background, 0, 0, 800, 600, this);

      ball.drawImage(g2);
      paddle1.drawImage(g2);
      
      //draw all bricks
      for(Sprite b : bricks)
         b.drawImageWithHits(g2);
     
      g2.setColor(Color.BLACK);
      g2.setFont(new Font("Super Mario 256", Font.PLAIN, 30));
      
      g2.drawString("Lives:" + (TOTAL_LIVES + lives)+"", PANEL_WIDTH*3/4, 40);
      g2.drawString("Score:" + score+"", PANEL_WIDTH/4, 40);
      g2.drawString("Level:" + level+"", PANEL_WIDTH/2, 40);

      g2.setFont(new Font("Super Mario 256", Font.PLAIN, 25));
      FontMetrics fm = g2.getFontMetrics();
      int messageWidth = fm.stringWidth(message);
      int startX = PANEL_WIDTH / 2 - messageWidth / 2;
      g2.drawString(message, startX, 180);
     // g2.drawString(volleyM, 170, 200);
   }
   
   public Dimension getPreferredSize()
   {
      return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
   }

   public static void main(String[] args)
   {
      JFrame frame = new JFrame("Brick Breaker");
      frame.add(new BrickBreaker());
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
   }

   @Override
   public void keyTyped(KeyEvent e)
   {
   }

   public void resetGame()
   {
      score = 0;
      lives = 0;
      gameOver = false;
      bricks = new ArrayList<Sprite>();
      level = 1;
      resetBricks(level);
      ball.resetPongBall();
   }
   
   @Override
   public void keyPressed(KeyEvent e)
   {
      //System.out.println(e.getKeyCode());
      paddle1.checkForPress(e.getKeyCode());
      
      if(e.getKeyCode() == KeyEvent.VK_SPACE) {
         if(gameOver) resetGame();
         else if(!playing) 
            playing = true;
    }
   }

   @Override
   public void keyReleased(KeyEvent e)
   {
      paddle1.checkForRelease(e.getKeyCode());
      ball.checkForRelease(e.getKeyCode());
   }

}
