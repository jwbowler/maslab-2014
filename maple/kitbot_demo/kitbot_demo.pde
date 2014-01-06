#define MOT_A_DIR 2
#define MOT_A_PWM 1
#define MOT_A_GND 0
#define MOT_B_DIR 7
#define MOT_B_PWM 6
#define MOT_B_GND 5

#define FRAME_SIZE 4

void setMotors(int8 velA, int8 velB);
uint16 calcPwm(int8 inputVel);
boolean calcDir(int8 inputVel);

unsigned int charCount = 0;
char buf[FRAME_SIZE];

void setup() {
  pinMode(BOARD_LED_PIN, OUTPUT);
  pinMode(MOT_A_DIR, OUTPUT);
  pinMode(MOT_A_PWM, PWM);
  pinMode(MOT_A_GND, OUTPUT);
  pinMode(MOT_B_DIR, OUTPUT);
  pinMode(MOT_B_PWM, PWM);
  pinMode(MOT_B_GND, OUTPUT);
  
  digitalWrite(MOT_A_GND, LOW);
  digitalWrite(MOT_B_GND, LOW);
  setMotors(0, 0);
}

void loop() {
  while (SerialUSB.available()) {
    char ch = SerialUSB.read();
    buf[charCount % 4] = ch;
    
    SerialUSB.print(charCount);
    if (charCount == 0 && ch != 'S') {
      continue;  
    }
    
    charCount++;
    
    if (ch == 'E') {
      if (charCount == 4) {
        setMotors(buf[1], buf[2]);
      }
      charCount = 0;
      toggleLED();
    }
  }
}

void setMotors(int8 velA, int8 velB) {
  digitalWrite(MOT_A_DIR, calcDir(velA));
  pwmWrite(MOT_A_PWM, calcPwm(velA));
  digitalWrite(MOT_B_DIR, calcDir(velB));
  pwmWrite(MOT_B_PWM, calcPwm(velB));
}

uint16 calcPwm(int8 inputVel) {
  uint16 inputVelMag = inputVel > 0 ? inputVel : -inputVel;
  uint16 pwm = (inputVelMag == 128) ? 65535 : inputVelMag << 9;
  return pwm;
}

boolean calcDir(int8 inputVel) {
  return (inputVel > 0);
}
