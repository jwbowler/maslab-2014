#include <stdlib.h>

//#define CONTROL_DEBUG

#ifdef CONTROL_DEBUG
#define D(X) (SerialUSB.print( X ))
#else
#define D(X)
#endif


// control character codes

#define START_INIT 'I'
#define START_STREAM 'S'
#define END ((char) 0xff)
#define POLL_SENSORS ((char) 0xfe)


// device-type definitions and properties, and list of possible device types

typedef struct DeviceType {
  uint8 code;
  WiringPinMode* pinModes;
  uint8 numPins;
  uint8 numRequestBytes;
  uint8 numReturnBytes;
} DeviceType;


#include "devices.h"


#define NOT_A_VALID_DEVICE_TYPE 255

uint8 getDeviceTypeIndex(uint8 code) {
  for (int i = 0; i < NUM_DEVICE_TYPES; i++) {
    if (deviceTypeList[i].code == code) {
      return i;
    }
  }
  return NOT_A_VALID_DEVICE_TYPE;
}


// registered devices and pin numbers to be recorded in the initialization phase
// and read from in the streaming phase

typedef struct Device {
  DeviceType* type;
  uint8 pins[MAX_NUM_INIT_BYTES];
  void* state; // oops, shouldn't have called it "state"... (friday 1:48)
} Device;

Device registeredDevices[1024];
uint8 numRegisteredDevices;
boolean isRegisteredDevicesValid = false;


#include "deviceMethods.h"


// states

#define STATE_IDLE 0
#define STATE_INIT 1
#define STATE_STREAM 2

int state = STATE_IDLE;



uint8 handleStartChar(uint8 ch);
uint8 handleInitChar(uint8 ch);
uint8 handleStreamChar(uint8 ch);
void pollSensors();


void setup() { }

void loop() {
  if (SerialUSB.available()) {
    uint8 ch = SerialUSB.read();
    D((char) ch);
    
    switch (state) {
      
      case STATE_IDLE:
        state = handleStartChar(ch);
        break;
      
      case STATE_INIT:
        state = handleInitChar(ch);
        break;
    
      case STATE_STREAM:
        state = handleStreamChar(ch);
        break;
    
    }
    
    D(state);
  }
}

uint8 handleStartChar(uint8 ch) {
  switch (ch) {
    case START_INIT:
    numRegisteredDevices = 0;
    isRegisteredDevicesValid = false;
    //noInterrupts();
    return STATE_INIT;
   
    case START_STREAM:
    return isRegisteredDevicesValid ? STATE_STREAM : STATE_IDLE;
    
  }
  return STATE_IDLE;
}

uint8 handleInitChar(uint8 ch) {
  switch (ch) {
    
    case END:
    isRegisteredDevicesValid = true;
    //interrupts();
    return STATE_IDLE;
    
    default:
    uint8 deviceTypeIndex = getDeviceTypeIndex(ch);
    if (deviceTypeIndex == NOT_A_VALID_DEVICE_TYPE) {
      return STATE_IDLE;
    }
    Device* device = &(registeredDevices[numRegisteredDevices]);
    device->type = &(deviceTypeList[deviceTypeIndex]);
    for (int i = 0; i < device->type->numPins; i++) {
      uint8 pin = SerialUSB.read();
      device->pins[i] = pin;
      pinMode(pin, device->type->pinModes[i]);
      //SerialUSB.print((char) device->pins[i]);
    }
    //handleInitCommand(device);
    numRegisteredDevices++;
    
  }
  return STATE_INIT;
}

uint8 handleStreamChar(uint8 ch) {
  switch (ch) {
    
    case END:
    return STATE_IDLE;
    
    case POLL_SENSORS:
    pollSensors();
    return STATE_STREAM;
    
    default:
    if (ch >= numRegisteredDevices) {
      return STATE_IDLE; 
    }
    Device* device = &(registeredDevices[ch]);
    uint8 numDataBytes = device->type->numRequestBytes;
    uint8 dataBytes[numDataBytes];
    for (int i = 0; i < numDataBytes; i++) {
      dataBytes[i] = SerialUSB.read();
    }
    handleWriteCommand(device->type->code, device->pins, dataBytes);
  }
  return STATE_STREAM;
}

void pollSensors() {
  SerialUSB.print(START_STREAM);
  for (int i = 0; i < numRegisteredDevices; i++) {
    Device* device = &(registeredDevices[i]);
    handleReadCommand(device->type->code, device->pins, device->state);
  }
  SerialUSB.print(END);
}
