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

public class SpaceInvaders extends JPanel implements KeyListener
{
   
   private static final long serialVersionUID = 1L;
   private Sprite laser, ship, alienLaser, extraPoints;//, brick1, brick2;
   private ArrayList<Sprite> aliens;
   public static final int PANEL_WIDTH = 800;
   public static final int PANEL_HEIGHT = 600;
   public static final int TOTAL_LIVES = 3;
   public static final int EXTRA_POINTS_X = -1000;
   private int score, lives, level, alienMoveCounter;
   private boolean playing, gameOver, laserIsFired, isCurrentAlien;
   private String message;
   private Clip hitSound, gameEndSound, niceShotAudio;
   Image alienImage, extraPointsImage, player1Image, blankLivesImage;
   
   //class constructor
   public SpaceInvaders()
   {
      this.addKeyListener(this);
      this.setFocusable(true);
     
      try {
         hitSound = AudioSystem.getClip(); //initialize a sound clip object
         hitSound.open(AudioSystem.getAudioInputStream(new File("src/hitSound.wav"))); //use the filename for your audio
         
         gameEndSound = AudioSystem.getClip(); //initialize a sound clip object
         gameEndSound.open(AudioSystem.getAudioInputStream(new File("src/gameEndSound.wav")));
         
         niceShotAudio = AudioSystem.getClip();
         niceShotAudio.open(AudioSystem.getAudioInputStream(new File("src/niceShotAudio.wav")));
      } catch (Exception e) {e.printStackTrace();}
 
      Image laserImage = new ImageIcon(this.getClass().getResource("laser.png")).getImage();
      Image alienLaserImage = new ImageIcon(this.getClass().getResource("iceberg.png")).getImage();
      player1Image = new ImageIcon(this.getClass().getResource("titanic.png")).getImage();
      blankLivesImage = new ImageIcon(this.getClass().getResource("blackWhiteTitanic.png")).getImage();
      Image extraPointsImage = new ImageIcon(this.getClass().getResource("lifebuoy.png")).getImage();
      Image alienImage = new ImageIcon(this.getClass().getResource("rowingBoat1.png")).getImage();
      this.alienImage = alienImage;
      
      lives = TOTAL_LIVES;
      isCurrentAlien = true;
      
      laser = new Sprite(PANEL_WIDTH/2-50, PANEL_HEIGHT-35, 5, 15, 0, -3, laserImage);
      laser.setBoundaries(0, PANEL_WIDTH, 0, PANEL_HEIGHT);
     
      alienLaser = new Sprite(100, 200, 25, 25, 0, 0.3, alienLaserImage);
      alienLaser.setBoundaries(0, PANEL_WIDTH, 0, PANEL_HEIGHT);

      extraPoints = new Sprite(EXTRA_POINTS_X, 10, 100, 45, 0.06, 0, extraPointsImage);
      extraPoints.setBoundaries(0, PANEL_WIDTH, 0, PANEL_HEIGHT);
      
      ship = new Sprite(PANEL_WIDTH/2-50, PANEL_HEIGHT-35, 100, 35, 0.4, 0.5, 37, 39, 38, 40, player1Image);
      ship.setBoundaries(0, PANEL_WIDTH, 0, PANEL_HEIGHT);
     
      laser.setToShip(ship);
      
      aliens = new ArrayList<Sprite>();
      level = 5;
      resetAliens(level);
      
      resetAliens(1);
     
      Timer timer = new Timer(1, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            ship.update_paddle_brickbreaker();
            if(playing) {
               message = "";

               //do the playing stuff... updates, collision, etc.
               //check if playing should stop and point is scored
                     //Does score value result in an end of the game
               extraPoints.update_alien();
               if(extraPoints.getX() > PANEL_WIDTH + 100)
                  extraPoints.setX(EXTRA_POINTS_X);
               
               if(laserIsFired)
               {
                  laser.update_space_invaders();
               }
               else
                  laser.setToShip(ship);
                  
               if(laser.isOffPanel())
               {
                  laserIsFired = false;
               }
               if(laser.getBounds().intersects(extraPoints.getBounds()))
               {
                  score+=10;
                  laserIsFired = false;
                  extraPoints.setX(EXTRA_POINTS_X);
               }
               //alien laser
               alienLaser.update_space_invaders();
               if(alienLaser.isOffPanel())
               {
                  resetAlienLaser();
               }
               if(alienLaser.getBounds().intersects(ship.getBounds()))
               {
                  lives--;
                  resetAlienLaser();
                  playing = false;
               }

               for(int i = aliens.size() - 1; i >= 0; i--)
               {
                  if(laser.getBounds().intersects(aliens.get(i).getBounds()))
                  {
                     aliens.remove(i);
                     score++;
                     laserIsFired = false;
                  }
               }
               
               alienMoveCounter++;
               if(alienMoveCounter > 20 + 2*aliens.size())
               {
                  moveAliens();
                  alienMoveCounter = 0;
               }
               
               //check for clearing the level
               if(aliens.size() == 0) {
                  level++;
                  resetAliens(level);
               }
               if(lives == 0) {
                  playing = false; 
                  gameOver = true;
                  extraPoints.setX(EXTRA_POINTS_X);
               }
               for
               (int i = aliens.size() - 1; i >= 0; i--)
               {
                  if(aliens.get(i).getY() > PANEL_HEIGHT - 35) {
                     gameOver = true;
                     playing = false;
                     extraPoints.setX(EXTRA_POINTS_X);
                  }
               }
          }
          else if(!gameOver) {
               //Not playing but not over... prepare for the next "round"... put the ball in the middle of the panel
             message = "Press <Space> to fire";
          }
          else {
             
               //The game is over... do game over stuff
             message = "Game Over";
          }
                     
            repaint();
         }
         
      });
      timer.start();
   } //end of constructor
   
   public void resetAlienLaser()
   {
      Sprite alienThatWillShoot = aliens.get((int)(Math.random()*aliens.size()));
      alienLaser.setX(alienThatWillShoot.getX());
      alienLaser.setY(alienThatWillShoot.getY());
   }
   
   public void moveAliens()
   {
      isCurrentAlien  = !isCurrentAlien;
      if(isCurrentAlien == true)
         alienImage = new ImageIcon(this.getClass().getResource
         ("rowingBoat1.png")).getImage();
      else
         alienImage = new ImageIcon(this.getClass().getResource
         ("rowingBoat2.png")).getImage();
      
      //should the aliens move directions
      boolean shouldChangeDirection = false;
      for(Sprite a: aliens)
         if(a.willHitEdge())
            shouldChangeDirection = true;
      
      //move the aliens
      for(int i = 0; i < aliens.size(); i++)
      {
         aliens.get(i).setImage(alienImage);
         if(shouldChangeDirection) 
         {
            aliens.get(i).changeHorizontal();
            aliens.get(i).moveAlienDown();
         }
      else
         aliens.get(i).update_alien();
         extraPoints.update_alien();
   }
}
   
   public void resetAliens(int rows)
   {
      for(int r = 0; r < rows; r++)
         for(int i = 0; i < 10; i++)
         {
            Sprite nextAlien = new Sprite(100+i*60, 100+r*50, 30, 20, 8, 25 , alienImage);
            nextAlien.setBoundaries(0, PANEL_WIDTH, 0, PANEL_HEIGHT);
            aliens.add(nextAlien);
         }
   }

   public void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D) g;
     
      //backgrond
      Image background = new ImageIcon(this.getClass().getResource("ocean.jpg")).getImage();
      g2.drawImage(background, 0, 0, 800, 600, this);
      
      laser.drawImage(g2);
      alienLaser.drawImage(g2);
      ship.drawImage(g2);
      extraPoints.drawImage(g2);
      
      g2.setColor(new Color(255, 255, 255, 180));
      g2.fillRect(150, 400, 100, 100);
      g2.fillRect(550, 400, 100, 100);
      
      //draw all bricks
      for(Sprite b : aliens)
         b.drawImage(g2);

      g2.drawImage(blankLivesImage, PANEL_WIDTH - 100, 20, 30, 20, this);
      g2.drawImage(blankLivesImage, PANEL_WIDTH - 50, 20, 30, 20, this);
      
      //lives remaining
      for(int i = 0; i < lives; i++)
         g2.drawImage(player1Image, PANEL_WIDTH - 50*i, 20, 30, 20, this);
     
      g2.setColor(Color.WHITE);
      g2.setFont(new Font("Super Mario 256", Font.PLAIN, 20));

      g2.drawString("Score:"+score+"", 20, 40);

      g2.setFont(new Font("Super Mario 256", Font.PLAIN, 25));
      FontMetrics fm = g2.getFontMetrics();
      int messageWidth = fm.stringWidth(message);
      int startX = PANEL_WIDTH / 2 - messageWidth / 2;
      g2.drawString(message, startX, 170);
   }
   
   public Dimension getPreferredSize()
   {
      return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
   }

   public static void main(String[] args)
   {
      JFrame frame = new JFrame("Space Invaders: Titanic Edition");
      frame.add(new SpaceInvaders());
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
   }
   
   public void resetGame()
   {
      score = 0;
      lives = TOTAL_LIVES;
      gameOver = false;
      aliens = new ArrayList<Sprite>();
      level = 5;
      resetAliens(level);
      laser.resetPongBall();
   }
   
   @Override
   public void keyTyped(KeyEvent e)
   {
   }
   
   @Override
   public void keyPressed(KeyEvent e)
   {
      //System.out.println(e.getKeyCode());
      ship.checkForPress(e.getKeyCode());
      
      if(e.getKeyCode() == 32) {
         if(gameOver) resetGame();
         else if(!playing) 
            playing = true;
         else
            laserIsFired = true;
    }
   }

   @Override
   public void keyReleased(KeyEvent e)
   {
      ship.checkForRelease(e.getKeyCode());
      laser.checkForRelease(e.getKeyCode());
   }

}
