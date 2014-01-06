#include <Servo.h>

// TODO:
// [ ] Serial Comm
// [ ] Dagu Motor Controller
// [X] Cytron Motor Controller
// [X] Encoders

// [X] Servo
// [X] HC-SR04 Sonic Range Finder
// [X] Analog read
// [X] Analog write
// [X] Digital read
// [X] Digital write

#define LED_PIN BOARD_LED_PIN

void setup() {
  /* Set up the LED to blink  */
  pinMode(LED_PIN, OUTPUT);
}

void loop() {
  delay(100);
  while (SerialUSB.available()) {
    char input = SerialUSB.read();
    SerialUSB.println(input);
    toggleLED();
  }
}

/*
// Cytron Motor Controller
unsigned int pwm = 32000;
boolean dir = true;

void setup() {
  pinMode(3, PWM);
  pinMode(4, OUTPUT);
}

void loop() {
  digitalWrite(4, dir);
  pwmWrite(3, pwm);
  delay(100);
  SerialUSB.println('A');
}
*/

/*
volatile unsigned int count = 0;

void irh() {
  if ( digitalRead(28)==HIGH )
    count++;
  else
    count--;
}

void setup() {
  noInterrupts();
  
  // Set up the built-in LED pin as an output:
  pinMode(24, PWM);
  pinMode(26, INPUT);
  pinMode(28, INPUT);

  attachInterrupt(26,irh,RISING);
  interrupts();
}

void loop() {
  pwmWrite(24,count*10);
}
*/

/*
// Encoder rig
volatile unsigned int count = 0;

void irh() {
  if ( digitalRead(28)==HIGH )
    count++;
  else
    count--;
}

void setup() {
  noInterrupts();
  
  // Set up the built-in LED pin as an output:
  pinMode(24, PWM);
  pinMode(26, INPUT);
  pinMode(28, INPUT);

  attachInterrupt(26,irh,RISING);
  interrupts();
}

void loop() {
  pwmWrite(24,count*10);
}
*/

/*
// Sonic Range Finder
volatile unsigned int start = 0;
volatile unsigned int endx = 0;

void irh() {
  if ( digitalRead(28)==HIGH ) {
    start = micros();
  } else {
    endx = micros();
  }
}

void setup() {
  noInterrupts();
  
  // Set up the built-in LED pin as an output:
  pinMode(24, PWM);
  pinMode(28, INPUT);
  pinMode(30, OUTPUT);
  
  attachInterrupt(28,irh,CHANGE);
  interrupts();
}

void loop() {
  digitalWrite(30,HIGH);
  delayMicroseconds(10);
  digitalWrite(30,LOW);
  delay(60);
  
  pwmWrite(24,(endx-start)*3/2);
  SerialUSB.println(endx-start);
}
*/

/*
// Servo
Servo servo;

void setup() {
  servo.attach(1);
  
  // Set up the built-in LED pin as an output:
  //pinMode(24, PWM);
}

int i = 0;
void loop() {
  //pwmWrite(24,i);
  servo.write(i);
  delay(1000);
  i += 5;
  if ( i > 180 ) i = 0;
}
*/

/*
// Analog Write
void setup() {
    // Set up the built-in LED pin as an output:
    pinMode(24, PWM);
}

int i = 0;
void loop() {
  pwmWrite(24,i*100);
  delay(10);
  i += 2;
}
*/


/*
// Analog Read
// Requires port labeled with AIN (from what I can tell)
void setup() {
  // Set up the built-in LED pin as an output:
  pinMode(24, PWM);
  pinMode(20, INPUT_ANALOG);
}

int i = 0;
void loop() {
  int val = analogRead(20);
  pwmWrite(24,i);
  delay(50);
  i += val;
}
*/

/*
// Digital Write
void setup() {
    // Set up the built-in LED pin as an output:
    pinMode(24, OUTPUT);
}

int i = 0;
void loop() {
  digitalWrite(24,HIGH);
  delay(500);
  digitalWrite(24,LOW);
  delay(500);
}
*/

/*
// Digital Read
void setup() {
  // Set up the built-in LED pin as an output:
  pinMode(24, OUTPUT);
  pinMode(23, INPUT);
}

int i = 0;
void loop() {
  int val = digitalRead(23);
  digitalWrite(24,val);
  delay(50);
}
*/
