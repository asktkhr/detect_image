package com.example.detectimage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class DetectImageActivity extends Activity {
	private static final String TAG = "Sample::Activity";
	private InfomationView mInfoview;
	private CameraPreview mCameraPreview;
	// private MainHandler mHandler = new MainHandler();

	public DetectImageActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		FrameLayout fl = new FrameLayout(this); // ビューを重ねて表示するためのレイアウト
		fl.setLayoutParams(params);

		mCameraPreview = new CameraPreview(this, new MainHandler());
		mCameraPreview.setLayoutParams(params);
		fl.addView(mCameraPreview);

		mInfoview = new InfomationView(this);
		mInfoview.setLayoutParams(params);
		fl.addView(mInfoview);

		setContentView(fl);

		init(); //学習画像の登録
	}

	private void init() {
		int[] ids = {R.raw.no_crossing, R.raw.closure};
		int[] widths = new int[2];
		int[] heights = new int[2];
		int[][] rgbas = new int[2][];
		for(int i = 0; i < ids.length; i++){
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), ids[i]); // 学習画像の読み込み
			widths[i] = bitmap.getWidth();
			heights[i] = bitmap.getHeight();
			rgbas[i] = new int[widths[i] * heights[i]];
			bitmap.getPixels(rgbas[i], 0, widths[i], 0, 0, widths[i], heights[i]); // 各学習画像のピクセル値をrgbasに格納
		}
		setTrainingImages(widths, heights, rgbas, 2);
	}

	private class MainHandler extends Handler {

		public void handleMessage(Message msg) {
			mInfoview.setDetectImageId(msg.arg1);      //検出した画像IDをセット
			mInfoview.invalidate();                  //InfomationViewの更新
			mCameraPreview.restartPreviewCallback(); // Previewのリスタート
		}
	}

	public native void setTrainingImage(int width, int height, int[] rgba);
	public native void setTrainingImages(int[] widths, int[] heights, int[][] rgbas, int imageNum);

	static {
		System.loadLibrary("native_sample"); //ネイティブライブラリの読み込み
	}
}
