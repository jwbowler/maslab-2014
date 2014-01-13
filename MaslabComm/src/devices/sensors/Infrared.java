package devices.sensors;

public class Infrared extends AnalogIn {
	
	public Infrared(int pin) {
		super(pin);
	}
	
	public float getDistance() {
		// TODO: conversion
		return (float) value;
	}

}
