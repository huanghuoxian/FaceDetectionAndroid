package org.opencv.samples.facedetect;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Circle {
	protected int x;
	protected int y;
	private int xVel;
	private int yVel;
	private int diam;
	private int screenWidth;
	private int screenHeight;
	private Bitmap bitmap;
	
	public Circle(int x, int y, int xVel, int yVel, int diam, int screenWidth, int screenHeight, Bitmap bitmap) {
		this.x = x;
		this.y = y;
		this.xVel = xVel;
		this.yVel = yVel;
		this.diam = diam;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.bitmap = bitmap;
	}
	
	public void update() {
		//test for wall to bounce off of
		if(x + (diam / 2) >= screenWidth || x - (diam / 2) <= 0) {
			xVel *= -1;
		}
		if(y + (diam / 2) >= screenHeight || y - (diam / 2) <= 0) {
			yVel *= -1;
		}

		x += xVel;
	    y += yVel;
	}
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x, y, null);
	}
}
