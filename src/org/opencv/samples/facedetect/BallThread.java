package org.opencv.samples.facedetect;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

public class BallThread extends Thread {
	private Bitmap mainBitmap;
	private Bitmap tailBitmap;
	private Circle mainBall;
    private ArrayList<Circle> trail;
    private int trailCounter;
	private int screenWidth;
	private int screenHeight;
	private SurfaceHolder surfHold;
	private Canvas canvas;
	private boolean run = false;
	private boolean firstTime;
	private FdActivity act;

	public BallThread(SurfaceHolder holder, Bitmap main, Bitmap tail) {
		surfHold = holder;
		mainBitmap = main;
		tailBitmap = tail;
		trailCounter = 0;
		trail = new ArrayList<Circle>();
	}

	public void start(boolean r) {
		run = r;
		firstTime = true;
		super.start();
	}

	public void run() {
		while(run) {
			try {
				canvas = surfHold.lockCanvas();
				
				if(firstTime) {
					//set up ball once thread is started
					int mainDiam = 75;
					int startX = canvas.getWidth() / 2 - mainDiam / 2;
					int startY = canvas.getHeight() / 2 - mainDiam / 2;
					int xVel = 9;
					int yVel = 7;
					mainBall = new Circle(startX, startY, xVel, yVel, mainDiam, canvas.getWidth(), canvas.getHeight(), mainBitmap);
					firstTime = false;
				}
				
				int tailDiam = 40;
				int tailX = mainBall.x + mainBall.diam / 2 - tailDiam / 2;
				int tailY = mainBall.y + mainBall.diam / 2 - tailDiam / 2;
				
				//add trail if not looking and replay trail/do nothing if looking
				if(act.getIsLooking()) {
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
			} finally {
				if(canvas != null) {
					surfHold.unlockCanvasAndPost(canvas);
				}

			}

		}
	}
	
	public void setActivity(FdActivity f) {
		act = f;
	}
	
	public void quit() {
		run = false;
		interrupt();
	}
}