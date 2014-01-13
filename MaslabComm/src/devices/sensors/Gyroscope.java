package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class Gyroscope extends Sensor {
	byte spiPort;
	byte ssPin;
	int omega;
	
	public Gyroscope(int spiPort, int ssPin) {
		this.spiPort = (byte) spiPort;
		this.ssPin = (byte) ssPin;
	}

	@Override
	public byte getDeviceCode() {
		return 'Y';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {spiPort, ssPin};
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
		byte msb = buff.get();
		byte lsb = buff.get();
		// TODO: conversion
		omega = (msb * 256) + lsb;
	}

	@Override
	public int expectedNumBytesFromMaple() {
		return 2;
	}
	
	public int getOmega() {
		return omega;
	}

}
