package com.zgy.debugtool.batteryinfo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.zgy.debugtool.main.R;

/**
 * 展示电池信息的页面
 * 
 * 通过注册receiver action.equals(Intent.ACTION_BATTERY_CHANGED获得电池的实时情况，貌似是30s一次
 * 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class BatteryInfoActivity extends Activity implements OnClickListener {

	private ImageView mImageback;
	private ListView mListview;
	private BatteryListAdapter mAdapter;
	private ProgressBar mProgressbar;
	private ToggleButton mToggleButtonAutoRefresh;

	private List<BatteryItem> mBatteryInfoList;

	private static final int TIME_DELAY_FLASH = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battery);

		mImageback = (ImageView) findViewById(R.id.img_battery_back);
		mListview = (ListView) findViewById(R.id.lv_battery);
		mToggleButtonAutoRefresh = (ToggleButton) findViewById(R.id.tbtn_battery_auto_refresh);

		mProgressbar = (ProgressBar) findViewById(R.id.progress_batterylist);

		mImageback.setOnClickListener(this);
		mToggleButtonAutoRefresh.setChecked(true);

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mBroadcastReceiver, filter);

	}

	private void refreshListView() {

		if (!mToggleButtonAutoRefresh.isChecked() && mProgressbar.getVisibility() != View.VISIBLE) {
			return;
		}

		if (mBatteryInfoList == null) {
			return;
		}

		if (mProgressbar.getVisibility() == View.VISIBLE) {
			mProgressbar.setVisibility(View.GONE);
		}

		if (mAdapter == null) {
			mAdapter = new BatteryListAdapter(BatteryInfoActivity.this, mBatteryInfoList);
			mListview.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataChanged(mBatteryInfoList);
		}

		mListview.setVisibility(View.INVISIBLE);
		mHandler.postDelayed(mRunnable, TIME_DELAY_FLASH);
	}

	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mListview.setVisibility(View.VISIBLE);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_battery_back:
			finish();
			break;

		default:
			break;
		}
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
				int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
				boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
				int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
				int icon_small = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);
				int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
				int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
				int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
				String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

				String statusString = "";

				switch (status) {
				case BatteryManager.BATTERY_STATUS_UNKNOWN:
					statusString = "未知";
					break;
				case BatteryManager.BATTERY_STATUS_CHARGING:
					statusString = "充电中";
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:
					statusString = "放电中";
					break;
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					statusString = "不充电";
					break;
				case BatteryManager.BATTERY_STATUS_FULL:
					statusString = "满电";
					break;
				}

				String healthString = "";

				switch (health) {
				case BatteryManager.BATTERY_HEALTH_UNKNOWN:
					healthString = "未知";
					break;
				case BatteryManager.BATTERY_HEALTH_GOOD:
					healthString = "良好";
					break;
				case BatteryManager.BATTERY_HEALTH_OVERHEAT:
					healthString = "过热";
					break;
				case BatteryManager.BATTERY_HEALTH_DEAD:
					healthString = "损坏";
					break;
				case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
					healthString = "电压过大";
					break;
				case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
					healthString = "未知故障";
					break;
				}

				String acString = "未充电";

				switch (plugged) {
				case BatteryManager.BATTERY_PLUGGED_AC:
					acString = "交流电";
					break;
				case BatteryManager.BATTERY_PLUGGED_USB:
					acString = "USB接口";
					break;
				case 4:// BatteryManager.BATTERY_PLUGGED_WIRELESS
					acString = "无线";
					break;
				}

				mBatteryInfoList = new ArrayList<BatteryItem>();
				// mBatteryInfoList.add(new BatteryItem("status", statusString));
				// mBatteryInfoList.add(new BatteryItem("health", healthString));
				// mBatteryInfoList.add(new BatteryItem("present", String.valueOf(present)));
				// mBatteryInfoList.add(new BatteryItem("level", String.valueOf(level)));
				// mBatteryInfoList.add(new BatteryItem("scale", String.valueOf(scale)));
				// mBatteryInfoList.add(new BatteryItem("icon_small", String.valueOf(icon_small)));
				// mBatteryInfoList.add(new BatteryItem("plugged", acString));
				// mBatteryInfoList.add(new BatteryItem("voltage", String.valueOf(voltage)));
				// mBatteryInfoList.add(new BatteryItem("temperature", String.valueOf(temperature)));
				// mBatteryInfoList.add(new BatteryItem("technology", technology));

				mBatteryInfoList.add(new BatteryItem("状态", statusString, icon_small));
				mBatteryInfoList.add(new BatteryItem("电量", String.valueOf(level * 100 / scale) + "%", 0));
				// mBatteryInfoList.add(new BatteryItem("scale", String.valueOf(scale)));//全部电量是100%
				// mBatteryInfoList.add(new BatteryItem("icon_small", String.valueOf(icon_small)));
				mBatteryInfoList.add(new BatteryItem("电压", String.format("%.3f  V", voltage / 1000.00), 0));
				mBatteryInfoList.add(new BatteryItem("温度", String.format("%.2f  ℃", temperature * 0.1), 0));
				mBatteryInfoList.add(new BatteryItem("充电方式", acString, 0));
				mBatteryInfoList.add(new BatteryItem("使用中", present ? "是" : "否", 0));
				mBatteryInfoList.add(new BatteryItem("物理材料", "Li-ion".equals(technology) ? "锂电池(Li-ion)" : technology, 0));
				mBatteryInfoList.add(new BatteryItem("健康状况", healthString, 0));

				refreshListView();
			}
		}
	};

	/**
	 * 
	 * "present"　　　　(boolean) ... "level"　　　　　 (int)　　　 …电池剩余容量 "scale"　　　　 (int)　　　 …电池最大值，通常为100。 "icon-small"　　 (int) 　　 …图标ID。 "voltage"　　　　(int)　　　 …电池的电压（伏特） "temperature"　 (int)　　　
	 * …电池的温度，0.1度单位。例如 表示197的时候，意思为19.7度。 "technology"　　(String)　　…电池类型，例如，Li-ion等等。 "plugged"　　　 (int) 　 …充电方式： 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_PLUGGED_AC：AC充电。
	 * 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_PLUGGED_USB：USB充电。 　 "status"　　　　 (int)　　　 …电池状态： 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_STATUS_CHARGING：充电状态。
	 * 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_STATUS_DISCHARGING：放电状态。 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_STATUS_NOT_CHARGING：未充满。 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_STATUS_FULL：充满电。
	 * 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_STATUS_UNKNOWN：未知状态。
	 * 
	 * "health"　　　　 (int)　　　 …健康状态： 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_HEALTH_GOOD：状态良好。 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_HEALTH_DEAD：电池没有电。
	 * 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE：电池电压过高。 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_HEALTH_OVERHEAT：电池过热。 　　　　　　　　　　　　　　　　　BatteryManager.BATTERY_HEALTH_UNKNOWN：未知状态
	 * 
	 */

}
