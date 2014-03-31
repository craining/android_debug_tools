package com.zgy.debugtool.showtopinfo;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zgy.debugtool.main.Constants;
import com.zgy.debugtool.main.MainApplication;
import com.zgy.debugtool.main.R;

/**
 * 此服务承载一个view，用来实时展示前台应用的包名、活动名
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class ShowService extends Service {
	private static final String TAG = "WaterMarkService";

	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;
	private View view;

	private static final int MSG_NOOP = 100;
	private static final int TIME_DELAY = 1500;

	private ActivityManager mActivityManager;
	private Handler mHandler;
	private TextView mTextView;
	private boolean isViewAdded;

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		Log.e("", "onStart");
		if (wm == null && view == null) {
			try {
				createView();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onCreate() {
		Log.e("", "CREATE");
		super.onCreate();
	}

	private void createView() {
		// LogRingForu.v(TAG, "set View ");
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		view = LayoutInflater.from(this).inflate(R.layout.floating_view, null);
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		wmParams = MainApplication.getMywmParams();

		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;

		wmParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN

		| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

		| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

		| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

		| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
		wmParams.gravity = Gravity.FILL;
		wmParams.format = PixelFormat.RGBA_8888;
		mTextView = (TextView) view.findViewById(R.id.text_showinfo);
		mTextView.setTextColor(Color.argb(100, 255, 255, 255));
		wm.addView(view, wmParams);
		isViewAdded = true;
		mHandler = new ListenerHandler();
		mHandler.sendEmptyMessage(MSG_NOOP);
	}

	class ListenerHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_NOOP:

				mHandler.removeMessages(MSG_NOOP);

				if (mActivityManager != null && mTextView != null) {
					List<RunningTaskInfo> list = mActivityManager.getRunningTasks(1);
					RunningTaskInfo info = list.get(0);
					String name = info.topActivity.getClassName();
					if (name != null && name.contains(Constants.APP_UnShow)) {
						if (isViewAdded) {
							isViewAdded = false;
							wm.removeView(view);
						}
					} else {
						if (!isViewAdded) {
							isViewAdded = true;
							wm.addView(view, wmParams);
						}
						mTextView.setText(info.topActivity.getPackageName() + "\r\n" + name + "");
					}
				}
				mHandler.sendEmptyMessageDelayed(MSG_NOOP, TIME_DELAY);
				break;

			default:
				break;
			}

			super.handleMessage(msg);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (wm != null && view != null) {
			if (view.isShown())
				wm.removeView(view);
			wm = null;
			view = null;
		}

		if (mHandler != null) {
			mHandler.removeMessages(MSG_NOOP);
		}
	}
}
