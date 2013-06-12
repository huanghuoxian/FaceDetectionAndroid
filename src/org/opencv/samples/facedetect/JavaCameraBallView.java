package org.opencv.samples.facedetect;

import java.util.ArrayList;

import org.opencv.android.JavaCameraView;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.view.SurfaceHolder;

public class JavaCameraBallView extends JavaCameraView implements Camera.PreviewCallback, SurfaceHolder.Callback{
	private BallThread ballThread;
    private FdActivity act;
    private boolean firstTime;
    private Circle mainBall;
    private ArrayList<Circle> trail;
    private int trailCounter;
    
	public JavaCameraBallView(android.content.Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
		
//		Bitmap mainBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_ball_75);
//		Bitmap tailBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_ball_40);
		
		getHolder().addCallback(this);
	}
	
	@Override
	public void onPreviewFrame(byte[] frame, Camera cam) {
		mCamera.setPreviewCallback(this);
//		Canvas canvas = this.getHolder().lockCanvas();
		
		//face detection
		Mat grayFrame = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);
		grayFrame.put(0, 0, frame);
		grayFrame = grayFrame.submat(0, mFrameHeight, 0, mFrameWidth);
		act.detectFaces(grayFrame);
		
//		int tailDiam = 40;
//		int tailX = mainBall.x + mainBall.diam / 2 - tailDiam / 2;
//		int tailY = mainBall.y + mainBall.diam / 2 - tailDiam / 2;
		
//		//add trail if not looking and replay trail/do nothing if looking
//		if(act.getIsLooking()) {
//			if(trailCounter >= 3)
//			{
//				if(trail.size() > 0) {
//					trail.add(new Circle(tailX, tailY, 0, 0, tailDiam, canvas.getWidth(), canvas.getHeight(), tailBitmap));
//				}
//				trailCounter = 0;
//			}
//			
//			int i = 0;
//			while(i < 2 && !trail.isEmpty()) {
//				trail.remove(0);
//				i++;
//			}
//		} 
//		else {
//			//add tail because not looking
//			if(trailCounter >= 3)
//			{
//				trail.add(new Circle(tailX, tailY, 0, 0, tailDiam, canvas.getWidth(), canvas.getHeight(), tailBitmap));
//				trailCounter = 0;
//			}
//		}
//		trailCounter++;
//		
//		canvas.drawColor(Color.BLACK);
//		
//		//draw all of tail
//		for(Circle currTail: trail) {
//			currTail.draw(canvas);
//		}
//		
//		mainBall.draw(canvas);
//		mainBall.update();
		
//        getHolder().unlockCanvasAndPost(canvas);
	}
	
	public void setActivity(FdActivity f) {
		act = f;
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Bitmap mainBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_ball_75);
		Bitmap tailBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_ball_40);
		ballThread.start();
		ballThread = new BallThread(this.getHolder(), mainBitmap, tailBitmap);
		ballThread.setActivity(act);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		ballThread.quit();
		while(retry) {
			try {
				ballThread.join();
				retry = false;
			} catch(InterruptedException ie) {
				//Try again and again and again
			}
			break;
		}
		ballThread = null;
	}
}
