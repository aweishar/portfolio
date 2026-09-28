import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

//Andrew Weishar
//Program description:
//Oct 23, 2024
//

public class SnakeGUI extends JPanel implements KeyListener
{
   public static final int PANEL_WIDTH = 800;
   public static final int PANEL_HEIGHT = 600;
   
   private Snake snake;
   private SnakeFood food;
   private boolean playing, gameOver;
   private int score;
   private String message;
   
   public SnakeGUI()
   {
      this.setFocusable(true);
      this.addKeyListener(this);
      
      playing = false;
      gameOver = false;
      message = "Press <Arrow Key> to Start";
         
      score = 0;
      
      Color snakeColor = new Color(0, 255, 0);
      snake = new Snake(400, 300, 25, 25, 38, 40, 37, 39, snakeColor);
      int foodX = (int)  (int) (Math.random() * SnakeGUI.PANEL_WIDTH/25)*25;
      int foodY = (int)  (int) (Math.random() * SnakeGUI.PANEL_HEIGHT/25)*25;
      food = new SnakeFood(foodX, foodY, 25, 25, new Color(255, 0, 0, 150));
      
      Timer timer = new Timer(100, new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            if(playing) {
               message = "";
               snake.update();
               //check bounds
               if(snake.getX() > PANEL_WIDTH - 25 ||
                  snake.getY() > PANEL_HEIGHT - 25 ||
                  snake.getX() < 0 || 
                  snake.getY() < 0)
               {
                  playing = false;
                  gameOver = true;
               }
               //check collision with self
               if(snake.isCollidingWithSelf())
               {
                  playing = false;
                  gameOver = true;
               }
               //check collision with food
               if(snake.getHitBox().intersects(food.getHitBox())) {
                  food.update();
                  score++;
                  snake.addBodyPart();
               }
            }
            else if(gameOver) {
               message = "Game Over";
            } 
               {
                  repaint();  
               }
         } 
      });
      timer.start();
   }
   
   @Override
   public void paintComponent(Graphics g)
   {
      Graphics2D g2 = (Graphics2D) g;
      
      g2.setColor(Color.BLACK);
      g2.fillRect(0, 0, this.getWidth(), this.getHeight());
      
      g2.setColor(Color.YELLOW);
      g2.setFont(new Font("Super Mario 256", Font.PLAIN, 25));
      FontMetrics fm = g2.getFontMetrics();
      int messageWidth = fm.stringWidth(message);
      int startX = PANEL_WIDTH / 2 - messageWidth / 2;
      g2.drawString(message, startX, 170);
      
      g2.setColor(Color.YELLOW);
      g2.setFont(new Font("Super Mario 256", Font.PLAIN, 25));
      g2.drawString("Score: " + score, 25, 50);
      
      g2.setFont(new Font("Super Mario 256", Font.PLAIN, 20));
      g2.drawString("Press <P> to pause", 32, 550);
      
      snake.draw(g2);
      food.draw(g2);
   }
   
   public Dimension getPreferredSize()
   {
      return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
   }
   
   public static void main(String[] args)
   {
      JFrame frame = new JFrame("Snake Game: Single Player");
      frame.add(new SnakeGUI());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setLocationRelativeTo(null);
      
      frame.setVisible(true);
   }

   public void resetGame()
   {
      Color snakeColor = new Color(0, 255, 0);
      snake = new Snake(400, 300, 25, 25, 38, 40, 37, 39, snakeColor);
      message = "Press <Arrow Key> to Start";
      score = 0;
      playing = false;
      gameOver = false;
   }
   
   @Override
   public void keyTyped(KeyEvent e) {}

   @Override
   public void keyPressed(KeyEvent e)
   {
      int key = e.getKeyCode();
      System.out.println("Key pressed... " + key);
      
      snake.keyWasPressed(key);
      if(key == 38 || key == 40 || key == 37 || key == 39) playing = true;  
      if(key == 'P') {
         playing = false; 
         gameOver = false;
         message = "Press <Arrow Key> to Continue";
      }
         
      if(gameOver) {
         snake.keyWasPressed(key);
            resetGame();
      }
   }

   @Override
   public void keyReleased(KeyEvent e) {}
   
}
