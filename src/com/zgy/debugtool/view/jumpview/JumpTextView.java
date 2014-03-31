package com.zgy.debugtool.view.jumpview;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class JumpTextView extends TextView {

	private static final String TAG = "jumpTextView";
	
	private Animation mAniUp;
	private Animation mAniDown;
	

	private JumpAnimationListener mListener;

	public JumpTextView(Context context, long timeDur, int jumpOffset) {
		super(context);
		initAnim(timeDur, jumpOffset);
	}

	public void setJumpAnimationListener(JumpAnimationListener listener) {
		this.mListener = listener;
	}

	public void jump() {
		this.startAnimation(mAniUp);
	}

	private void initAnim(long timeDur, int jumpOffset) {
		mAniDown = new TranslateAnimation(0, 0, -jumpOffset, 0);
		mAniUp = new TranslateAnimation(0, 0, 0, -jumpOffset);
		mAniDown.setInterpolator(new AccelerateInterpolator());
		mAniUp.setInterpolator(new DecelerateInterpolator());
		mAniDown.setDuration(timeDur / 2);
		mAniUp.setDuration(timeDur / 2);
		mAniDown.setAnimationListener(mAniListenerDown);
		mAniUp.setAnimationListener(mAniListenerUp);
	}

	private AnimationListener mAniListenerUp = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			JumpTextView.this.startAnimation(mAniDown);
		}
	};

	private AnimationListener mAniListenerDown = new AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			if (mListener != null) {
				mListener.onJumpAnimationEnd(JumpTextView.this);
			}
		}
	};

	public interface JumpAnimationListener {
		public void onJumpAnimationEnd(View v);
	}
}
