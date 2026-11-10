 
import processing.serial.*;

//Andrew Weishar
//Program description: Displays live ultrasonic radar in fullscreen.
//Nov 9, 2026
//

final int MAX_DISTANCE = 200;

Serial radarPort;

int angle = 90;
int distance = -1;
int direction = 1;
int lastReading = -10000;

String[] ports = new String[0];
String message = "Select an Arduino port";

void settings()
{
   //open the radar in fullscreen
   fullScreen();
}

void setup()
{
   frameRate(60);
   refreshPorts();
}

void draw()
{
   background(0);

   drawRadar();
   drawSweep();
   drawLiveObject();
   drawInformation();

   if(radarPort == null)
   {
      drawPortSelection();
   }
}

float getScale()
{
   //adjust text and graphics for the screen size
   return min(width / 1000.0f, height / 700.0f);
}

float getRadius()
{
   //calculate radar size based on the screen
   return min(width * 0.43f, height * 0.65f);
}

float getCenterX()
{
   return width / 2.0f;
}

float getCenterY()
{
   return height * 0.82f;
}

void refreshPorts()
{
   //get available serial ports
   try
   {
      ports = Serial.list();

      if(ports == null)
      {
         ports = new String[0];
      }

      println("Available Ports:");
      printArray(ports);

      if(ports.length == 0)
      {
         message = "No serial ports found";
      }
      else
      {
         message = "Select your Arduino port";
      }
   }
   catch(Exception e)
   {
      ports = new String[0];
      message = "Unable to find serial ports";
      println(e.getMessage());
   }
}

void connectArduino(int portNumber)
{
   if(portNumber < 0 || portNumber >= ports.length)
   {
      return;
   }

   try
   {
      //connect to Arduino
      radarPort = new Serial(this, ports[portNumber], 9600);
      radarPort.clear();
      radarPort.bufferUntil('\n');

      message = "Connected: " + ports[portNumber];
      lastReading = -10000;
      distance = -1;

      println(message);
   }
   catch(Exception e)
   {
      radarPort = null;
      message = "Unable to connect. Check the port.";
      println(e.getMessage());
   }
}

void drawRadar()
{
   float centerX = getCenterX();
   float centerY = getCenterY();
   float radius = getRadius();
   float scale = getScale();

   noFill();
   stroke(0, 110, 55);
   strokeWeight(max(1, scale));

   //draw distance rings
   for(int i = 1; i <= 4; i++)
   {
      float ringRadius = radius * i / 4.0f;

      arc(centerX, centerY, ringRadius * 2, ringRadius * 2,
          PI, TWO_PI);

      fill(0, 200, 90);
      textAlign(LEFT);
      textSize(13 * scale);

      text(i * 50 + " cm",
           centerX + 10 * scale,
           centerY - ringRadius - 6 * scale);

      noFill();
   }

   //draw angle lines
   for(int i = 0; i <= 180; i += 30)
   {
      float rotation = radians(i);

      float x = centerX - cos(rotation) * radius;
      float y = centerY - sin(rotation) * radius;

      stroke(0, 90, 40);
      line(centerX, centerY, x, y);

      fill(0, 200, 90);
      textAlign(CENTER);
      textSize(13 * scale);

      text(i + "°",
           centerX - cos(rotation) * (radius + 22 * scale),
           centerY - sin(rotation) * (radius + 22 * scale));

      noFill();
   }

   //draw radar baseline
   stroke(0, 210, 90);
   strokeWeight(2 * scale);

   line(centerX - radius, centerY,
        centerX + radius, centerY);
}

void drawSweep()
{
   if(radarPort == null || millis() - lastReading > 3000)
   {
      return;
   }

   float centerX = getCenterX();
   float centerY = getCenterY();
   float radius = getRadius();
   float scale = getScale();

   //draw moving radar line
   for(int i = 15; i >= 0; i--)
   {
      int sweepAngle = angle - direction * i;

      if(sweepAngle >= 0 && sweepAngle <= 180)
      {
         float rotation = radians(sweepAngle);

         float x = centerX - cos(rotation) * radius;
         float y = centerY - sin(rotation) * radius;

         stroke(0, 255, 100, map(i, 0, 15, 255, 10));
         strokeWeight(i == 0 ? 3 * scale : scale);

         line(centerX, centerY, x, y);
      }
   }
}

