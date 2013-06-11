package org.opencv.samples.facedetect;

import org.opencv.android.JavaCameraView;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;

public class JavaCameraBallView extends JavaCameraView implements Camera.PreviewCallback{
	private Bitmap bitmap;
	private float ballX, ballY;
	private float ballVelX, ballVelY;
	private int ballWidth, ballHeight;
    private FdActivity act;
    private boolean setPosition;
    private Circle mainBall;
    
	public JavaCameraBallView(android.content.Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_ball_75);

		//ballX, ballY set in on Draw so we can access canvas dimensions
		
		//dimensions of ball are hard coded.  Hopefully can be changed later
		ballWidth = 75;
		ballHeight = 75;
		
		ballVelX = 9.0f;
		ballVelY = 7.0f;
		
		setPosition = true;
		
		getHolder().addCallback(this);
//		ballThread = new BallThread(getHolder(), this);
	}

	public JavaCameraBallView(android.content.Context context, int cameraId) {
		super(context, cameraId);
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_ball_75);

		//ballX, ballY set in on Draw so we can access canvas dimensions
		
		//dimensions of ball are hard coded.  Hopefully can be changed later
		ballWidth = 75;
		ballHeight = 75;
		
		ballVelX = 7.0f;
		ballVelY = 5.0f;
		
		setPosition = true;
		
		getHolder().addCallback(this);
//		ballThread = new BallThread(getHolder(), this);
	}	
	
	@Override
	public void onPreviewFrame(byte[] frame, Camera cam) {
		mCamera.setPreviewCallback(this);
		Canvas canvas = this.getHolder().lockCanvas();
		
		if(setPosition)
		{
			//set start position in middle. doesn't seem like middle in practice for some reason
			ballX = canvas.getWidth() / 2 - (ballWidth / 2);
			ballY = canvas.getHeight() / 2 - (ballHeight / 2);
			setPosition = false;
		}
		
		Mat grayFrame = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);
		grayFrame.put(0, 0, frame);

		grayFrame = grayFrame.submat(0, mFrameHeight, 0, mFrameWidth);
		
		act.detectFaces(grayFrame);
		
		if(act.mIsLooking) {
			update(canvas);
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(bitmap, ballX, ballY, null);
		} 
		else {
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(bitmap, ballX, ballY, null);
		}
        getHolder().unlockCanvasAndPost(canvas);
	}
	
	public void update(Canvas canvas) {
		checkCollisions(canvas);
		ballX += ballVelX;
		ballY += ballVelY;
	}
	
	public void checkCollisions(Canvas canvas) {
		//no idea why 1.6 * ballWidth seems like the proper collision area
		if(ballX <= 0 || ballX + (1.6 * ballWidth) >= canvas.getWidth()) {
			ballVelX *= -1;
		}

		if(ballY <= 0 || ballY + (1.6 * ballHeight) >= canvas.getHeight()) {
			ballVelY *= -1;
		} 
	}
	
	public void setActivity(FdActivity f) {
		act = f;
	}
}
