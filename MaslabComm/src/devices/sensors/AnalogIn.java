package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class AnalogIn extends Sensor {
	private byte signalPin = 0;
	protected float value = 0;

	public AnalogIn(int i) {
		this.signalPin = (byte) i;
	}
	
	@Override
	public byte getDeviceCode() {
		return 'A';
	}

	@Override
	public byte[] getInitializationBytes() {
		return new byte[] {signalPin};
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
		byte msb = buff.get();
		byte lsb = buff.get();
		value = (msb * 256) + lsb;
	}

	@Override
	public int expectedNumBytesFromMaple() {
		return 2;
	}

	public float getValue() {
		return value;
	}

}
