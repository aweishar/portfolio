import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

//Andrew Weishar
//Program description: This is a class for testing the Sprite class for a PLATFORMER game
//Jun 11, 2025
//

public class SpriteTester extends JPanel implements KeyListener
{
   private static final long serialVersionUID = 1L;
   
   //Define the panel dimensions
   public static final int PREF_W = 320;
   public static final int PREF_H = 480;
  
   //Rendering hints are for smoothing the draw if needed
   private RenderingHints hints = new RenderingHints(
                                      RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON);
   //I've gotta have Cooper Black
   private Font font = new Font("Cooper Black", Font.PLAIN, 25);
   
   private Sprite player;
   private Platforms platforms;
   private int score, hiScore;
   private boolean playing, gameOver;
   
   //CONSTRUCTOR
   public SpriteTester()
   {
      setBackground(Color.BLACK);
      addKeyListener(this);
      setFocusable(true);
      
      platforms = new Platforms();
      platforms.setLevel1();
      
      player = new Sprite(platforms);
      player.setX(PREF_W / 2);
      player.setY(PREF_H - 100);
      
      //The main game timer
      new Timer(10, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            update();
            repaint();
         }
      }).start();
   }
   
   //The update method that is called by the timer
   public void update()
   {  
      if(playing) {
        
         //setting the score and high score
         score++;
         if(score / 10 > hiScore)
            hiScore = score / 10;
            
         //Update the sprite location and image
         platforms.updateV();
         platforms.updateH();
         player.update();
         
         //Update the world scrolling (if player jumps more than half screen, scroll down)
         if(player.getY() < PREF_H / 2) {
            double dy = player.getDy();
            player.setY(PREF_H / 2); //sets the sprite to a location
            for(Rectangle r : platforms.getPlatforms())
               r.y += -dy;
         }
         
         //sprite falls out of frame
         if(player.getY() > SpriteTester.PREF_H) {
            playing = false;
            gameOver = true;
            }
         }
   }
   
   public void resetGame()
   {
      playing = true;
      gameOver = false;
      score = 0;
      
      platforms.setLevel1();
      
      player.setX(PREF_W / 2);
      player.setY(PREF_H - 100);
      player.setDx(0);
      player.setDy(0);
   }

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHints(hints);

      g2.setFont(font);
      FontMetrics fm = g2.getFontMetrics();
      g2.setColor(Color.RED);
//      int width = fm.stringWidth("Disco Jump");
//      g2.drawString("Disco Jump", getWidth()/2 - width / 2, 30);
      
//      g2.setColor(Color.CYAN);
//      g2.fillRect(0, 0, getWidth(), getHeight()); 
      
      g2.setColor(Color.WHITE);
      Font font = new Font("Cooper Black", Font.PLAIN, 15);
      g2.setFont(font);
      g2.drawString("Score: " + score / 10, 5, 39);
      g2.drawString("HIGH SCORE: " + hiScore, 5, 20);
      
      //draw the platforms
      platforms.draw(g2);
      
      //draw the player
      player.draw(g2);
      
      if(!playing && !gameOver) {
         g2.setColor(Color.WHITE);
         font = new Font("Cooper Black", Font.BOLD, 20);
         g2.setFont(font);
         g2.drawString("Press <UP_KEY> to start", PREF_W / 2 - 125, PREF_H / 2 + 60);
      }
      if(gameOver) {
         g2.setColor(Color.WHITE);
         font = new Font("Cooper Black", Font.BOLD, 20);
         g2.setFont(font);
         g2.drawString("Press <UP_KEY> to restart", PREF_W / 2 - 135, PREF_H / 2 + 60);
      }
   }

   //KEYLISTENER METHODS
   
   @Override
   public void keyPressed(KeyEvent e)
   {
      int key = e.getKeyCode();
      
      if(key == KeyEvent.VK_LEFT) {
         player.setLeft(true);
      }
      if(key == KeyEvent.VK_RIGHT) {
         player.setRight(true);
      }
//      if(key == KeyEvent.VK_UP) {
//         player.setJumping(true);
//      }
      if(key == KeyEvent.VK_UP) 
         if(gameOver) resetGame();
         else if(!playing) playing = true;
   }

   @Override
   public void keyReleased(KeyEvent e)
   {
      int key = e.getKeyCode();
      
      if(key == KeyEvent.VK_LEFT) {
         player.setLeft(false);
      }
      if(key == KeyEvent.VK_RIGHT) {
         player.setRight(false);
      }
   }

   @Override
   public void keyTyped(KeyEvent e){}
   
//********** METHODS FOR CREATING THE JPANEL AND JFRAME **********
   
   public Dimension getPreferredSize() {
      return new Dimension(PREF_W, PREF_H);
   }

   private static void createAndShowGUI() {
      JFrame frame = new JFrame("Disco Jump");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //Add the game panel to the frame
      frame.getContentPane().add(new SpriteTester());
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }

   public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGUI();
         }
      });
   }
}
