import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

//Andrew Weishar
//Program description:
//Oct 28, 2024
//

public class Snake
{
   private int headX, headY, dx, dy, w, h;
   private ArrayList<SnakeBodyPart> snake;
   private boolean up, down, left, right;
   private int upKey, downKey, leftKey, rightKey;
   private Color color;
   private int StartSnakeLength;
   
   public Snake(int x, int y, int w, int h, int upKey, int downKey, int leftKey, int rightKey, Color color)
   {
      up = true;
      StartSnakeLength = 3;
      this.headX = x;
      this.headY = y;
      this.w = w;
      this.h = h;
      
      snake = new ArrayList<SnakeBodyPart>();
      for(int i = 0; i < StartSnakeLength; i++)
         snake.add(new SnakeBodyPart(x, y, w, h, color));
      
      dx = w;
      dy = h;
      this.upKey = upKey;
      this.downKey = downKey;
      this.leftKey = leftKey;
      this.rightKey = rightKey;
      this.color = color;
   }
   
   public Rectangle getHitBox()
   {
      return new Rectangle(headX, headY, w, h);
   }
  
   public void update()
   {
      if(up) headY-=dy;
      if(down) headY+=dy;
      if(left) headX-=dx;
      if(right) headX+=dx;
      
      snake.remove(snake.size()-1);
      snake.add(0, new SnakeBodyPart(headX, headY, w, h, color));
   }
   
   public void addBodyPart()
   {
      snake.add(0, new SnakeBodyPart(headX, headY, w, h, color));
   }
   
   public void draw(Graphics2D g2)
   {
      for(SnakeBodyPart sbp : snake)
         sbp.draw(g2);
   }
   
   public void keyWasPressed(int key)
   {
      if(key == upKey) {
         up = true;
         down = false;
         left = false;
         right = false;
      }
      if(key == downKey) {
         up = false;
         down = true;
         left = false;
         right = false;
      }
      if(key == leftKey) {
         up = false;
         down = false;
         left = true;
         right = false;
      }
      if(key == rightKey) {
         up = false;
         down = false;
         left = false;
         right = true;
      }
   }
   
   public int getX()
   {
      return headX;
   }
   
   public void setX(int x)
   {
      this.headX = x;
   }
   
   public int getY()
   {
      return headY;
   }
   
   public void setY(int y)
   {
      this.headY = y;
   }
   
   public int getDx()
   {
      return dx;
   }
   
   public void setDx(int dx)
   {
      this.dx = dx;
   }
   
   public int getDy()
   {
      return dy;
   }
   
   public void setDy(int dy)
   {
      this.dy = dy;
   }
   
   public int getW()
   {
      return w;
   }
   
   public void setW(int w)
   {
      this.w = w;
   }
   
   public int getH()
   {
      return h;
   }
   
   public void setH(int h)
   {
      this.h = h;
   }
   
   public boolean isUp()
   {
      return up;
   }
   
   public void setUp(boolean up)
   {
      this.up = up;
   }
   
   public boolean isDown()
   {
      return down;
   }
   
   public void setDown(boolean down)
   {
      this.down = down;
   }
   
   public boolean isLeft()
   {
      return left;
   }
   
   public void setLeft(boolean left)
   {
      this.left = left;
   }
   
   public boolean isRight()
   {
      return right;
   }
   
   public void setRight(boolean right)
   {
      this.right = right;
   }
   
   public int getUpKey()
   {
      return upKey;
   }
   
   public void setUpKey(int upKey)
   {
      this.upKey = upKey;
   }
   
   public int getDownKey()
   {
      return downKey;
   }
   
   public void setDownKey(int downKey)
   {
      this.downKey = downKey;
   }
   
   public int getLeftKey()
   {
      return leftKey;
   }
   
   public void setLeftKey(int leftKey)
   {
      this.leftKey = leftKey;
   }
   
   public int getRightKey()
   {
      return rightKey;
   }
   
   public void setRightKey(int rightKey)
   {
      this.rightKey = rightKey;
   }
   
   public Color getColor()
   {
      return color;
   }
   
   public void setColor(Color color)
   {
      this.color = color;
   }
  
   @Override
   public String toString()
   {
      return "Snake [x=" + headX + ", y=" + headY + ", dx=" + dx + ", dy=" + dy + ", w=" + w + ", h=" + h + ", up=" + up
            + ", down=" + down + ", left=" + left + ", right=" + right + ", upKey=" + upKey + ", downKey=" + downKey
            + ", leftKey=" + leftKey + ", rightKey=" + rightKey + "]";
   }

   public boolean isCollidingWithSelf()
   {
      for(int i = 1; i < snake.size(); i++) {
         if(this.getHitBox().intersects(snake.get(i).getBodyHitBox()))
            return true;
      }
      return false;
   }
   
}
