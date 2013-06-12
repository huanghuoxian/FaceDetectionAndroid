package org.opencv.samples.facedetect;

import java.io.File;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class FdActivity extends Activity implements CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mFaceDetector;

    private float                  mRelativeFaceSize   = 0.25f;
    private int                    mAbsoluteFaceSize   = 0;
    
    public boolean                 mIsLooking = false;

    //*
    private JavaCameraBallView   mOpenCvCameraView;
    /*/
    private CameraBridgeViewBase   mOpenCvCameraView;
    //*/
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                	
                	// Load native library after(!) OpenCV initialization
                	System.loadLibrary("detection_based_tracker");

                	// load cascade file from application resources
                	File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                	mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");

                	mFaceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                	if (mFaceDetector.empty()) {
                		Log.e(TAG, "Failed to load cascade classifier!");
                		mFaceDetector = null;
                	} else {
                		Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
                	}
                	cascadeDir.delete();

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public FdActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.face_detect_surface_view);

        /*
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        /*/
        mOpenCvCameraView = (JavaCameraBallView) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setActivity(this);
        //*/
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	//Landscape Version
    	mRgba = inputFrame.rgba();
    	mGray = inputFrame.gray();
        
        mAbsoluteFaceSize = Math.round(mGray.rows() * mRelativeFaceSize);
        
    	MatOfRect faces = new MatOfRect();

    	if (mFaceDetector != null) {
    			mFaceDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
    	}
    	else {
    		Log.e(TAG, "Detection Initialization Failed!");
    		System.exit(0);
    	}
    	
    	Rect[] facesArray = faces.toArray();
    	
    	for (int i = 0; i < facesArray.length; i++) {
    		Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
    	}

    	//Core.transpose(mRgba, mRgba);
    	Core.flip(mRgba, mRgba, 1);
    	return mRgba;
    	
        
    	/*
    	//Portrait Version
    	mRgba = inputFrame.rgba();
    	mGray = inputFrame.gray();
    	
        Core.flip(mGray.t(), mGray, 0);
        
        mAbsoluteFaceSize = Math.round(mGray.rows() * mRelativeFaceSize);
        
    	MatOfRect faces = new MatOfRect();

    	if (mFaceDetector != null) {
    			mFaceDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
    	}
    	else {
    		Log.e(TAG, "Detection Initialization Failed!");
    	}

    	Rect[] facesArray = faces.toArray();
    	
    	for (int i = 0; i < facesArray.length; i++) {
    		Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
    	}

    	//Core.transpose(mRgba, mRgba);
    	Core.flip(mRgba, mRgba, 1);
    	
    	return mRgba;
    	*/
    }
    
    public void detectFaces(Mat frame) {
    	mAbsoluteFaceSize = Math.round(frame.rows() * mRelativeFaceSize);
        
    	MatOfRect faces = new MatOfRect();

    	if (mFaceDetector != null) {
    			mFaceDetector.detectMultiScale(frame, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
    	}
    	else {
    		Log.e(TAG, "Detection Initialization Failed!");
    		System.exit(0);
    	}
    	
    	Rect[] facesArray = faces.toArray();
    	
    	if(facesArray.length > 0)
    	{
    		setIsLooking(true);
    	}
    	else
    	{
    		setIsLooking(false);
    	}
    }
    
    public synchronized boolean getIsLooking() {
    	return mIsLooking;
    }
    
    public synchronized void setIsLooking(boolean b) {
    	mIsLooking = b;
    }
}