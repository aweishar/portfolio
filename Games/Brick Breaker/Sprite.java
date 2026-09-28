//import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

//Andrew Weishar
//Program description:
//Mar 26, 2024
//

public class Sprite
{
   //define variables to represent our object... a sprite in this case
   private double x, y, dx, dy;
   private int w, h, hits;
   private Image image;
   private int leftKey, rightKey, upKey, downKey;
   private boolean right, left, up, down;
   private boolean isFacingRight;
   private int maxX, minX, maxY, minY;
  
   //first constructor to initialize the variables
   public Sprite(Image image)
   {
      x = 0;
      y = 0;
      w = 35;
      h = 25;
      dx = 0.5;
      dy = -0.5;
      this.image  = image;
      
      isFacingRight = true;
   }
   
 //second constructor for key movement
   public Sprite(int leftKey, int rightKey, int upKey, int downKey, Image image)
   {
      x = 0;
      y = 0;
      w = 35;
      h = 25;
      dx = 1;
      dy = 1;
      this.image  = image;
      
      this.leftKey = leftKey;
      this.rightKey = rightKey;
      this.upKey = upKey;
      this.downKey = downKey;
      
      isFacingRight = true;
   }
  
   //third constructor for key movement and other values
   public Sprite(int x, int y, double dx, double dy, int leftKey, int rightKey, int upKey, int downKey, Image image)
   {
      this.x = x;
      this.y = y;
      w = 25;
      h = 100;
      this.dx = dx;
      this.dy = dy;
      this.image  = image;
      
      this.leftKey = leftKey;
      this.rightKey = rightKey;
      this.upKey = upKey;
      this.downKey = downKey;
      
      isFacingRight = true;
   }
   
   public Sprite(int x, int y, int w, int h, double dx, double dy, int leftKey, int rightKey, int upKey, int downKey, Image image)
   {
      this.x = x;
      this.y = y;
      
      this.w = w;
      this.h = h;
      
      this.dx = dx;
      this.dy = dy;
      this.image  = image;
      
      this.leftKey = leftKey;
      this.rightKey = rightKey;
      this.upKey = upKey;
      this.downKey = downKey;
      
      isFacingRight = true;
   }
   
   //a brick constructor (and other things, but brick for now)
   public Sprite(int x, int y, int w, int h, double dx, double dy, Image image)
   {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.dx = dx;
      this.dy = dy;
      this.image = image;
   }
   
   public Sprite(int x, int y, int w, int h, double dx, double dy, int hits, Image image)
   {
      this.x = x;
      this.y = y;
      this.w = w;
      this.h = h;
      this.dx = dx;
      this.dy = dy;
      this.hits = hits;
      this.image = image;
   }
   
   public void update()
   {
      x+=dx;
      if(x > PongGame.PANEL_WIDTH)
         x = -w;
      if(x < -w)
         x = PongGame.PANEL_WIDTH;
      
      y+=dy;
      if(y > PongGame.PANEL_HEIGHT)
         y = -h;
      if(y < -h)
         y = PongGame.PANEL_HEIGHT;
   }
 
   public void update_bounce()
   {
      if(x > PongGame.PANEL_WIDTH - w)
      {
         dx = -dx;
         isFacingRight = false;
      }
      if(x < 0)
      {
         dx = -dx;
         isFacingRight = true;
      }
      if(y > PongGame.PANEL_HEIGHT - h)
         dy = -dy;
      if(y < 0)
         dy = -dy;
      
      x+=dx;
      y+=dy;
   }
   
   
   public void update_pong_bounce()
   {
      if(y > PongGame.PANEL_HEIGHT - h)
         dy = -dy;
      if(y < 0)
         dy = -dy;
      
      x+=dx;
      y+=dy;
   }
   
   public void update_space_invaders()
   {
      y += dy;
   }
   
   public void bounceUpAndDown()
   {
      if(y > PongGame.PANEL_HEIGHT - h)
         dy = -dy;
      if(y < 0)
         dy = -dy;
      
      y+=dy;
   }
   
   public void update_keys()
   {
      if(right) x+=dx;
      if(left) x-=dx;
      if(up) y-=dy;
      if(down) y+=dy;
      
      if(x > PongGame.PANEL_WIDTH - w)
         x = PongGame.PANEL_WIDTH - w;
      if (x < 0)
         x = 0;
      if(y > PongGame.PANEL_HEIGHT - h)
         y = PongGame.PANEL_HEIGHT - h;
      if(y < 0)
         y = 0;
   }
  
