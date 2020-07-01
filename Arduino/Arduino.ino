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
    Serial.println(s);
    int flag = 1;
    if(1 <= s && s <= 19)
    {
      Servo_1.write(180 - (s-1) * 10);
      flag = 0;
      BT.flush();
    }
    if(21 <= s && s <= 39)
    {
      Servo_2.write(180 - (s-21) * 10);
      flag = 0;
      BT.flush();
    }
    if(flag){
      Serial.println("clean");
      BT.flush();
    }
  }
    
}
