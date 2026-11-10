
#include <Servo.h>

//Andrew Weishar
//Program description: Centers the servo and controls the ultrasonic radar.
//Nov 9, 2026
//

const int TRIG_PIN = 10;
const int ECHO_PIN = 11;
const int SERVO_PIN = 9;

const int CENTER_ANGLE = 90;
const int SWEEP_ANGLE = 90;
const int MIN_ANGLE = CENTER_ANGLE - SWEEP_ANGLE;
const int MAX_ANGLE = CENTER_ANGLE + SWEEP_ANGLE;

Servo radarServo;

int angle = CENTER_ANGLE;
int direction = 1;
int distance;

void setup()
{
   Serial.begin(9600);

   pinMode(TRIG_PIN, OUTPUT);
   pinMode(ECHO_PIN, INPUT);
   digitalWrite(TRIG_PIN, LOW);

   //move the servo to the center when the Arduino starts
   radarServo.attach(SERVO_PIN);
   radarServo.write(CENTER_ANGLE);
   delay(1500);

   //start the sweep from the center
   angle = CENTER_ANGLE;
   direction = 1;
}

void loop()
{
   radarServo.write(angle);
   delay(30);

   distance = getDistance();

   //send angle and distance to Processing
   Serial.print(angle);
   Serial.print(",");
   Serial.println(distance);

   //change direction at the ends of the sweep
   if(angle >= MAX_ANGLE)
   {
      direction = -1;
   }
   else if(angle <= MIN_ANGLE)
   {
      direction = 1;
   }

   angle += direction;
}

int getDistance()
{
   //send ultrasonic pulse
   digitalWrite(TRIG_PIN, LOW);
   delayMicroseconds(2);
   digitalWrite(TRIG_PIN, HIGH);
   delayMicroseconds(10);
   digitalWrite(TRIG_PIN, LOW);

   long duration = pulseIn(ECHO_PIN, HIGH, 25000UL);

   if(duration == 0)
   {
      return -1;
   }

   int measuredDistance = duration / 58;

   if(measuredDistance < 2 || measuredDistance > 200)
   {
      return -1;
   }

   return measuredDistance;
}
