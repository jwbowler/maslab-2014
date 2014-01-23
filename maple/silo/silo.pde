#define IR_PIN 5
#define SERVO_PIN 0

#define TIMESTEP 100
#define IR_TIMEOUT 1000
#define SERVO_PWM_LEFT (29 << 11)
#define SERVO_PWM_CENTER (18 << 11)
#define SERVO_PWM_RIGHT (1 << 11)
#define SERVO_PAUSE_TIME 500

void dispenseLeft();
//void dispenseRight();

boolean isBallInLeft = false;
boolean isJustDispensedLeft = false;
int timeLeftEmpty = 0;
//boolean isBallInRight = false;
//int timeRightEmpty = 0;

void setup() {
    pinMode(BOARD_LED_PIN, OUTPUT);
    pinMode(IR_PIN, INPUT);
    pinMode(SERVO_PIN, PWM);
    pwmWrite(SERVO_PIN, SERVO_PWM_CENTER);
    isJustDispensedLeft = false;
}

void loop() {
    isBallInLeft = !digitalRead(IR_PIN);
    digitalWrite(BOARD_LED_PIN, isBallInLeft);
    if (isJustDispensedLeft && isBallInLeft) {
      isJustDispensedLeft = false;
    } else if (isJustDispensedLeft) {
      return;
    } else if (isBallInLeft) {
      timeLeftEmpty = 0;
    } else if (timeLeftEmpty >= IR_TIMEOUT) {
      dispenseLeft();
      timeLeftEmpty = 0;
    } else {
      isJustDispensedLeft = false;
      timeLeftEmpty += TIMESTEP;
    }
    delay(TIMESTEP);
}

void dispenseLeft() {
  pwmWrite(SERVO_PIN, SERVO_PWM_LEFT);
  delay(SERVO_PAUSE_TIME);
  pwmWrite(SERVO_PIN, SERVO_PWM_CENTER);
  timeLeftEmpty += SERVO_PAUSE_TIME;
  //timeRightEmpty += SERVO_PAUSE_TIME;
  isJustDispensedLeft = true;
}
