import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;

//Andrew Weishar
//Program description:
//Jun 13, 2025
//

public class Platforms
{
   
   private ArrayList<Rectangle> platforms;
   
   public Platforms()
   {
      platforms = new ArrayList<Rectangle>();
   }
   
   public void updateH()
   {
      int dx = 1;
      for(Rectangle r : platforms) {
         r.x -= dx;
         if(r.x + r.width < 0) {
            r.x = SpriteTester.PREF_W + (int) (Math.random() * 100);
            r.y = (int) (Math.random() * (SpriteTester.PREF_H - r.height)); //random spawn height
         }
      }
   }
   
   public void updateV()
   {
//      int dy = 2; //sets constant speed
      for(Rectangle r : platforms) {
         if(r.y > SpriteTester.PREF_H) {
            r.y = -20; //sends platform back to top of screen
            r.x = (int) (Math.random() * (SpriteTester.PREF_W - r.width)); //sets random spawn (x value)
          }
      }
   }
   
   public void setLevel1()
   {
      platforms.clear(); //resets the number of platforms
      
      int platformW = 50; //sets platform width
      int platformH = 10; //sets platform height
      int numPlatforms = 10; //the number of platforms created
      int spacing = 60; //vertical space between platforms
      
      //spawn platforms at random locations
      for(int i = 0; i < numPlatforms; i++) {
         boolean overlap = false;
         int x, y;
         
         do
         {
            overlap = false;
            x = (int) (Math.random() * (SpriteTester.PREF_W - platformW));
            y = SpriteTester.PREF_H - i * spacing; 
            
            for(Rectangle r : platforms)
               if(r.y == y && r.getBounds().intersects(new Rectangle(x, y, platformW, platformH).getBounds()))
                  overlap = true;
         } while(overlap);
         platforms.add(new Rectangle(x, y, platformW, platformH));
      }
      
      //starting platform for sprite
      platforms.add(new Rectangle(SpriteTester.PREF_W / 2 - platformW / 2, SpriteTester.PREF_H - 60, platformW, platformH));
   }
   
   public void draw(Graphics2D g2)
   {
      for(Rectangle r : platforms) {
         g2.setColor(new Color((int) (Math.random()*256), //sets random red value
             (int) (Math.random()*256), //sets random green value
             (int) (Math.random()*256))); //sets random blue value
         g2.fill(r); //draws rectangle
//         g2.setColor(Color.WHITE); 
//         g2.draw(r); //draws border
      }
   }
   
   public ArrayList<Rectangle> getPlatforms()
   {
      return platforms;
   }
   
   
}
