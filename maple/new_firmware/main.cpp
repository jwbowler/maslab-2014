#include <stdlib.h>
#include <wirish/wirish.h>

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
#define RESPONSE 'R'
#define END ((char) 0xff)


//===================================
// HELPER CLASSES
//===================================

class Device {
public:
  virtual void sample() = 0;
  virtual void get() = 0;
  virtual void set() = 0;
};

class SettableDevice : public Device {
public:
  void sample() {}
  void get() {}
};

class SampleableDevice : public Device {
public:
  void set() {}
};

uint8 ultrasonicCount;

class DeviceList {
private:
  Device** devices;
  uint8 devicesArraySize;
  uint8 count;
public:
  DeviceList() {
    devices = NULL;
  }
  
  void init() {
    if (devices != NULL) {
      for (int i = 0; i < count; i++) {
        delete devices[i];
      }
      free(devices);
    }
    
    devicesArraySize = SerialUSB.read();
    devices = (Device**) malloc(sizeof(Device*) * devicesArraySize);
    count = 0;

    ultrasonicCount = 0;
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
    while (true) {
      uint8 deviceIndex = SerialUSB.read();
      if (deviceIndex == END) {
        return;
      }
      if (deviceIndex >= count) {
        while (SerialUSB.read() != END);
        return;
      }
      devices[deviceIndex]->set();
    }
  }

  void add(Device *device) {
    if (count < devicesArraySize) {
      devices[count] = device;
      count++;
    } else {
      // send WTF packet
    }
  }
};

//===================================
// DEVICE IMPLEMENTATIONS
//===================================

#define ANALOG_INPUT_CODE 'A'
#define DIGITAL_INPUT_CODE 'D'
#define DIGITAL_OUTPUT_CODE 'd'
#define CYTRON_CODE 'C'
#define PWM_OUTPUT_CODE 'P'
#define ULTRASONIC_CODE 'U'

class Cytron : public SettableDevice {
private:    
  uint8 dirPin;
  uint8 pwmPin;
public:
  Cytron() {
    dirPin = SerialUSB.read();
    pwmPin = SerialUSB.read();
    pinMode(dirPin, OUTPUT);
    pinMode(pwmPin, PWM);
    setSpeed(0);
  }

  void set() {
    uint8 msb = SerialUSB.read();
    uint8 lsb = SerialUSB.read();
    uint16 speed = msb;
    speed = (speed << 8) + lsb;
    setSpeed(speed);
  }

  void setSpeed(uint16 speed) {
    bool reverse = speed & 0x8000;
    digitalWrite(dirPin, reverse);
    analogWrite(pwmPin, reverse ? 2 * (speed ^ 0xFFFF) : 2 * speed);
  }
};

class AnalogInput : public SampleableDevice {
private:
  uint8 pin;
  uint16 val;
public:
  AnalogInput() {
    pin = SerialUSB.read();
    pinMode(pin, INPUT_ANALOG);
  }
  void sample() {
    val = analogRead(pin);
  }
  void get() {
    uint8 msb = val >> 8;
    uint8 lsb = val;
    SerialUSB.write(msb);
    SerialUSB.write(lsb);
  }
};

class PwmOutput : public SettableDevice {
private:
  uint8 pin;
  uint16 val;
public:
  PwmOutput() {
    pin = SerialUSB.read();
    pinMode(pin, PWM);
  }
  void set() {
    uint8 msb = SerialUSB.read();
    uint8 lsb = SerialUSB.read();
    uint16 dutyCycle = msb;
    dutyCycle = (dutyCycle << 8) + lsb;
    pwmWrite(pin, dutyCycle);
  }
}

class DigitalInput : public SampleableDevice {
private:
  uint8 pin;
  bool val;
public:
  DigitalInput() {
    pin = SerialUSB.read();
    pinMode(pin, INPUT);
  }
  void sample() {
    val = digitalRead(pin);
  }
  void get() {
    SerialUSB.write(val);
  }
}

class DigitalOutput : public SettableDevice {
private:
  uint8 pin;
public:
  DigitalOutput() {
    pin = SerialUSB.read();
    pinMode(pin, OUTPUT);
  }
  void set() {
    // note that the uint8 is used here as a boolean
    uint8 value = SerialUSB.read();
    digitalWrite(pin, value);
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
                                                
class Ultrasonic;

Ultrasonic *ultrasonics[8];

class Ultrasonic : public SampleableDevice {
private:
  uint8 echoPin;
  uint8 triggerPin;

  uint32 startTime;
  bool isEchoLow;
  bool receivedEcho;

  uint16 val;

public:
  Ultrasonic() {
    echoPin = SerialUSB.read();
    delay(20);
    triggerPin = SerialUSB.read();
    delay(20);
    
    digitalWrite(triggerPin,LOW);

    ultrasonics[ultrasonicCount] = this;
    ultrasonicCount++;
    //attachInterrupt(echoPin, ultrasonicISRList[ultrasonicCount], CHANGE);
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
    SerialUSB.write(msb);
    SerialUSB.write(lsb);
  }
};

void ultrasonicISR(uint8 index) {
  ultrasonics[index]->localISR();
}


//===================================
// LOGIC
//===================================

void firmwareInit();
void get();
void set();

bool initStatus;
DeviceList deviceList;

void setup() {
  pinMode(LED_PIN, OUTPUT);
  initStatus = false;
}

void loop() {
  //sample sensors and buffer
  char header = SerialUSB.read();
  switch (header) {
  case INIT:
    firmwareInit();
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
    break;
  }
}


void firmwareInit() {
  initStatus = false;
  deviceList.init();

  uint8 deviceCode;
  while (true) {
    deviceCode = SerialUSB.read();
    switch (deviceCode) {
    case ANALOG_INPUT_CODE:
      deviceList.add(new AnalogInput());
      break;
    case CYTRON_CODE:
      deviceList.add(new Cytron());
      break;
    case ULTRASONIC_CODE:
      deviceList.add(new Ultrasonic());
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

  deviceList.sample();
  
  SerialUSB.write(RESPONSE);
  deviceList.get();
  SerialUSB.write(END);
}

void set() {
  if (!initStatus) {
    // send WTF packet
    return;
  }

  deviceList.set();
}




// Force init to be called *first*, i.e. before static object allocation.
// Otherwise, statically allocated objects that need libmaple may fail.
__attribute__((constructor)) void premain() {
    init();
}

int main(void) {
    setup();

    while (true) {
        loop();
    }

    return 0;
}
