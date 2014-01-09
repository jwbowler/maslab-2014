import java.awt.image.BufferedImage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Mat2Image {
    BufferedImage img;
    byte[] dat;
    Mat convMat;
    int bufferedImageType;
    
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    public Mat2Image(int bufferedImageType) {
    	this.bufferedImageType = bufferedImageType;
    }

    public BufferedImage getImage(Mat mat) {
        allocateTempSpace(mat);
        if (mat.type() == CvType.CV_8UC3) {
        	Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
        }
        mat.get(0, 0, dat);
    	img.getRaster().setDataElements(0, 0, img.getWidth(), img.getHeight(), dat);
        return img;
    }
    
    private void allocateTempSpace(Mat mat) {
        int w = mat.cols();
        int h = mat.rows();
        int c = mat.channels();
        System.out.println(w);
        System.out.println(h);
        System.out.println(c);
        if (dat == null || dat.length != w * h * c) {
            dat = new byte[w * h * c];
            System.out.println(1);
        }
        if (img == null || img.getWidth() != w || img.getHeight() != h) {
        	img = new BufferedImage(w, h, bufferedImageType);
        	System.out.println(2);
        }
    }
}