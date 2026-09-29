import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//Andrew Weishar
//Program description: Sprite class for a PLATFORMER game
//Jun 11, 2025
//

public class Sprite
{
   private final int GROUND_LEVEL = 339;
   
   private double x, y, dx, dy;
   private int width, height;
   
   private boolean left, right, jumping, falling;

   private double moveSpeed, stopSpeed, maxSpeed; //values for horizontal movement
   private double jumpSpeed, gravity, maxFallingSpeed; //values for vertical movement
   private BufferedImage[] idleSprite, walkingSprites, jumpingSprite, fallingSprite;
   private Animation animation;
   
   private boolean facingLeft;
   
   private Platforms platforms;
   
   public Sprite(Platforms platforms)
   {
      jumping = true; 
      
      this.platforms = platforms;
      
      width = 22;
      height = 22;
      
      //Initial values for horizontal movement
      moveSpeed = 0.5; //acceleration factor
      stopSpeed = 0.5; //deceleration factor (low value slides like ice)
      maxSpeed = 2.0; //top speed
      
      //Initial values for vertical movement
      jumpSpeed = -10.0;
      gravity = 0.25; 
      maxFallingSpeed = 12; //top falling speed (low value glides like parachute)
      
      try {
         idleSprite = new BufferedImage[1];
         idleSprite[0] = ImageIO.read(new File("src/kirbyidle.gif"));
        
         jumpingSprite = new BufferedImage[1];
         jumpingSprite[0] = ImageIO.read(new File("src/kirbyjump.gif"));
        
         fallingSprite = new BufferedImage[1];
         fallingSprite[0] = ImageIO.read(new File("src/kirbyfall.gif"));
         
         walkingSprites = new BufferedImage[6];
         //Read in from a sprite sheet
         BufferedImage image = ImageIO.read(new File("src/kirbywalk.gif"));
         for(int i = 0; i < walkingSprites.length; i++) {
            walkingSprites[i] = image.getSubimage(
                  i*width + i,
                  0,
                  width,
                  height
                  );
         }
      } catch (IOException e) {e.printStackTrace();}
      
      //Initialize the Animation object
      animation = new Animation();
      animation.setFrames(idleSprite);
      
   }
   
   public void update()
   {
      //HORIZONTAL MOVEMENT
      if(left) {
         dx -= moveSpeed;           //this is acceleration
         if(dx < -maxSpeed)         //top speed to the left
            dx = -maxSpeed;
      }
      else if(right) {
         dx += moveSpeed;           //this is acceleration
         if(dx > maxSpeed)          //top speed to the left
            dx = maxSpeed;
      }
      else {
         if(dx > 0) {               //slow to a stop if moving right
            dx -= stopSpeed;        //deceleration
            if(dx < 0)
               dx = 0;
         }
         else if(dx < 0) {          
            dx += stopSpeed;        
            if(dx > 0)
               dx = 0;
         }
      }
      //wrap around
      if(x < width / 2)
         x = SpriteTester.PREF_W - width / 2;
      if(x > SpriteTester.PREF_W - width / 2)
         x = width;
      
      //VERTICAL MOVEMENT
      if(jumping) {
         dy = jumpSpeed;             //initial jump power 
         jumping = false;
         falling = true;
      }
      else if(falling) {
         dy += gravity;              //acceleration due to gravity
         if(dy > maxFallingSpeed)    //check terminal velocity
            dy = maxFallingSpeed;
      } 
      else {
         dy = 0;
      }
      
      //update the location of the sprite
      x += dx;
      calculateY(); //make adjustments to the y-value based on platform collisions
      
//      if(y >= GROUND_LEVEL) {
//         falling = false;
//         y = GROUND_LEVEL;
//      }
      
      //Set the direction sprite is facing
      if(dx < 0)
         facingLeft = true;
      else if(dx > 0)
         facingLeft = false;
      
      //Set the animation... update the image based on the sprite's properties
      if(left || right) {
         animation.setFrames(walkingSprites);
         animation.setDelay(100);
      }
      else {
         animation.setFrames(idleSprite);
         animation.setDelay(-1);
      }
      
      if(dy < 0) { //if moving up
         animation.setFrames(jumpingSprite);
         animation.setDelay(-1);
      }
      else if(dy > 0) { //if moving down
         animation.setFrames(fallingSprite);
         animation.setDelay(-1);
      }
      
      //Update the animation timer so the values can check for a costume change
      animation.update();
      
   }
   
   //To make adjustments to the sprite y-value based on platform collisions
   public void calculateY()
   {
      //Always allow sprite to go up
      if(dy < 0) {
         y += dy;
         return;
      }
      
      //CHECK BELOW THE SPRITE FOR A POTENTIAL PLATFORMTO STOP FALLING
      //OR CHECK IF YOU "WALK OFF" A PLATFORM AND HAVE TO START FALLING
      
      //Define booleans for whether a specific location is blocked (blocked = true)
      boolean bottomLeft; //if true, then platform is below you, false if nothing below
      boolean bottomRight; 
      
      //If falling, see if you land on a platform or keep falling
      if(dy > 0) {
         Rectangle platform = null;
         double toY = y + dy; //The y that the sprite would go to without platform collision
         //Check possible collision with each platform
         for(Rectangle r : platforms.getPlatforms()) {
            bottomLeft = r.contains(x - width / 2, toY + height / 2);
            bottomRight = r.contains(x + width / 2, toY + height / 2);
            
            if(bottomLeft || bottomRight)
               platform = r;
         }
         //if you landed on a platform, keep jumping
         if(platform != null) {
            dy = jumpSpeed; //jump again
            y = platform.getY() - height / 2;
            falling = true; //start falling after jump
         }
         else //No platform below sprite so keep falling
            y += dy;
      }
      
      //See if you "stepped off" a platform in which case you should fall
      if(!falling) {
         falling = true; //This will stay true if no platform is below sprite
         for(Rectangle r : platforms.getPlatforms()) {
            bottomLeft = r.contains(x - width / 2, y + height / 2 + 1);
            bottomRight = r.contains(x + width / 2, y + height / 2 + 1);
            if(bottomLeft || bottomRight)
               falling = false;
         }
      }
   }
   
   public void draw(Graphics2D g2)
   {
      //draw the sprite hitbox
//      g2.setColor(Color.RED);
//      g2.fillRect((int)x-width/2, (int)y-height/2, width, height);
   
      if(facingLeft)
         g2.drawImage(
               animation.getImage(),
               (int) (x - width / 2), //x is the horizontal center of the sprite
               (int) (y - height / 2), //y is the vertical center of the sprite
               width,
               height,
               null
         );
      else //sprite is facing to the right
         g2.drawImage(
               animation.getImage(),
               (int) (x - width / 2 + width), //x is the horizontal center of the sprite
               (int) (y - height / 2), //y is the vertical center of the sprite
               -width,   //flip the image with a negative
               height, 
               null
         );
   }
   
   public double getX()
   {
      return x;
   }

   public void setX(double x)
   {
      this.x = x;
   }

   public double getY()
   {
      return y;
   }

   public void setY(double y)
   {
      this.y = y;
   }

   public double getDx()
   {
      return dx;
   }

   public void setDx(double dx)
   {
      this.dx = dx;
   }

   public double getDy()
   {
      return dy;
   }

   public void setDy(double dy)
   {
      this.dy = dy;
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

   public boolean isJumping()
   {
      return jumping;
   }

   public void setJumping(boolean jumping)
   {
      //avoid double jump
      if(!falling) 
         this.jumping = true;
   }
   
}
