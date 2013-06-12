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

public class JavaCameraBallView extends JavaCameraView implements Camera.PreviewCallback{
	private Bitmap mainBitmap;
	private Bitmap tailBitmap;
    private FdActivity act;
    private boolean firstTime;
    private Circle mainBall;
    private ArrayList<Circle> trail;
    private int trailCounter;
    
	public JavaCameraBallView(android.content.Context context, android.util.AttributeSet attrs) {
		super(context, attrs);
		
		mainBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_ball_75);
		tailBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_ball_40);
		
		trail = new ArrayList<Circle>();
		trailCounter = 0;
		
		firstTime = true;
		
		getHolder().addCallback(this);
//		ballThread = new BallThread(getHolder(), this);
	}
	
	@Override
	public void onPreviewFrame(byte[] frame, Camera cam) {
		mCamera.setPreviewCallback(this);
		Canvas canvas = this.getHolder().lockCanvas();
		
		if(firstTime)
		{
			//set up ball
			int mainDiam = 75;
			int startX = canvas.getWidth() / 2 - mainDiam / 2;
			int startY = canvas.getHeight() / 2 - mainDiam / 2;
			int xVel = 9;
			int yVel = 7;
			mainBall = new Circle(startX, startY, xVel, yVel, mainDiam, canvas.getWidth(), canvas.getHeight(), mainBitmap);
			firstTime = false;
		}
		
		//face detection
		Mat grayFrame = new Mat(mFrameHeight + (mFrameHeight/2), mFrameWidth, CvType.CV_8UC1);
		grayFrame.put(0, 0, frame);
		grayFrame = grayFrame.submat(0, mFrameHeight, 0, mFrameWidth);
		act.detectFaces(grayFrame);
		
		int tailDiam = 40;
		int tailX = mainBall.x + mainBall.diam / 2 - tailDiam / 2;
		int tailY = mainBall.y + mainBall.diam / 2 - tailDiam / 2;
		
		//add trail if not looking and replay trail/do nothing if looking
		if(act.mIsLooking) {
			if(trailCounter >= 3)
			{
				if(trail.size() > 0) {
					trail.add(new Circle(tailX, tailY, 0, 0, tailDiam, canvas.getWidth(), canvas.getHeight(), tailBitmap));
				}
				trailCounter = 0;
			}
			
			int i = 0;
			while(i < 2 && !trail.isEmpty()) {
				trail.remove(0);
				i++;
			}
		} 
		else {
			//add tail because not looking
			if(trailCounter >= 3)
			{
				trail.add(new Circle(tailX, tailY, 0, 0, tailDiam, canvas.getWidth(), canvas.getHeight(), tailBitmap));
				trailCounter = 0;
			}
		}
		trailCounter++;
		
		canvas.drawColor(Color.BLACK);
		
		//draw all of tail
		for(Circle currTail: trail) {
			currTail.draw(canvas);
		}
		
		mainBall.draw(canvas);
		mainBall.update();
		
        getHolder().unlockCanvasAndPost(canvas);
	}
	
	public void setActivity(FdActivity f) {
		act = f;
	}
}
