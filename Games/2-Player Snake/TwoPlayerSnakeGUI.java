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

public class TwoPlayerSnakeGUI extends JPanel implements KeyListener
{
   public static final int PANEL_WIDTH = 800;
   public static final int PANEL_HEIGHT = 600;
   
   private Snake snake1, snake2;
   private SnakeFood food1, food2, food3;
   private boolean playing, gameOver;
   private int score;
   private String message;
   
   public TwoPlayerSnakeGUI()
   {
      this.setFocusable(true);
      this.addKeyListener(this);
      
      playing = false;
      gameOver = false;
      message = "Press <Space Bar> to Start";
         
      score = 0;
      
      Color snake1Color = new Color(0, 255, 0);
      snake1 = new Snake(400, 300, 25, 25, 38, 40, 37, 39, snake1Color);
     
      Color snake2Color = new Color(0, 0, 255);
      snake2 = new Snake(300, 300, 25, 25, 87, 83, 65, 68, snake2Color);
     
      int foodX = (int)  (int) (Math.random() * TwoPlayerSnakeGUI.PANEL_WIDTH/25)*25;
      int foodY = (int)  (int) (Math.random() * TwoPlayerSnakeGUI.PANEL_HEIGHT/25)*25;
      food1 = new SnakeFood(foodX, foodY, 25, 25, new Color(255, 0, 0, 150));
     
      foodX = (int)  (int) (Math.random() * TwoPlayerSnakeGUI.PANEL_WIDTH/25)*25;
      foodY = (int)  (int) (Math.random() * TwoPlayerSnakeGUI.PANEL_HEIGHT/25)*25;
      food2 = new SnakeFood(foodX, foodY, 25, 25, new Color(255, 0, 0, 150));
    
      foodX = (int)  (int) (Math.random() * TwoPlayerSnakeGUI.PANEL_WIDTH/25)*25;
      foodY = (int)  (int) (Math.random() * TwoPlayerSnakeGUI.PANEL_HEIGHT/25)*25;
      food3 = new SnakeFood(foodX, foodY, 25, 25, new Color(160, 103, 60, 150));
      
      Timer timer = new Timer(100, new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e)
         {
            if(playing) {
               message = "";
               snake1.update();
               snake2.update();
               //check bounds
               if(snake1.getX() > PANEL_WIDTH - 25 ||
                  snake1.getY() > PANEL_HEIGHT - 25 ||
                  snake1.getX() < 0 || 
                  snake1.getY() < 0 || 
                  snake2.getX() > PANEL_WIDTH - 25 ||
                  snake2.getY() > PANEL_HEIGHT - 25 ||
                  snake2.getX() < 0 || 
                  snake2.getY() < 0)
               {
                  playing = false;
                  gameOver = true;
               }
               //check collision with self
               if(snake1.isCollidingWithSelf() || snake2.isCollidingWithSelf())
               {
                  playing = false;
                  gameOver = true;
               }
               //check collision with food
               if(snake1.getHitBox().intersects(food1.getHitBox())) {
                  food1.update();
                  score++;
                  snake1.addBodyPart();
               }
               if(snake2.getHitBox().intersects(food1.getHitBox())) {
                  food1.update();
                  score++;
                  snake2.addBodyPart();
               }
               if(snake1.getHitBox().intersects(food2.getHitBox())) {
                  food2.update();
                  score++;
                  snake1.addBodyPart();
               }
               if(snake2.getHitBox().intersects(food2.getHitBox())) {
                  food2.update();
                  score++;
                  snake2.addBodyPart();
               }
               if(snake1.getHitBox().intersects(food3.getHitBox())) {
                  food3.update();
                  score--;
               }
               if(snake2.getHitBox().intersects(food3.getHitBox())) {
                  food3.update();
                  score--;
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
      g2.drawString(message, startX, 240);
      
      if(playing == true && gameOver == false) {
         g2.setColor(Color.YELLOW);
         g2.setFont(new Font("Super Mario 256", Font.PLAIN, 25));
         g2.drawString("Score: " + score, 25, 50);
         
         g2.setFont(new Font("Super Mario 256", Font.PLAIN, 20));
         g2.drawString("Press <P> to pause", 32, 565);
         
         food1.draw(g2);
         food2.draw(g2);
         food3.draw(g2);
      }

      if(playing == false && gameOver == false) {
         g2.setFont(new Font("Super Mario 256", Font.PLAIN, 20));
         g2.drawString("Objective: Work together to obtain the", 32, 45);
         g2.drawString("highest score possible, but be careful...", 32, 70);
         g2.drawString("You must avoid the spoiled brown apples!", 32, 95);
         
         g2.setFont(new Font("Super Mario 256", Font.PLAIN, 15));
         g2.drawString("Blue Controls: <W> <A> <S> <D>", 32, 540);
         g2.drawString("Green Controls: <Arrow Keys>", 32, 570);
      }
      
      if(playing == false && gameOver == true) {
         g2.setColor(Color.YELLOW);
         g2.setFont(new Font("Super Mario 256", Font.PLAIN, 25));
         g2.drawString("Score: " + score, 25, 50);
      }
      
      snake1.draw(g2);
      snake2.draw(g2);
   }
   
   public Dimension getPreferredSize()
   {
      return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
   }
   
   public static void main(String[] args)
   {
      JFrame frame = new JFrame("Snake Game: 2 Player");
      frame.add(new TwoPlayerSnakeGUI());
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setLocationRelativeTo(null);
      
      frame.setVisible(true);
   }

   public void resetGame()
   {
      Color snake1Color = new Color(0, 255, 0);
      snake1 = new Snake(400, 300, 25, 25, 38, 40, 37, 39, snake1Color);
      Color snake2Color = new Color(0, 0, 255);
      snake2 = new Snake(300, 300, 25, 25, 87, 83, 65, 68, snake2Color);
      message = "Press <Space Bar> to Start";
      score = 0;
      playing = false;
      gameOver = false;
      food1.update();
      food2.update();
      food3.update();
   }
   
   @Override
   public void keyTyped(KeyEvent e) {}

   @Override
   public void keyPressed(KeyEvent e)
   {
      int key = e.getKeyCode();
      System.out.println("Key pressed... " + key);
      
      snake1.keyWasPressed(key);
      snake2.keyWasPressed(key);
      if(key == 32) playing = true;  
      if(key == 'P') {
         playing = false; 
         gameOver = false;
         message = "Press <Space Bar> to Continue";
      }
         
      if(gameOver) {
         snake1.keyWasPressed(key);
         snake2.keyWasPressed(key);
            resetGame();
      }
   }

   @Override
   public void keyReleased(KeyEvent e) {}
   
}