   public void update_paddle_pong()
   {
      if(up) y-=dy;
      if(down) y+=dy;
      
      if(y < 0)
         y = 0;
      if(y > PongGame.PANEL_HEIGHT - h)
         y = PongGame.PANEL_HEIGHT - h;
   }
   
   public void update_paddle_brickbreaker()
   {
      if(left) x-=dx;
      if(right) x+=dx;
      
      if(x < minX)
         x = minX;
      if(x > maxX - w)
         x = maxX - w;
   }
   
      public boolean isOffPanel()
      {
         if(x < minX-w || x > maxX || y < minY-h || y > maxY)
            return true;
         return false;
      }
      
      //for space invaders
      public void setToShip(Sprite ship)
      {
         x = ship.x + 49;
         y = ship.y + 18;
      }
      
      public void update_alien()
      {
         x += dx;
         if(dx > 0)
            isFacingRight = false;
         else
            isFacingRight = true;
      }
      
      public void changeHorizontal()
      {
         dx = -dx;
      }
      
      public boolean willHitEdge()
      {
         return x + dx < 0 || x + w + dx > maxX;
      }
      
      public void moveAlienDown()
      {
         y += dy;
      }
      
      public void resetPongBall()
      {
         setXtoMiddle(0, PongGame.PANEL_WIDTH);
         setYtoMiddle(0, PongGame.PANEL_HEIGHT);
      }
   
      public void update_BrickbreakerBall()
      {
         if(x > maxX - w)
         {
            dx = -dx;
            isFacingRight = false;
         }
         if(y < minY + h) 
            dy = -dy;
         if(x < minX)
         {
            dx = -dx;
            isFacingRight = true;
         }

         x+=dx;
         y+=dy;
      }

      public void setXtoMiddle(int min, int max)
      {
         x = (min + max) / 2 - w/2;
      }
      
      public void setYtoMiddle(int min, int max)
      {
         y = (min + max) / 2 - h/2;
      }
   
      public double getX()
      {
         return x;
      }
      
      public double getY()
      {
         return y;
      }
      
      public void setX(double x)
      {
         this.x = x;
      }
      
      public void setY(double y)
      {
         this.y = y;
      }
      
      public void setImage(Image g)
      {
         this.image = g;
      }
      
      public int getHits()
      {
         return hits; 
      }
      
      public void setHits(int hits)
      {
         this.hits = hits;
      }
      
      public void setBoundaries(int minX, int maxX, int minY, int maxY)
      {
         this.minX = minX;
         this.maxX = maxX;
         this.minY = minY;
         this.maxY = maxY;
      }
      
      public void isFacingRight(boolean isFacingRight)
      {
         this.isFacingRight = isFacingRight;
      }
      
   //methods that access and modify the variables
   //object behaviors
   public void draw(Graphics2D g2)
   {
      g2.setColor(Color.RED);
      g2.fillRect((int) x, (int) y, w, h);
      
//      g2.setStroke(new BasicStroke(4));
      g2.setColor(Color.BLACK);
      g2.drawRect((int) x, (int) y, w, h);
   }
   
   public void drawWithHits(Graphics2D g2)
   {
      g2.setColor(Color.RED);
      g2.fillRect((int) x, (int) y, w, h);
      
//      g2.setStroke(new BasicStroke(4));
      g2.setColor(Color.BLACK);
      g2.drawRect((int) x, (int) y, w, h);
      
      g2.setFont(new Font("Super Mario 256", Font.PLAIN, 15));
      g2.drawString(""+hits, (int) x + 40, (int) y + 15);
   }
   
   public void drawImage(Graphics2D g2)
   {
      if(isFacingRight)
         g2.drawImage(image, (int) x, (int) y, w, h, null);
      else
         g2.drawImage(image, (int) x + w, (int) y, -w, h, null);
   }
   
   public void drawImageWithHits(Graphics2D g2)
   {
      if(isFacingRight)
         g2.drawImage(image, (int) x, (int) y, w, h, null);
      else
         g2.drawImage(image, (int) x + w, (int) y, -w, h, null);
      g2.setFont(new Font("Super Mario 256", Font.PLAIN, 15));
      g2.drawString(""+hits, (int) x + 40, (int) y + 15);
   }
 
