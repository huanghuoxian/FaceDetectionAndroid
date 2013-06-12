package org.opencv.samples.facedetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Ball extends SurfaceView implements SurfaceHolder.Callback {
	private BallThread ballThread = null;
	private Bitmap bitmap;
	private float ballX, ballY;
	private float ballVelX, ballVelY;
	private int ballWidth, ballHeight;
	private boolean setPosition;

	public Ball(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_ball_75);

		//ballX, ballY set in on Draw so we can access canvas dimensions
		
		//dimensions of ball are hard coded.  Hopefully can be changed later
		ballWidth = 75;
		ballHeight = 75;
		
		ballVelX = 5.0f;
		ballVelY = 4.0f;
		
		setPosition = true;
		
		getHolder().addCallback(this);
		//ballThread = new BallThread(getHolder(), this);
	}

	protected void onDraw(Canvas canvas) {
		update(canvas);
		if(setPosition)
		{
			//set start position in middle. doesn't seem like middle in practice for some reason
			ballX = canvas.getWidth() / 2 - (ballWidth / 2);
			ballY = canvas.getHeight() / 2 - (ballHeight / 2);
			setPosition = false;
		}
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(bitmap, ballX, ballY, null);
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

	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public int getBitmapWidth() {
		if(bitmap != null) {
			return bitmap.getWidth();
		} 
		else {
			return 0;
		}
	}

	public int getBitmapHeight() {
		if(bitmap != null) {
			return bitmap.getHeight();
		} 
		else {
			return 0;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		//ballThread.setRunnable(true);
		ballThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		//ballThread.setRunnable(false);
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