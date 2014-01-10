
// device-type character codes

#define DIGITAL_IN 'D'
#define DIGITAL_OUT 'd'
#define ANALOG_IN 'A'
#define PWM_OUT 'P'
#define CYTRON 'C'
#define ENCODER 'E'
#define GYROSCOPE 'G'
#define ULTRASONIC 'U'


// device-type pin modes

WiringPinMode _digitalInPinModes[] = {INPUT};
WiringPinMode _digitalOutPinModes[] = {OUTPUT};
WiringPinMode _analogInPinModes[] = {INPUT_ANALOG};
WiringPinMode _pwmOutPinModes[] = {PWM};
WiringPinMode _cytronPinModes[] = {OUTPUT, PWM};
WiringPinMode _encoderPinModes[] = {INPUT, INPUT};
WiringPinMode _gyroscopePinModes[] = {OUTPUT};
WiringPinMode _ultrasonicPinModes[] = {INPUT, OUTPUT};


// complete device-type static (i.e. not instance-specific) properties

DeviceType _digitalIn{DIGITAL_IN, _digitalInPinModes, 1, 0, 1};
DeviceType _digitalOut{DIGITAL_OUT, _digitalOutPinModes, 1, 1, 0};
DeviceType _analogIn{ANALOG_IN, _analogInPinModes, 1, 0, 1};
DeviceType _pwmOut{PWM_OUT, _pwmOutPinModes, 1, 1, 0};
DeviceType _cytron{CYTRON, _cytronPinModes, 2, 2, 0};
DeviceType _encoder{ENCODER, _encoderPinModes, 2, 0, 1};
DeviceType _gyroscope{GYROSCOPE, _gyroscopePinModes, 1, 0, 2};
DeviceType _ultrasonic{ULTRASONIC, _ultrasonicPinModes, 2, 0, 2};


// list of all device types

DeviceType deviceTypeList[] = {
  _digitalIn,
  _digitalOut,
  _analogIn,
  _pwmOut,
  _cytron,
  _encoder,
  _gyroscope,
  _ultrasonic
};

#define NUM_DEVICE_TYPES 8
#define MAX_NUM_INIT_BYTES 2
#define MAX_NUM_REQUEST_BYTES 2

/*
// extra fields that device instances will need

typedef struct EncoderState {
  unsigned int ticks;
} EncoderState;

typedef struct GyroscopeState {
} GyroscopeState;

typedef struct UltrasonicState {
  unsigned int startTime;
  unsigned int pulseDuration;
} UltrasonicState;
*/