   public void checkForPress(int key)
   {
      if(key == rightKey) 
      {
         right = true;
         isFacingRight = true;
      }
      if(key == leftKey) 
      {
         left = true;
         isFacingRight = false;
      }
      if(key == upKey) up = true;
      if(key == downKey) down = true;
   }
  
   public void checkForRelease(int key)
   {
      if(key == rightKey) right = false;
      if(key == leftKey) left = false;
      if(key == upKey) up = false;
      if(key == downKey) down = false;
   }
   
   //COLLISION METHODS
   /** Returns a hitbox to be used for collision checking
   */
   public Rectangle getBounds()
   {
      return new Rectangle((int) x, (int) y, w, h);
   }
   
   /** Determines the intersecting side for an object in relation to another object
   *  return true for a collision and false otherwise
   */
   public boolean checkAndReactToCollisionWith(Rectangle rect)
   {
      int xm = (int)x + w/2; //use the center of the rectangle
      int ym = (int)y + h/2; //use the center of the rectangle
      
      int side = getSideForIntersection(rect, xm, ym); //get location of the this object in relation to the other
      
      if(side == 0)      //Is this object above the other?
         return checkCollisionTopOfRectangle(rect);
      else if(side == 1) //Is this object to the right of the other?
         return checkCollisionRightSideOfRectangle(rect);
      else if(side == 2) //Is this object below the other?
         return checkCollisionBottomOfRectangle(rect);
      else if(side == 3) //Is this object to the left of the other?
         return checkCollisionLeftSideOfRectangle(rect);
      
      return false;
   }
   
   /** Returns the side where a collision "would" occur if intersecting
   *    0 = top
   *    1 = right
   *    2 = bottom
   *    3 = left
   */
   private int getSideForIntersection(Rectangle rect, int x1, int y1)
   {
      double slopeMajor = (double) rect.height / rect.width;       //major diagonal slope
      double slopeMinor = (double) -rect.height / rect.width;      //minor diagonal slope
      double bMajor = rect.y - slopeMajor * rect.x;                //major diagonal y-intercept
      double bMinor = rect.y - slopeMinor * (rect.x + rect.width); //minor diagonal y-intercept
      
      boolean aboveMajor = y1 < slopeMajor * x1 + bMajor; //Is the center of this object above the major diagonal
      boolean aboveMinor = y1 < slopeMinor * x1 + bMinor; //Is the center of this object above the minor diagonal
      
      if(aboveMajor  && aboveMinor)  return 0; //This object is above the other
      if(aboveMajor  && !aboveMinor) return 1; //This object is to the right of the other
      if(!aboveMajor && !aboveMinor) return 2; //This object is below the other
      if(!aboveMajor && aboveMinor)  return 3; //This object is to the left of the other
      
      System.out.println("I'm on a diagonal!");
      return -1;   //Should never get here since "not above" is below OR ON a diagonal
   }
   
   private boolean checkCollisionLeftSideOfRectangle(Rectangle rect)
   {
      boolean collision = false;
      
      if(y + h > rect.y && y < rect.y + rect.height) {
         if(x + w > rect.x) {
            dx = -Math.abs(dx); //direction change: left
            collision = true;
            isFacingRight = false;
         }
      }
      return collision;
   }
   
   private boolean checkCollisionRightSideOfRectangle(Rectangle rect)
   {
      boolean collision = false;
      
      if(y + h > rect.y && y < rect.y + rect.height) {
         if(x < rect.x + rect.width) {
            dx = Math.abs(dx); //direction change: right
            x = rect.x + rect.width;
            collision = true;
            isFacingRight = true;
         }
      }
      return collision;
   }
   
   private boolean checkCollisionBottomOfRectangle(Rectangle rect)
   {
      boolean collision = false;
      
      if(x + w > rect.x && x < rect.x + rect.width) {
         if(y < rect.y + rect.height) {
            dy = Math.abs(dy); //direction change: down
            collision = true;
         }
      }
      return collision;
   }
   
   private boolean checkCollisionTopOfRectangle(Rectangle rect)
   {
      boolean collision = false;
      
      if(x + w > rect.x && x < rect.x + rect.width) {
         if(y + h > rect.y) {
            dy = -Math.abs(dy); //direction change: up
            collision = true;
         }
      }
      return collision;
   }
}
