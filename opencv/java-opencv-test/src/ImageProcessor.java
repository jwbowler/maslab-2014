import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class ImageProcessor {
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	// Input: an image from the camera
	// Output: the OpenCV-processed image
	
	// (In practice it's a little different:
	//  the output image will be for your visual reference,
	//  but you will mainly want to output a list of the locations of detected objects.)
	public static Mat process(Mat rawImage) {
		Mat processedImage = new Mat();
		Imgproc.blur(rawImage, processedImage, new Size(9, 9));
		return processedImage;
	}
	
}
