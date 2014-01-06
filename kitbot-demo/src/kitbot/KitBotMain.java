package kitbot;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import jssc.SerialPort;
import jssc.SerialPortException;


public class KitBotMain {
	
    public static void main(String[] args) {
    	int width = 1366;
    	int height = 768;
    	
    	try {
    		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    		width = dim.width;
    		height = dim.height;
    	} catch ( Exception e ) {
    		System.out.println( e );
    	}
    	
    	JFrame window = new JFrame("Kit Bot Interface");
    	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	window.setSize(width,height);
    	window.setVisible(true);
    	
    	KitBotModel model = new KitBotModel();
    	KitBotView view = new KitBotView(width,height,window);
    	KitBotController controller = new KitBotController(model,view);
    	
    	model.setMotorA(1);
    	model.setMotorA(0);
    	model.setMotorA(-1);
    	model.setMotorA(0.5);
    	model.setMotorA(-0.5);
    }
    
}
