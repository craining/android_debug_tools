package com.zgy.debugtool.appinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zgy.debugtool.appinfo.OperasListDialog.OnItemClickedListener;
import com.zgy.debugtool.main.MainApplication;
import com.zgy.debugtool.main.R;

public class AllAppsListActivity extends Activity implements OnClickListener, OnItemClickListener {

	private ListView mListViewApps;
	private AllAppsListAdapter mAdapter;
	private ProgressBar mProgressBar;

	private Button mBtnRefresh;
	private ImageView mImgBack;

	private List<AppInfo> mAppInfo;
	private BlockingQueue<String> mBlockPackageStep = new PriorityBlockingQueue<String>();

//	private PackageManager mPackageM;

	private boolean mSingleLine;

	private static final int MSG_REFRESHLIST_AFTER_GET_APPS = 0x100;
	private static final int MSG_GET_APPS_FAIL = 0x101;
	
	private RelativeLayout mLayotUpdateTimeLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_allapps);
		MainApplication.mPackageManager = getApplicationContext().getPackageManager();

		mListViewApps = (ListView) findViewById(R.id.lv_apps);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_appslist);
		mBtnRefresh = (Button) findViewById(R.id.btn_apps_refresh);
		mImgBack = (ImageView) findViewById(R.id.img_apps_back);
		
		mLayotUpdateTimeLabel = (RelativeLayout) findViewById(R.id.layout_appslist_time);
		
		if(android.os.Build.VERSION.SDK_INT <= 8) {
			mLayotUpdateTimeLabel.setVisibility(View.INVISIBLE);
		}
		
		mBtnRefresh.setOnClickListener(this);
		mImgBack.setOnClickListener(this);
		mListViewApps.setOnItemClickListener(this);

		new Thread(mRunnableGetPackageList).start();
		putGetAppsStep();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private Runnable mRunnableGetPackageList = new Runnable() {

		@Override
		public void run() {
			while (true) {
				try {
					mBlockPackageStep.peek();
					mBlockPackageStep.take();
					List<PackageInfo> mPackagesList = MainApplication.mPackageManager.getInstalledPackages(PackageManager.GET_GIDS);// | PackageManager.GET_CONFIGURATIONS
					// PackageManager.GET_SERVICES | PackageManager.GET_GIDS | PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS |
					// PackageManager.GET_SIGNATURES
					mAppInfo = new ArrayList<AppInfo>();
					if (mPackagesList != null) {
						AppInfo app;
						for (PackageInfo pkgInfo : mPackagesList) {
							app = new AppInfo(MainApplication.mPackageManager, pkgInfo);
							mAppInfo.add(app);
						}
					}

					mHandler.sendEmptyMessage(MSG_REFRESHLIST_AFTER_GET_APPS);
				} catch (Exception e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(MSG_GET_APPS_FAIL);
				}
			}
		}
	};

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case MSG_REFRESHLIST_AFTER_GET_APPS:
				if (isFinishing()) {
					Log.e("", "activity is finished!");
					break;
				}
				if (mProgressBar.getVisibility() == View.VISIBLE) {
					mProgressBar.setVisibility(View.GONE);
				}
				mListViewApps.setVisibility(View.VISIBLE);
				if (mAdapter == null) {
					mAdapter = new AllAppsListAdapter(AllAppsListActivity.this, mAppInfo, mSingleLine);
					mListViewApps.setAdapter(mAdapter);
				} else {
					mAdapter.notifyDataChanged(mAppInfo, mSingleLine);
				}
				break;

			case MSG_GET_APPS_FAIL:
				if (mProgressBar.getVisibility() == View.VISIBLE) {
					mProgressBar.setVisibility(View.GONE);
				}
				new AlertDialog.Builder(AllAppsListActivity.this).setTitle("错误提示").setMessage("获取程序列表失败，请终止应用进程后重试！").setPositiveButton("确定", null).setCancelable(false).create().show();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 触发线程走出阻塞，读取cpu和内存
	 * 
	 * @param
	 * @author zhuanggy
	 * @date 2014-1-15
	 */
	private void putGetAppsStep() {
		try {
			mBlockPackageStep.put("test");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		showListOperaDialog(mAdapter.getShowingAppsList().get(position));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_apps_refresh:
			// 刷新
			mListViewApps.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
			putGetAppsStep();

			break;
		case R.id.img_apps_back:
			// 返回
			finish();
			break;
		default:
			break;
		}
	}

	private void showListOperaDialog(final AppInfo appInfo) {

		final OperasListDialog dialogMode = new OperasListDialog(this, R.style.dialog);

		List<Operas> listOpera = new ArrayList<Operas>();
		listOpera.add(new Operas(Operas.OPERA_ID_SHOW_INFO, "程序信息", null));
		listOpera.add(new Operas(Operas.OPERA_ID_START, "打开此程序", null));

		dialogMode.setValues(listOpera);

		if (appInfo.appLogo != null) {
			Drawable[] icons = new Drawable[1];
			icons[0] = appInfo.appLogo;
			dialogMode.setIconDrawable(icons);
		}

		dialogMode.setOnItemClickedListener(new OnItemClickedListener() {

			@Override
			public void onItemClicked(Operas opear) {
				switch (opear.id) {

				case Operas.OPERA_ID_SHOW_INFO:
					showAppInfosDlg(appInfo);
					break;

				case Operas.OPERA_ID_START:
					dialogMode.dismiss();

					if (appInfo.pkgName.equals(AllAppsListActivity.this.getPackageName())) {
						Toast.makeText(AllAppsListActivity.this, "当前程序已打开", Toast.LENGTH_LONG).show();
						return;
					}
					Intent intent = getIntent(appInfo.pkgName);
					try {
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(AllAppsListActivity.this, "打开失败！", Toast.LENGTH_LONG).show();
					}
					break;
				case Operas.OPERA_ID_UNINSTALL:

					break;

				default:
					break;
				}

			}
		});

		dialogMode.show();

	}

	private void showAppInfosDlg(AppInfo appInfo) {
		AppInfo[] info = new AppInfo[1];
		info[0] = appInfo;
		final AppsInfoDialog dialogMode = new AppsInfoDialog(this, R.style.dialog, MainApplication.mPackageManager);
		dialogMode.setApps(info, false);
		dialogMode.show();

		// 全屏
		WindowManager.LayoutParams params = dialogMode.getWindow().getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		AllAppsListActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		params.width = dm.widthPixels;
		params.height = dm.heightPixels;
		dialogMode.getWindow().setAttributes(params);
		dialogMode.getWindow().setGravity(Gravity.CENTER);
	}

	public Intent getIntent(String packageName) {
		Intent intent = null;
		try {
			intent = MainApplication.mPackageManager.getLaunchIntentForPackage(packageName);
			if (intent != null) {
				intent = intent.cloneFilter();
				// intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				return intent;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
