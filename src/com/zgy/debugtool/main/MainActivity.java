package com.zgy.debugtool.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.zgy.debugtool.appinfo.AllAppsListActivity;
import com.zgy.debugtool.appinfo.ProcessListActivity;
import com.zgy.debugtool.batteryinfo.BatteryInfoActivity;
import com.zgy.debugtool.showtopinfo.ShowService;
import com.zgy.debugtool.util.ServiceUtil;

/**
 * 主菜单页
 * 
 * 欢迎加开发讨论群88130145
 * 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class MainActivity extends Activity implements OnClickListener{


	private ToggleButton mTBtnShowTop;
	private Button mBtnProcess;
	private Button mBtnBattery;
	private Button mBtnAllApps;
	private Button mBtnAbout;
	
	private boolean isRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTBtnShowTop = (ToggleButton) findViewById(R.id.tbtn_show_top_switch);
		mBtnProcess = (Button) findViewById(R.id.btn_process);
		mBtnBattery = (Button) findViewById(R.id.btn_battery);
		mBtnAllApps = (Button) findViewById(R.id.btn_all_apps);
		mBtnAbout = (Button) findViewById(R.id.btn_about);

		isRunning = ServiceUtil.isServiceStarted(MainActivity.this, Constants.APP_ServiceName);

		refreshView();

		mBtnBattery.setOnClickListener(this);
		mTBtnShowTop.setOnClickListener(this);
		mBtnProcess.setOnClickListener(this);
		mBtnAllApps.setOnClickListener(this);
		mBtnAbout.setOnClickListener(this);

	}

	private void refreshView() {
		mTBtnShowTop.setChecked(isRunning);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tbtn_show_top_switch:
			if (isRunning) {
				stopService(new Intent(MainActivity.this, ShowService.class));
			} else {
				startService(new Intent(MainActivity.this, ShowService.class));
			}
			isRunning = !isRunning;
			refreshView();
			break;

		case R.id.btn_process:
			startActivity(new Intent(MainActivity.this, ProcessListActivity.class));
			break;
			
		case R.id.btn_battery:
			startActivity(new Intent(MainActivity.this, BatteryInfoActivity.class));
			break;
		case R.id.btn_all_apps:
			startActivity(new Intent(MainActivity.this, AllAppsListActivity.class));
			break;
		case R.id.btn_about:
			startActivity(new Intent(MainActivity.this, AboutActivity.class));
			break;
		default:
			break;
		}
	}
}