void drawLiveObject()
{
   //only display a recent valid reading
   if(radarPort == null)
   {
      return;
   }

   if(distance < 2 || distance > MAX_DISTANCE)
   {
      return;
   }

   if(millis() - lastReading > 180)
   {
      return;
   }

   float centerX = getCenterX();
   float centerY = getCenterY();
   float radius = getRadius();
   float scale = getScale();

   //calculate object position
   float objectRadius = radius * (float) distance / MAX_DISTANCE;
   float rotation = radians(angle);

   float x = centerX - cos(rotation) * objectRadius;
   float y = centerY - sin(rotation) * objectRadius;

   //draw detected object as a red arc
   noFill();
   stroke(255, 50, 50);
   strokeWeight(10 * scale);

   arc(centerX, centerY,
       objectRadius * 2, objectRadius * 2,
       PI + radians(angle - 3),
       PI + radians(angle + 3));

   //show distance next to detected object
   fill(255, 120, 120);
   textAlign(LEFT);
   textSize(15 * scale);

   text(distance + " cm",
        x + 12 * scale,
        y - 12 * scale);
}

void drawInformation()
{
   float scale = getScale();

   fill(0, 255, 110);
   textAlign(LEFT);

   //draw title
   textSize(27 * scale);
   text("Ultrasonic Radar", 30 * scale, 48 * scale);

   textSize(14 * scale);
   text(message, 30 * scale, 76 * scale);

   //draw angle and distance
   textSize(18 * scale);

   text("Angle: " + angle + "°",
        30 * scale, height - 42 * scale);

   if(distance >= 2 && distance <= MAX_DISTANCE &&
      millis() - lastReading <= 180)
   {
      text("Distance: " + distance + " cm",
           width * 0.25f, height - 42 * scale);
   }
   else
   {
      text("Distance: No Object",
           width * 0.25f, height - 42 * scale);
   }

   //draw connection status
   if(radarPort != null)
   {
      if(millis() - lastReading > 3000)
      {
         fill(255, 180, 0);

         text("Waiting for data...",
              width * 0.58f, height - 42 * scale);
      }
      else
      {
         fill(0, 255, 110);

         text("LIVE",
              width * 0.58f, height - 42 * scale);
      }
   }

   //draw controls
   fill(100, 180, 130);
   textSize(13 * scale);

   text("Press P to change port    |    Press ESC to exit fullscreen",
        30 * scale, height - 15 * scale);
}

void drawPortSelection()
{
   float scale = getScale();

   float boxWidth = min(width * 0.85f, 720 * scale);
   float boxHeight = min(height * 0.78f, 430 * scale);

   float boxX = (width - boxWidth) / 2;
   float boxY = (height - boxHeight) / 2;

   float left = boxX + 25 * scale;

   //draw port selection menu
   fill(10, 30, 20);
   stroke(0, 180, 90);
   strokeWeight(2 * scale);

   rect(boxX, boxY, boxWidth, boxHeight, 10 * scale);

   fill(0, 255, 110);
   textAlign(LEFT);
   textSize(22 * scale);

   text("Select Arduino Port",
        left, boxY + 43 * scale);

   textSize(15 * scale);

   if(ports.length == 0)
   {
      text("No ports available",
           left, boxY + 100 * scale);
   }

   //show available ports
   for(int i = 0; i < ports.length && i < 10; i++)
   {
      String number = str(i + 1);

      if(i == 9)
      {
         number = "0";
      }

      text("Press " + number + ": " + ports[i],
           left, boxY + (105 + i * 26) * scale);
   }

   fill(160, 200, 170);
   textSize(14 * scale);

   text("Press R to refresh ports",
        left, boxY + boxHeight - 25 * scale);
}

void serialEvent(Serial port)
{
   String data = port.readStringUntil('\n');

   if(data == null)
   {
      return;
   }

   //separate angle and distance
   String[] values = split(trim(data), ',');

   if(values.length != 2)
   {
      return;
   }

   try
   {
      int newAngle = Integer.parseInt(trim(values[0]));
      int newDistance = Integer.parseInt(trim(values[1]));

      //check valid measurements
      if(newAngle < 0 || newAngle > 180)
      {
         return;
      }

      if(newDistance < -1 || newDistance > MAX_DISTANCE)
      {
         return;
      }

      //determine servo direction
      if(newAngle > angle)
      {
         direction = 1;
      }
      else if(newAngle < angle)
      {
         direction = -1;
      }

      //update live radar data
      angle = newAngle;
      distance = newDistance;
      lastReading = millis();
   }
   catch(NumberFormatException e)
   {
      //ignore invalid serial data
   }
}

void keyPressed()
{
   //refresh available ports
   if(key == 'R' || key == 'r')
   {
      refreshPorts();
   }

   //disconnect and change port
   if(key == 'P' || key == 'p')
   {
      if(radarPort != null)
      {
         radarPort.stop();
         radarPort = null;
      }

      distance = -1;
      refreshPorts();
   }

   //select port using number keys
   if(radarPort == null)
   {
      if(key >= '1' && key <= '9')
      {
         connectArduino(key - '1');
      }
      else if(key == '0')
      {
         connectArduino(9);
      }
   }
}
