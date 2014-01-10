

// utility functions

void serialPrintDigitalValue(boolean val) {
  SerialUSB.print(val ? (char) 0x01 : (char) 0x00);
}

void serialPrintAnalogValue(uint16 val) {
  SerialUSB.print((char) (val >> 8));
  SerialUSB.print((char) (val));
}

/*
// lists of devices of given type

Device* ultrasonicList[16];
int ultrasonicListSize = 0;


// ISR's

void ultrasonicISR(uint8 index) {
  UltrasonicState* state = (UltrasonicState*) ultrasonicList[index]->state;
  state->pulseDuration = index + 17;
}

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


// special procedures for initializing devices (besides setting pin modes)

void handleInitCommand(Device* device) {
  switch (device->type->code) {
   
    case CYTRON:
    break;
    
    case ENCODER:
    // TODO: register interrupt
    break;
    
    case GYROSCOPE:
    // TODO: register interrupt
    break;
    
    case ULTRASONIC:
    device->state = malloc(sizeof(UltrasonicState));
    UltrasonicState* state = (UltrasonicState*) (device->state);
    state->pulseDuration = 3;
    ultrasonicList[ultrasonicListSize] = device;
    attachInterrupt(device->pins[0], *(ultrasonicISRList[ultrasonicListSize]), CHANGE);
    ultrasonicListSize++;
    break;
    
  }
}
*/

// device-specific code for sensors

void handleReadCommand(uint8 devCode, uint8* pins, void* state) {
  switch (devCode) {
   
    case DIGITAL_IN:
    serialPrintDigitalValue(digitalRead(pins[0]));
    break;
    
    case ANALOG_IN:
    //serialPrintAnalogValue(10);
    serialPrintAnalogValue(analogRead(pins[0]));
    break;
    
    case ULTRASONIC:
    serialPrintAnalogValue(20);
    //UltrasonicState* ustate = (UltrasonicState*) state;
    //serialPrintAnalogValue((uint16) ustate->pulseDuration);
    break;
    
    
  }
}


// device-specific code for actuators

void cytronWrite(uint8* pins, uint8* command);

void handleWriteCommand(uint8 devCode, uint8* pins, uint8* command) {
  switch (devCode) {
    
    case DIGITAL_OUT:
    digitalWrite(pins[0], command[0]);
    break;
    
    case PWM_OUT:
    pwmWrite(pins[0], command[0]);
    break;
    
    case CYTRON:
    cytronWrite(pins, command);
    break;
    
  }
}

void cytronWrite(uint8* pins, uint8* command) {
  boolean isReverse = command[0] & 0x80;
  uint16 inputSignal = ((((uint16) (command[0])) << 8) + command[1]);
  uint16 convertedPWM = isReverse ? -2 * inputSignal : 2 * inputSignal;
  if (inputSignal == 0x8000) { convertedPWM = 0xffff; }
  
  digitalWrite(pins[0], isReverse);
  pwmWrite(pins[1], convertedPWM);
}
