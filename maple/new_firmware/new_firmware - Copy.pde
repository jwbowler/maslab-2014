#include <stdlib.h>

/* features 
 [] serial comm
 [] dagu motor controller
 [] cytron motor controller
 [] encoders

 [] servo
 [] hc-sr04 sonic range finder
 [] analog read
 [] analog write
 [] digital read
 [] digital write
 */

#define LED_PIN BOARD_LED_PIN

#define INIT 'I'
#define GET 'G'
#define SET 'S'
#define END = ((char) 0xff)

//===================================
// HELPER CLASSES
//===================================

class Device {
public:
  virtual void sample();
  virtual void get();
  virtual void set();
}

class SettableDevice : Device{
public:
  void sample() {}
  void get() {}
}

class SampleableDevice : Device {
public:
  void set() {}
}

class DeviceList {
private:
  Device** devices;
  uint8 count;
public:
  void init() {
    for (int i = 0; i < count; i++) {
      free(devices[i]);
    }
    free(devices);

    devices = (Device**) malloc(sizeof(Device*) * SerialUSB.read());
    count = 0;
  }

  void sample() {
    for (int i = 0; i < count; i++) {
      devices[i]->sample();
    }
  }

  void get() {
    for (int i = 0; i < count; i++) {
      devices[i]->get();
    }
  }

  void set() {
    devices[SerialUSB.read()]->set();
  }

  void add(Device *device) {
    devices[count] = device;
    count++;
  }
}


//===================================
// DEVICE IMPLEMENTATIONS
//===================================

class Cytron : SettableDevice {
private:
  uint8 pwmPin;
  uint8 dirPin;
public:
  Cytron() {
    pwmPin = SerialUSB.read();
    dirPin = SerialUSB.read();
    pinMode(pwmPin, PWM);
    pinMode(dirPin, OUTPUT);
    setSpeed(0);
  }

  void set() {
    uint16 speed = SerialUSB.read();
    speed = (speed << 8) + SerialUSB.read();
    setSpeed(speed);
  }

  void setSpeed(uint16 speed) {
    bool reverse = speed & 0x8000;
    digitalWrite(dirPin, reverse);
    analogWrite(pwmPin, reverse ? 2 * (speed ^ 0xFFFF) : 2 * speed);
  }
}

class AnalogInput : SampleableDevice {
private:
  uint8 pin;
  uint16 val;
public:
  AnalogInput() {
    pin = SerialUSB.read();
  }
  void sample() {
    val = analogRead(pin);
  }
  void get() {
    uint8 msb = val >> 8;
    uint8 lsb = val;
    SerialUSB.print(msb);
    SerialUSB.print(lsb);
  }
}

void ultrasonicISR(uint8 index);

void ultrasonicISR0() { ultrasonicISR(0); }
void ultrasonicISR1() { ultrasonicISR(1); }
void ultrasonicISR2() { ultrasonicISR(2); }
void ultrasonicISR3() { ultrasonicISR(3); }
void ultrasonicISR4() { ultrasonicISR(4); }
void ultrasonicISR5() { ultrasonicISR(5); }
void ultrasonicISR6() { ultrasonicISR(6); }
void ultrasonicISR7() { ultrasonicISR(7); }

typedef void (*UltrasonicISRPtr)();
UltrasonicISRPtr ultrasonicISRList[8] = {&ultrasonicISR0,
                                                &ultrasonicISR1,
                                                &ultrasonicISR2,
                                                &ultrasonicISR3,
                                                &ultrasonicISR4,
                                                &ultrasonicISR5,
                                                &ultrasonicISR6,
                                                &ultrasonicISR7};

class Ultrasonic : SampleableDevice {
private:
  uint8 triggerPin;
  uint8 echoPin;

  uint32 startTime;
  bool isEchoLow;
  bool receivedEcho;

  uint16 val;

public:
  static uint8 count;
  static Ultrasonic *ultrasonics[8];

  Ultrasonic() {
    triggerPin = SerialUSB.read();
    echoPin = SerialUSB.read();
    
    digitalWrite(triggerPin,LOW);

    ultrasonics[count] = this;
    count++;
    attachInterrupt(echoPin, ultrasonicISRList[count], CHANGE);
  }


  void localISR() {
    if (isEchoLow) {
      startTime = micros();
      isEchoLow = false;
      receivedEcho = true;
    } else {
      val = micros() - startTime;
      isEchoLow = true;
    }
  }

  void sample() {
    if (!receivedEcho) {
      if (micros()-startTime < 60000) {
        return;
      }
    }

    if (!isEchoLow) {
      return;
    }

    digitalWrite(triggerPin,HIGH);
    delayMicroseconds(10);
    digitalWrite(triggerPin,LOW);
    startTime = micros();

    isEchoLow = true;
    receivedEcho = false;
  }

  void get() {
    uint8 msb = val >> 8;
    uint8 lsb = (uint8) val;
    SerialUSB.print(msb);
    SerialUSB.print(lsb);
  }
}

Ultrasonic::count = 0;
void ultrasonicISR(int index) {
  Ultrasonic::ultrasonics[index]->localISR();
}

//===================================
// LOGIC
//===================================

void init();
void get();
void set();

bool initStatus = false;
DeviceList *deviceList;

void setup() {
  pinMode(LED_PIN, OUTPUT);
  deviceList = new DeviceList();
}

void loop() {
  //sample sensors and buffer
  uint8 header = SerialUSB.read();
  switch (header) {
  case INIT:
    init();
    break;
  case GET:
    get();
    break;
  case SET:
    set();
    break;
  case END:
    break;
  default:
    while (SerialUSB.read() != END);
  }
}

#define ANALOG_INPUT_CODE 'A'
#define CYTRON_CODE 'C'
#define ULTRASONIC_CODE 'U'

void init() {
  initStatus = false;
  deviceList->init();

  uint8 deviceCode;
  while (true) {
    deviceCode = SerialUSB.read();
    switch (deviceCode) {
    case ANALOG_INPUT_CODE:
      deviceList->add(new AnalogInput());
      break;
    case CYTRON_CODE:
      deviceList->add(new Cytron());
      break;
    case ULTRASONIC_CODE:
      deviceList->add(new Ultrasonic());
      break;
    case END:
      initStatus = true;
      return;
    default:
      while (SerialUSB.read() != END);
      return;
    }
  }
}

void get() {
  if (!initStatus) {
    // send WTF packet
    return;
  }

  deviceList->sample();
  deviceList->get();
}

void set() {
  if (!initStatus) {
    // send WTF packet
    return;
  }

  deviceList->set();
}
