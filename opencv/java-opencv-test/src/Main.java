import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class Main {

	public static void main (String args[]) {
		// Load the OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Setup the camera
		VideoCapture camera = new VideoCapture();
		camera.open(0);
		
		// Create GUI windows to display camera output and OpenCV output
		int width = (int) (camera.get(Highgui.CV_CAP_PROP_FRAME_WIDTH));
		int height = (int) (camera.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT));
		JLabel cameraPane = createWindow("Camera output", width, height);
		JLabel opencvPane = createWindow("OpenCV output", width, height);

		// Main loop
		Mat rawImage = new Mat();
		while (true) {
			// Wait until the camera has a new frame
			while (!camera.read(rawImage)) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Process the image however you like
			Mat processedImage = ImageProcessor.process(rawImage);
			
			// Update the GUI windows
			updateWindow(cameraPane, rawImage);
			updateWindow(opencvPane, processedImage);
		}
	}
	
    private static JLabel createWindow(String name, int width, int height) {    
        JFrame imageFrame = new JFrame(name);
        imageFrame.setSize(width, height);
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel imagePane = new JLabel();
        imagePane.setLayout(new BorderLayout());
        imageFrame.setContentPane(imagePane);
        
        imageFrame.setVisible(true);
        return imagePane;
    }
    
    private static void updateWindow(JLabel imagePane, Mat mat) {
    	int w = (int) (mat.size().width);
    	int h = (int) (mat.size().height);
    	if (imagePane.getWidth() != w || imagePane.getHeight() != h) {
    		imagePane.setSize(w, h);
    	}
    	BufferedImage bufferedImage = Mat2Image.getImage(mat);
    	imagePane.setIcon(new ImageIcon(bufferedImage));
    }

}