#include <SPI.h>
#include <MFRC522.h>

#define RST_PIN         9          // Configurable, see typical pin layout above
#define SS_1_PIN        10
#define SS_2_PIN        8

#define ALERT_HIGH  5
#define ALERT_LOW   4

#define NUMBER_OF_READERS   2

byte ssPins[] = {SS_1_PIN, SS_2_PIN};

MFRC522 mfrc522[NUMBER_OF_READERS];   // Create MFRC522 instance.

/**
 * Initialize.
 */
void setup() {

  Serial.begin(9600); // Initialize serial communications with the PC
  while (!Serial);    // Do nothing if no serial port is opened (added for Arduinos based on ATMEGA32U4)
  SPI.begin();        // Init SPI bus

  for (byte i = 0; i < NUMBER_OF_READERS; i++) {
    mfrc522[i].PCD_Init(ssPins[i], RST_PIN); // Init each MFRC522 card
    Serial.print(F("Reader "));
    Serial.print(i);
    Serial.print(F(": "));
    mfrc522[i].PCD_DumpVersionToSerial();
  }
  pinMode(ALERT_HIGH, OUTPUT);
  pinMode(ALERT_LOW, OUTPUT);
  digitalWrite(ALERT_LOW, LOW);
  
}

/**
 * Main loop.
 */
void loop() {

  for (byte reader = 0; reader < NUMBER_OF_READERS; reader++) {
    // Look for new cards

    if (mfrc522[reader].PICC_IsNewCardPresent() && mfrc522[reader].PICC_ReadCardSerial()) {
      triggerAlert(true);
//      Serial.print(F("Reader "));
      Serial.print("R");
      Serial.print(reader);
      Serial.print(" ");
      // Show some details of the PICC (that is: the tag/card)
//      Serial.print(F(": Card UID:"));
      dump_byte_array(mfrc522[reader].uid.uidByte, mfrc522[reader].uid.size);
      Serial.println();
//      Serial.println();
//      Serial.print(F("PICC type: "));
//      MFRC522::PICC_Type piccType = mfrc522[reader].PICC_GetType(mfrc522[reader].uid.sak);
//      Serial.println(mfrc522[reader].PICC_GetTypeName(piccType));

      // Halt PICC
      mfrc522[reader].PICC_HaltA();
      // Stop encryption on PCD
      mfrc522[reader].PCD_StopCrypto1();
      triggerAlert(false);
    } //if (mfrc522[reader].PICC_IsNewC
  } //for(uint8_t reader
}

/**
 * Helper routine to dump a byte array as hex values to Serial.
 */
void dump_byte_array(byte *buffer, byte bufferSize) {
  for (byte i = 0; i < bufferSize; i++) {
    Serial.print(buffer[i] < 16 ? "0" : "");
    Serial.print(buffer[i], HEX);
  }
}

void triggerAlert(bool state){
  if (state == true) {
    digitalWrite(ALERT_HIGH, HIGH);
  } else {
    digitalWrite(ALERT_HIGH, LOW);
  }
//  alertState = !alertState;
  
}
