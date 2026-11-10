
import processing.serial.*;

//Andrew Weishar
//Program description: Displays live objects detected by the Arduino radar.
//Nov 9, 2026
//

final int PANEL_WIDTH = 1000;
final int PANEL_HEIGHT = 700;
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
   size(1000, 700);
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

void refreshPorts()
{
   //get available serial ports
   ports = Serial.list();

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
   int centerX = PANEL_WIDTH / 2;
   int centerY = PANEL_HEIGHT - 105;
   int radius = 430;

   noFill();
   stroke(0, 110, 55);
   strokeWeight(1);

   //draw distance rings
   for(int i = 1; i <= 4; i++)
   {
      int ringRadius = radius * i / 4;

      arc(centerX, centerY, ringRadius * 2, ringRadius * 2,
          PI, TWO_PI);

      fill(0, 200, 90);
      textAlign(LEFT);
      textSize(13);
      text(i * 50 + " cm", centerX + 10, centerY - ringRadius - 6);
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
      textSize(13);

      text(i + "°",
           centerX - cos(rotation) * (radius + 22),
           centerY - sin(rotation) * (radius + 22));

      noFill();
   }

   //draw radar baseline
   stroke(0, 210, 90);
   strokeWeight(2);
   line(centerX - radius, centerY, centerX + radius, centerY);
}

void drawSweep()
{
   int centerX = PANEL_WIDTH / 2;
   int centerY = PANEL_HEIGHT - 105;
   int radius = 430;

   //draw the moving radar line
   for(int i = 15; i >= 0; i--)
   {
      int sweepAngle = angle - direction * i;

      if(sweepAngle >= 0 && sweepAngle <= 180)
      {
         float rotation = radians(sweepAngle);
         float x = centerX - cos(rotation) * radius;
         float y = centerY - sin(rotation) * radius;

         stroke(0, 255, 100, map(i, 0, 15, 255, 10));
         strokeWeight(i == 0 ? 3 : 1);

         line(centerX, centerY, x, y);
      }
   }
}

void drawLiveObject()
{
   //only display a recent valid reading
   if(distance < 2 || distance > MAX_DISTANCE)
   {
      return;
   }

   if(millis() - lastReading > 250)
   {
      return;
   }

   int centerX = PANEL_WIDTH / 2;
   int centerY = PANEL_HEIGHT - 105;
   int radius = 430;

   float objectRadius = radius * (float) distance / MAX_DISTANCE;
   float rotation = radians(angle);

   float x = centerX - cos(rotation) * objectRadius;
   float y = centerY - sin(rotation) * objectRadius;

   //draw the detected object as a red arc
   noFill();
   stroke(255, 50, 50);
   strokeWeight(12);

   arc(centerX, centerY,
       objectRadius * 2, objectRadius * 2,
       PI + radians(angle - 3),
       PI + radians(angle + 3));

   //draw distance next to detected object
   fill(255, 120, 120);
   textAlign(LEFT);
   textSize(15);
   text(distance + " cm", x + 12, y - 12);
}

void drawInformation()
{
   fill(0, 255, 110);
   textAlign(LEFT);

   textSize(27);
   text("Arduino Radar", 30, 48);

   textSize(14);
   text(message, 30, 76);

   textSize(18);
   text("Angle: " + angle + "°", 30, PANEL_HEIGHT - 40);

   if(distance >= 2 && millis() - lastReading <= 250)
   {
      text("Distance: " + distance + " cm",
           230, PANEL_HEIGHT - 40);
   }
   else
   {
      text("Distance: No Object",
           230, PANEL_HEIGHT - 40);
   }

   if(radarPort != null)
   {
      if(millis() - lastReading > 3000)
      {
         fill(255, 180, 0);
         text("Waiting for data...", 530, PANEL_HEIGHT - 40);
      }
      else
      {
         fill(0, 255, 110);
         text("LIVE", 530, PANEL_HEIGHT - 40);
      }
   }

   fill(100, 180, 130);
   textSize(13);
   text("Press P to change port", 30, PANEL_HEIGHT - 14);
}

void drawPortSelection()
{
   //draw the serial port selection menu
   fill(10, 30, 20, 245);
   stroke(0, 180, 90);
   strokeWeight(2);
   rect(225, 140, 550, 350, 10);

   fill(0, 255, 110);
   textAlign(LEFT);
   textSize(22);
   text("Select Arduino Port", 250, 180);

   textSize(15);

   if(ports.length == 0)
   {
      text("No ports available", 250, 220);
   }

   for(int i = 0; i < ports.length && i < 10; i++)
   {
      String number = str(i + 1);

      if(i == 9)
      {
         number = "0";
      }

      text("Press " + number + ": " + ports[i],
           250, 220 + i * 24);
   }

   fill(160, 200, 170);
   textSize(14);
   text("Press R to refresh ports", 250, 465);
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

   //disconnect and select another port
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

   //select a serial port using the number keys
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
