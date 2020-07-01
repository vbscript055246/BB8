#define KEY 10
#define RXD 11
#define TXD 12
#include <SoftwareSerial.h>
#include <Wire.h>
#include <Servo.h>

// Servo motor
#define Servo_1_Data 5
#define Servo_2_Data 6
Servo Servo_1, Servo_2;
int pos_1 = 0, pos_2 = 0;


SoftwareSerial BT(RXD, TXD);

void setup() {
  BT.begin(9600);
  Serial.begin(9600);
  
  //setting motor
  pinMode(KEY, OUTPUT);
  digitalWrite(KEY, LOW);
  
  //testing motor
  Servo_1.attach(Servo_1_Data,500,2400);
  Servo_2.attach(Servo_2_Data,500,2400);
  Servo_1.write(90);
  Servo_2.write(90);
  delay(3000);
}

void loop() {
  int i, PW=90;
  while(1){
    while(BT.available()>0){
      i = BT.read();
      
      if(i == '1'){ //set to 180
        Servo_1.write(90+PW);
        Serial.println(1);
      }
      else if(i == '2'){ //set to 0
        Servo_1.write(90-PW);
        Serial.println(2);
      }
      else if(i == '4'){ //set to 180
        Servo_2.write(90+PW);
        Serial.println(4);
      }
      else if(i == '3'){ //set to 0
        Servo_2.write(90-PW);
        Serial.println(3);
      }
      else if(i == '0'){ //set both to 90
        Servo_2.write(90);
        Servo_1.write(90);
        Serial.println(0);
      }
      else if(i == 'B'){ //set both to 90
        PW = 90;
        Serial.println(0);
      }
      else if(i == 'S'){
        PW = 45;
        Serial.println('S');
      }
      else if(i == 'N'){
        Servo_1.write(90);
        Serial.println('N');
      }
      else if(i == 'M'){
        Servo_2.write(90);
        Serial.println('M');
      }
      else{
        Serial.println(i);
      }
    }
  }
}
