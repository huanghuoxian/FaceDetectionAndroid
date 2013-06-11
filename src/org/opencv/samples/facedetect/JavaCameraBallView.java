package org.opencv.samples.facedetect;

import org.opencv.android.JavaCameraView;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class JavaCameraBallView extends JavaCameraView implements Camera.PreviewCallback{
	private float ballX, ballY;
	private float ballVelX, ballVelY;
	private int ballWidth, ballHeight;
    protected Camera mCamera;
    private FdActivity act;
    
	public JavaCameraBallView(android.content.Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
	}

	public JavaCameraBallView(android.content.Context context, int cameraId) {
		super(context, cameraId);
	}	
	
	@Override
	public void onPreviewFrame(byte[] frame, Camera cam) {
		//super.onPreviewFrame(frame, cam);
		mCamera.setPreviewCallback(this);
		Mat gray = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);
		gray.put(0, 0, frame);

		gray = gray.submat(0, mFrameHeight, 0, mFrameWidth);
		
		act.detectFaces(gray);

		Canvas canvas = this.getHolder().lockCanvas();
		if(FdActivity.mIsLooking) {
			canvas.drawColor(Color.GREEN);
		} 
		else {
			canvas.drawColor(Color.RED);
		}
        getHolder().unlockCanvasAndPost(canvas);
	}
	
	public void update(Canvas canvas) {
		checkCollisions(canvas);
		ballX += ballVelX;
		ballY += ballVelY;
	}
	
	public void checkCollisions(Canvas canvas) {
		//no idea why 1.5 * ballWidth seems like the proper collision area
		if(ballX <= 0 || ballX + (1.5 * ballWidth) >= canvas.getWidth()) {
			ballVelX *= -1;
		}

		if(ballY <= 0 || ballY + (1.5 * ballHeight) >= canvas.getHeight()) {
			ballVelY *= -1;
		} 
	}
	
	public void setActivity(FdActivity f) {
		act = f;
	}
}
