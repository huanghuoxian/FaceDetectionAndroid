package org.opencv.samples.facedetect;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class BallThread extends Thread {
	private SurfaceHolder surfHold;
	private Ball ball;
	private Canvas canvas;
	private boolean run = false;

	public BallThread(SurfaceHolder holder, Ball b) {
		surfHold = holder;
		ball = b;
	}

	public void setRunnable(boolean r) {
		run = r;
	}

	public void run() {
		while(run) {
			canvas = null;
			try {
				canvas = surfHold.lockCanvas(null);
				synchronized(surfHold) {
					ball.onDraw(canvas);
				}
			} finally {
				if(canvas != null) {
					surfHold.unlockCanvasAndPost(canvas);
				}

			}

		}
	}
}