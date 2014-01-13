package devices.sensors;

import java.nio.ByteBuffer;

import devices.Sensor;

public class DigitalIn extends Sensor {

	@Override
	public byte getDeviceCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public byte[] getInitializationBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void consumeMessageFromMaple(ByteBuffer buff) {
		// TODO Auto-generated method stub

	}

	@Override
	public int expectedNumBytesFromMaple() {
		// TODO Auto-generated method stub
		return 0;
	}

}
