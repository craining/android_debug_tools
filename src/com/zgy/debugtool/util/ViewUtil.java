package com.zgy.debugtool.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;

public class ViewUtil {
	public static void animHideShowView(final View v, AnimationListener al, int measureHeight, final boolean show, long ainmTime) {

		if (measureHeight == 0) {
			measureHeight = v.getMeasuredHeight();
		}
		final int heightMeasure = measureHeight;
		Animation anim = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime, Transformation t) {

				if (interpolatedTime == 1) {
					v.setVisibility(show ? View.VISIBLE : View.GONE);
				} else {
					int height;
					if (show) {
						height = (int) (heightMeasure * interpolatedTime);
					} else {
						height = heightMeasure - (int) (heightMeasure * interpolatedTime);
					}
					v.getLayoutParams().height = height;
					v.requestLayout();
				}
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};

		if (al != null) {
			anim.setAnimationListener(al);
		}
		anim.setDuration(ainmTime);
		v.startAnimation(anim);
	}

	public static SpannableStringBuilder getProcessPKGnameSpan(String head, String tail) {
		String allText = head + tail;
		SpannableStringBuilder mSpannableStringBuilder = new SpannableStringBuilder(allText);

		mSpannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, head.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		mSpannableStringBuilder.setSpan(new ForegroundColorSpan(Color.BLACK), head.length(), allText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

		return mSpannableStringBuilder;
	}
}
