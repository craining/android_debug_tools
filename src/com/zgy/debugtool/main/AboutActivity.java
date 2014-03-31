package com.zgy.debugtool.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgy.debugtool.main.R;
import com.zgy.debugtool.view.jumpview.JumpViewTogether;

/**
 * 关于页面
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class AboutActivity extends Activity implements OnClickListener {

	private ImageView mImgback;
	private JumpViewTogether mJumpView;
	private TextView mTextViewTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		mImgback = (ImageView) findViewById(R.id.img_about_back);
		mImgback.setOnClickListener(this);
		mJumpView = (JumpViewTogether) findViewById(R.id.text_jump_about_title);
		mTextViewTitle = (TextView) findViewById(R.id.text_about_title);

		mTextViewTitle.setTextSize(20);
		
		showAnimAlpha();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	
	private void showAnimAlpha() {

		Animation anim = new AlphaAnimation(0.1f, 1.0f);
		anim.setDuration(1800);
		mTextViewTitle.setTextColor(Color.BLUE);
		mTextViewTitle.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				animDown();
			}
		});
	}

	private void animDown() {
		final Animation mAniDown = new TranslateAnimation(0, 0, 0, mTextViewTitle.getHeight() - mTextViewTitle.getLineHeight());
		mAniDown.setDuration(1000);
		mAniDown.setInterpolator(new AccelerateInterpolator());
		mAniDown.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mTextViewTitle.setVisibility(View.GONE);
				mJumpView.setVisibility(View.VISIBLE);
				mJumpView.setJumpText("开发助手");
				mJumpView.setSingleInfo(800, mTextViewTitle.getHeight() - 2*mTextViewTitle.getLineHeight());
				mJumpView.setPointSize(20);
				mJumpView.startAnim();
			}
		});
		mTextViewTitle.startAnimation(mAniDown);
	}

	@Override
	protected void onDestroy() {
		if(mJumpView.isAnimRunning()) {
			mJumpView.stopAnim();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_about_back:
			finish();
			break;

		default:
			break;
		}

	}
}
