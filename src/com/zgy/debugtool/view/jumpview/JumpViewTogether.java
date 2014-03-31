package com.zgy.debugtool.view.jumpview;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.zgy.debugtool.view.jumpview.JumpTextView.JumpAnimationListener;

public class JumpViewTogether extends LinearLayout {

	private static final String TAG = "JumpViewTogether";

	private Context mContext;

	private long mTimeDelay = 1000;
	private String mJumpString = "...";
	private long mSingleJumpDur = 800;
	private int mSingleJumpOffset = 30;
	private int mPointSize;

	private boolean mIsRunning;

	public JumpViewTogether(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public JumpViewTogether(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	private void init() {
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(lp);
		this.setOrientation(LinearLayout.HORIZONTAL);
	}

	private void initViews() {
		this.removeAllViews();
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.BOTTOM;
		int count = mJumpString.length();
		for (int i = 0; i < count; i++) {
			JumpTextView textview = new JumpTextView(mContext, mSingleJumpDur, mSingleJumpOffset);
			textview.setLayoutParams(lp);
			textview.setText(String.valueOf(mJumpString.charAt(i)));
			textview.setTextColor(Color.BLUE);
			textview.setId(i);
			if (mPointSize > 0) {
				textview.setTextSize(mPointSize);
			}
			textview.setJumpAnimationListener(mJumpListener);
			this.addView(textview);
		}
	}

	public void setPointSize(int pointSize) {
		if (mIsRunning) {
			Log.e(TAG, "error !  animation is running");
			return;
		}
		this.mPointSize = pointSize;
	}

	public void setTimeDelay(long timeDelay) {
		if (mIsRunning) {
			Log.e(TAG, "error !  animation is running");
			return;
		}
		this.mTimeDelay = timeDelay;
	}

	public void setJumpText(String jumpText) {
		if (mIsRunning) {
			Log.e(TAG, "error !  animation is running");
			return;
		}
		this.mJumpString = jumpText;
	}

	public void setSingleInfo(long timeDur, int jumpOffset) {
		if (mIsRunning) {
			Log.e(TAG, "error !  animation is running");
			return;
		}
		this.mSingleJumpDur = timeDur;
		this.mSingleJumpOffset = jumpOffset;
	}

	public void startAnim() {
		if (!mIsRunning) {
			mIsRunning = true;
			initViews();
			runAllAnim();
		} else {
			Log.e(TAG, "error! anim is running");
		}

	}

	public void stopAnim() {
		mIsRunning = false;
		int count = mJumpString.length();
		for (int i = 0; i < count; i++) {
			this.getChildAt(i).clearAnimation();
		}
	}

	public boolean isAnimRunning() {
		return mIsRunning;
	}

	private void runAllAnim() {
		int count = mJumpString.length();
		long delay = mSingleJumpDur / count;

		for (int i = 0; i < count; i++) {
			final JumpTextView v = (JumpTextView) this.getChildAt(i);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					v.jump();
				}
			}, delay * i);

		}
	}

	private JumpAnimationListener mJumpListener = new JumpAnimationListener() {

		@Override
		public void onJumpAnimationEnd(View v) {
			if (v.getId() == (mJumpString.length() - 1) && mIsRunning) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						runAllAnim();
					}
				}, mTimeDelay);

			}
		}
	};

}
