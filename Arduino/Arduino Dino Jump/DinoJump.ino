// Include the LCD library
#include <LiquidCrystal.h>

// --- LCD Pin Configuration ---
const int rs = 12, en = 11, d4 = 5, d5 = 4, d6 = 3, d7 = 2;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

// --- Button Pin Configuration ---
#define BUTTON_PIN 7 // Using digital pin 7 for the jump button

// --- Game Configuration ---
const int DINO_POSITION_X = 1;
const int GROUND_LEVEL = 1;
const int JUMP_LEVEL = 0;
const unsigned long JUMP_TIME = 450;

// --- Game State Variables ---
int dino_y = GROUND_LEVEL;
int obstacle_x = 15; // Obstacle is back to only having an X position
int score = 0;
bool isJumping = false;
bool isGameOver = false;
unsigned long jumpStartTime;
int gameSpeed = 250;

// --- Custom Characters ---
byte dino[8] = { B00111, B00101, B00110, B01111, B11100, B01010, B00110, B00000 };
// This is the original cactus sprite you had
byte cactus[8] = { B00100, B01101, B10101, B00101, B00101, B00101, B00101, B00000 };

void setup() {
  lcd.begin(16, 2);
  
  // Set up the button pin
  pinMode(BUTTON_PIN, INPUT_PULLUP);

  // Create just the two characters
  lcd.createChar(0, dino);
  lcd.createChar(1, cactus);

  lcd.print("Dino Jump Game!");
  lcd.setCursor(0, 1);
  lcd.print("Press to Start");
  
  // Wait for a button press (LOW) to start
  while (digitalRead(BUTTON_PIN) == HIGH) {
    // Do nothing, just wait
  }
  
  resetGame();
}

void loop() {
  if (!isGameOver) {
    handleButton();
    handleJump();
    moveObstacle();
    checkCollision();
    drawGameScreen();
    delay(gameSpeed);
  } else {
    // Wait for a button press (LOW) to restart
    if (digitalRead(BUTTON_PIN) == LOW) {
      resetGame();
    }
  }
}

// Reads the digital button
void handleButton() {
  if (digitalRead(BUTTON_PIN) == LOW && !isJumping) {
    isJumping = true;
    dino_y = JUMP_LEVEL;
    jumpStartTime = millis();
  }
}

void handleJump() {
  if (isJumping) {
    if (millis() - jumpStartTime > JUMP_TIME) {
      isJumping = false;
      dino_y = GROUND_LEVEL;
    }
  }
}

// REVERTED: Simplified obstacle logic
void moveObstacle() {
  obstacle_x--;

  // If obstacle is off-screen, reset it and increase score
  if (obstacle_x < 0) { // Using < 0 for original feel
    obstacle_x = 15; // Reset to the far right
    score++;
    
    if (gameSpeed > 100) {
      gameSpeed -= 10;
    }
  }
}

// REVERTED: Simplified collision logic
void checkCollision() {
  // A collision occurs if the obstacle is at the Dino's X position
  // AND the Dino is on the ground (not jumping).
  if (obstacle_x == DINO_POSITION_X && dino_y == GROUND_LEVEL) {
    isGameOver = true;
    displayGameOver();
  }
}

// REVERTED: Simplified drawing logic
void drawGameScreen() {
  lcd.clear();
  
  // Draw Dino
  lcd.setCursor(DINO_POSITION_X, dino_y);
  lcd.write((byte)0);

  // Draw the cactus on the ground
  lcd.setCursor(obstacle_x, GROUND_LEVEL);
  lcd.write((byte)1);

  // Draw Score
  lcd.setCursor(4, 0);
  lcd.print("Score: ");
  lcd.print(score);
}

void displayGameOver() {
  lcd.clear();
  lcd.setCursor(3, 0);
  lcd.print("GAME OVER!");
  lcd.setCursor(1, 1);
  lcd.print("Score: ");
  lcd.print(score);
}

// REVERTED: Simplified reset logic
void resetGame() {
  score = 0;
  obstacle_x = 15; // Reset obstacle position
  dino_y = GROUND_LEVEL;
  isJumping = false;
  isGameOver = false;
  gameSpeed = 250;
  lcd.clear();
}