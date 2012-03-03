package com.example.detectimage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

public class InfomationView extends View {
	private Paint mPaint;

	private final int PADDING = 5;
	private final int FONT_SIZE = 30;
	private int mStartPosX;
	private int mStartPosY = 50;
	private int[] mExplanationIds = { R.string.no_crossing, R.string.closure };
	private int mDetectImageId = -1;

	public InfomationView(Context context) {
		super(context);
		this.setBackgroundColor(Color.argb(0,0,0,0));
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	public void setDetectImageId(int id) {
		this.mDetectImageId = id;
	}

	private String getExplanation(int id) {
		return this.getContext().getString(mExplanationIds[id]);
	}

	private int calcMaxLengthOnLine(String str, int startPosX, int fontSize) {
		int endPoint = startPosX + str.length() * fontSize;
		return endPoint < this.getWidth() ? str.length() : (int) (str.length() - Math.ceil((endPoint - this.getWidth()) / (double) fontSize));
	}

	private int getDisplayStartPosX(String str, int fontSize) {
		if ((str.length() * fontSize) > this.getWidth())
			return 0;
		return (this.getWidth() - str.length() * fontSize) / 2;
	}

	@Override
	public void onDraw(Canvas canvas) {

		if (mDetectImageId != -1) {
			String explanation = getExplanation(mDetectImageId);
			mStartPosX = getDisplayStartPosX(explanation, FONT_SIZE); // 画面上部中央へ表示するときの開始位置
			int charNum = calcMaxLengthOnLine(explanation, mStartPosX, FONT_SIZE); // 1行に表示する文字数
			int linage = (int) Math.ceil(explanation.length()  / (double) charNum); // 表示する文字列の行数

			mPaint.setColor(Color.argb(180, 0, 0, 0));
			mPaint.setStyle(Style.FILL);
			// 説明文の背景を描画
			canvas.drawRect(mStartPosX - PADDING, mStartPosY - FONT_SIZE, mStartPosX + charNum * FONT_SIZE + PADDING, mStartPosY + (linage - 1) * FONT_SIZE + PADDING, mPaint);

			mPaint.setTextSize(FONT_SIZE);
			mPaint.setColor(Color.WHITE);

			// 画像の説明文の描画
			int i = 0;
			for (; i < linage - 1; i++) {
				canvas.drawText(explanation, i * charNum, (i + 1) * charNum, mStartPosX, mStartPosY + i * FONT_SIZE, mPaint);
			}
			// 最後の行の描画
			if (explanation.length() % charNum != 0) {
				canvas.drawText(explanation, i * charNum, i * charNum + explanation.length() % charNum, mStartPosX, mStartPosY + i * FONT_SIZE, mPaint);
			} else
				canvas.drawText(explanation, i * charNum, i * charNum + charNum, mStartPosX, mStartPosY + i * FONT_SIZE, mPaint);
		}
	}

}