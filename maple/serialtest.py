import serial
import time

ser = serial.Serial('/dev/ttyACM0', 115200)

def read():
    print ser.read()

while True:
    ser.write('S')
    read()
    ser.write('\x00')
    read()
    ser.write('\x00')
    read()
    ser.write('E')
    read()
    
    time.sleep(1)
    
