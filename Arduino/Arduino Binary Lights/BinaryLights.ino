#include <IRremote.h>

// --- Hardware Pins ---
#define IR_RECEIVE_PIN 11
const int ledPins[] = {2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, A0};

// --- Global Variable ---
long currentNumber = 0;

// === YOUR NEW CODES ARE HERE ===
#define FINALIZE_CODE 0xBF40FF00  // Your "Enter" button
#define CLEAR_CODE    0xBA45FF00  // Your "Clear" button


void setup() {
  Serial.begin(9600);
  IrReceiver.begin(IR_RECEIVE_PIN, DISABLE_LED_FEEDBACK);

  for (int i = 0; i < 12; i++) {
    pinMode(ledPins[i], OUTPUT);
  }
  
  Serial.println("Multi-Digit Binary Counter Ready.");
  Serial.println("Type a number (0-4095) and press 'Finalize'.");
}

void loop() {
  if (IrReceiver.decode()) {
    
    int digitPressed = -1; // -1 means no digit was pressed

    // Switch statement to find out WHAT was pressed
    switch (IrReceiver.decodedIRData.decodedRawData) {
      // Your number buttons
      case 0xE916FF00: digitPressed = 0; break;
      case 0xF30CFF00: digitPressed = 1; break;
      case 0xE718FF00: digitPressed = 2; break;
      case 0xA15EFF00: digitPressed = 3; break;
      case 0xF708FF00: digitPressed = 4; break;
      case 0xE31CFF00: digitPressed = 5; break;
      case 0xA55AFF00: digitPressed = 6; break;
      case 0xBD42FF00: digitPressed = 7; break;
      case 0xAD52FF00: digitPressed = 8; break;
      case 0xB54AFF00: digitPressed = 9; break;
      // You can add your '0' button code here too
      
      // Your special function buttons
      case FINALIZE_CODE:
        Serial.print("Finalizing. Displaying: ");
        Serial.println(currentNumber);
        displayBinary(currentNumber); // Send the final number to the LEDs
        currentNumber = 0; // Reset for the next number
        break;

      case CLEAR_CODE:
        Serial.println("Cleared.");
        currentNumber = 0; // Reset the buffer
        displayBinary(0);  // Turn off all lights
        break;
    }

    // Logic to handle building the number
    if (digitPressed != -1) {
      // Check if the number will be too big (max 4095)
      if (currentNumber >= 409) { 
        currentNumber = 4095; // Cap it at the max
      } else {
        currentNumber = (currentNumber * 10) + digitPressed;
      }
      
      Serial.print("Current Number: ");
      Serial.println(currentNumber);
    }
    
    IrReceiver.resume(); 
  }
}

void displayBinary(long n) { 
  Serial.print("Displaying binary for: ");
  Serial.println(n);
  
  // This loop runs 12 times
  for (int i = 0; i < 12; i++) {
    if ((n >> i) & 1) {
      digitalWrite(ledPins[i], HIGH);
    } else {
      digitalWrite(ledPins[i], LOW);
    }
  }
}