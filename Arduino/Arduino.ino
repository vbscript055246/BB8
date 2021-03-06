#include <Wire.h>
#include <Servo.h>                                                                                                                                                                                                                                                                                                                                        #include <SoftwareSerial.h>
#include <SoftwareSerial.h>

#define KEY 10
#define TXD 11
#define RXD 12

SoftwareSerial BT(TXD, RXD);

#define Servo_1_Data 5
#define Servo_2_Data 6

Servo Servo_1, Servo_2;

void setup() {
  // debug Serial
  Serial.begin(9600);

  // Bluetooth
  BT.begin(9600);

  // Setting Motor

  // SG90 argument
  Servo_1.attach(Servo_1_Data, 500, 2400);
  Servo_2.attach(Servo_2_Data, 500, 2400);

  // Set Angle
  Servo_1.write(90);
  Servo_2.write(90);

  delay(1000);
  Serial.println("Ready!");

}


int s;
void loop() {
  while (BT.available() > 0) {
    //delay(20); // reduce burst error
    // read bluetooth
    s = int(BT.read());
    Serial.println("input");
    Serial.println(char(s));
    if('A' <= s && s <= 'S')
    {
      s -= 'A';
      Servo_1.write(180 - s * 10);
      BT.flush();
    }
    else if('a' <= s && s <= 's')
    {
      s -= 'a';
      Servo_2.write(180 - s * 10);
      BT.flush();
    }
    else{
      Serial.println("clean");
      BT.flush();
    }
  }
    
}
