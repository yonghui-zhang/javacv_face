package video;

import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;
/*
 * 调用摄像头
 */
public class CameraDemo {
	/*
	 * 绘制人脸装饰套件
	 */
	public void loadFaceImage(String imagepath, Rect face_i, Mat mat, int x_weight, int y_weight)
	{
		Mat logoMat = opencv_imgcodecs.imread(imagepath);
		Mat mask =  opencv_imgcodecs.imread(imagepath, 0);
		/********绘制套件的起始坐标*******/
		int x = face_i.tl().x() + x_weight;
		int y = face_i.tl().y() + y_weight;
	    Mat imageROI = null;
	    if(x+logoMat.cols() < mat.cols() && y+logoMat.rows() < mat.rows())
	    {
	         imageROI = mat.apply(new Rect(x, y, logoMat.cols(), logoMat.rows()));
	   	     logoMat.copyTo(imageROI, mask);  
	    }
	}
	/*
	 * 人脸识别
	 */
	public void findFace() throws Exception
	{
		OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		grabber.start();//开始获取摄像头数据
		CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
		canvas.setSize(600, 400);
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setAlwaysOnTop(true);
		
		/**********人脸识别设置*******/
	    CascadeClassifier face_cascade = new CascadeClassifier(
	                "D:\\haarcascade_frontalface_alt.xml");// haarcascade_frontalface_alt.xml
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 /*************水印文字位置********/
        Point point = new Point(0, 20);
        /**********颜色*********/
        Scalar scalar = new Scalar(0, 0, 255, 0);
        Frame frame = null;
        int tiger = 0;//第几张老虎脸
		while(true)
		{
			if(!canvas.isDisplayable())
			{
				//窗口是否关闭
				grabber.stop();//停止抓取
				System.exit(2);//退出
				
			}
			 frame = grabber.grab();
			/*********添加时间显示的水印*************/
	    	 Mat mat = converter.convertToMat(frame);
	    	 
	    	 opencv_imgproc.putText(mat, sdf.format(new Date()), point, opencv_imgproc.CV_FONT_ITALIC, 0.8, scalar, 2, 20, false);
	    	 
	    	 Mat videoMatGray = new Mat();
	         cvtColor(mat, videoMatGray, COLOR_BGRA2GRAY);
	         Point p = new Point();
	         RectVector faces = new RectVector();
	         face_cascade.detectMultiScale(videoMatGray, faces);
	       
	         tiger = tiger + 1;
	         if(tiger >= 9)
	         {
	        	 tiger = 1;
	         }
	         /************画N张人脸*****************/
	         for (int i = 0; i < faces.size(); i++) {
	                Rect face_i = faces.get(i);
	                Mat face = new Mat(videoMatGray, face_i);             
	                rectangle(mat, face_i, new Scalar(0, 255, 0, 1));
	                int x = face_i.tl().x();
	                int y = face_i.tl().y(); 
	                System.out.println("脸长：" + face_i.width() + ",脸宽:" + face_i.height());
	                /*********添加头像挂件********/
	                
	                /*********胡子位置**********/
	                 loadFaceImage("D:\\hu2.png", face_i, mat, 30, face_i.height()/2);
	                /**********添加眼镜***************/ 
	                 loadFaceImage("D:\\eye.png", face_i, mat, 0, 30);
	                /*********添加动态老虎脸*********/
	                 String tpath = "D:\\tiger\\tige" + tiger + ".png";
	                loadFaceImage(tpath, face_i, mat, face_i.width(), 0);
	         }
	         
	    	frame = converter.convert(mat);
			canvas.showImage(frame);//获取摄像头图像并放到窗口上显示，表示是一帧图像
		//	Thread.sleep(40);//25帧图像
		}
	}

	public static void main(String[] args) throws Exception, InterruptedException {
	
		CameraDemo camera = new CameraDemo();
		camera.findFace();
	}
		
}
