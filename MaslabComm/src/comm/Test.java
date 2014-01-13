package comm;

import devices.actuators.Cytron;
import devices.sensors.Gyroscope;
import devices.sensors.Infrared;

public class Test {

	public static void main(String[] args) {
		new Test();
		System.exit(0);
	}

	public Test() {
		// MapleComm comm = new MapleComm(MapleIO.SerialPortType.SIMULATION);
		MapleComm comm = new MapleComm(MapleIO.SerialPortType.LINUX);

		Cytron motor1 = new Cytron(4, 5);
		Cytron motor2 = new Cytron(6, 7);
		Infrared infra1 = new Infrared(0);
		Gyroscope gyro = new Gyroscope(2, 9);

		comm.registerDevice(motor1);
		comm.registerDevice(motor2);
		comm.registerDevice(infra1);
		comm.registerDevice(gyro);

		comm.initialize();

		motor1.setSpeed(0.2);
		motor2.setSpeed(-0.4);
		
		comm.transmit();

		float speed = 0;

		while (true) {
			
			comm.updateSensorData();

			motor1.setSpeed(speed);
			motor2.setSpeed(-1 + speed);

			comm.transmit();

			speed += .1;
			if (speed >= 1) {
				speed = 0;
			}
			
			System.out.println(infra1.getDistance() + " " + gyro.getOmega());

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { }
		}
	}
}
