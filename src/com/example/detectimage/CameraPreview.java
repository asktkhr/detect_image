package com.example.detectimage;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = "CameraPreview";

	private Camera mCamera;
	private SurfaceHolder mHolder;
	private int mFrameWidth;
	private int mFrameHeight;
	private Handler mHandler;
	


	public CameraPreview(Context context, Handler handler) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mHandler = handler;
		Log.i(TAG, "Instantiated new " + this.getClass());
	}


	public void surfaceChanged(SurfaceHolder _holder, int format, int width,
			int height) {
		Log.i(TAG, "surfaceCreated");
		if (mCamera != null) {
			Camera.Parameters params = mCamera.getParameters();
			List<Camera.Size> sizes = params.getSupportedPreviewSizes();
			mFrameWidth = width;
			mFrameHeight = height;

			// selecting optimal camera preview size
			{
				double minDiff = Double.MAX_VALUE;
				for (Camera.Size size : sizes) {
					if (Math.abs(size.height - height) < minDiff) {
						mFrameWidth = size.width;
						mFrameHeight = size.height;
						minDiff = Math.abs(size.height - height);
					}
				}
			}

			params.setPreviewSize(mFrameWidth, mFrameHeight);
			mCamera.setParameters(params);
			mCamera.startPreview();
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surfaceCreated");
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.setOneShotPreviewCallback(mCallback);
	}

	private PreviewCallback mCallback = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			Message msg = new Message();
			msg.arg1 = detectImage(mFrameWidth, mFrameHeight, data); // 学習画像を検出したら、その画像のIDを返す。検出できなければ-1を返す。
			mHandler.sendMessage(msg); // 検出結果をDetectImageActivityへ伝える
		}
	};
	
	public void restartPreviewCallback(){
		this.requestLayout();
		this.invalidate();
		if (mCamera != null) {
			// PreviewCallbackの再セット
			mCamera.setOneShotPreviewCallback(mCallback);
			mCamera.startPreview();
		}
	}

	
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");
		if (mCamera != null) {
			synchronized (this) {
				mCamera.stopPreview();
				mCamera.setPreviewCallback(null);
				mCamera.release();
				mCamera = null;
			}
		}
	}
	
	public native int detectImage(int width, int height, byte[] data);

	static {
		System.loadLibrary("native_sample");
	}
}