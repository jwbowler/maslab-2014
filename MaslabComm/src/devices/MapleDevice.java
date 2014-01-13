package devices;

import java.nio.ByteBuffer;

public abstract class MapleDevice {
	
	public abstract byte getDeviceCode();
	
	public abstract byte[] getInitializationBytes();

	// Generates data for output stream (from Java to Maple)
	abstract public byte[] generateCommandToMaple();

	// Consumes and stores local byte data from stream (from Maple to Java)
	abstract public void consumeMessageFromMaple(ByteBuffer buff);

	// Size of consume operation
	abstract public int expectedNumBytesFromMaple();

}
