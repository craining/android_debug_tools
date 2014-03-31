package com.zgy.debugtool.appinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.zgy.debugtool.appinfo.OperasListDialog.OnItemClickedListener;
import com.zgy.debugtool.main.MainApplication;
import com.zgy.debugtool.main.R;
import com.zgy.debugtool.util.PhoneUtil;
import com.zgy.debugtool.util.Util;

/**
 * 进程列表页
 * 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version
 * @since
 */
public class ProcessListActivity extends Activity implements OnClickListener, OnItemClickListener {

	private ListView mList;
	private ToggleButton mTBtnAutoProcessRefresh;
	private ToggleButton mTBtnAutoCPURefresh;
	private ToggleButton mTBtnSingleLine;
	private ImageView mImagback;

	private ProgressBar mProgressBar;

	private LinearLayout mLayoutCPUMemoryPannel;
	private TextView mTextSystemAllMemory;
	private TextView mTextSystemFreeMemory;
	private TextView mTextSystemCPU;

	private LinearLayout mLayoutPidItem;
	private LinearLayout mLayoutUidItem;
	private LinearLayout mLayoutMemoryItem;
	private RelativeLayout mLayoutNameItem;
	private ImageView mImagePidItem;
	private ImageView mImageUidItem;
	private ImageView mImageMemoryItem;
	private ImageView mImageNameItem;

	private ProcessListAdapter mAdapter;

//	private PackageManager mPackageM;
	private ActivityManager mActivityM;
	private List<Process> mProcesses;

	private static final long AUTO_REFRESH_PROGRESS_TIME = 2000;// 进程刷新间隔
	private static final long AUTO_REFRESH_CPU_MEMORY_TIME = 1000;// cpu使用频率和内存刷新间隔
	private static final long REFRESH_FLASH_TIME = 1;

	private static final int MSG_GET_PROCESS_INFO = 0x100;
	private static final int MSG_REFRESH_AFTER_GET_PROCESS_INFO = 0x101;
	private static final int MSG_SHOW_AFTER_GET_PROCESS_INFO = 0x107;

	private static final int MSG_GET_CPU_MEMORY_INFO = 0x103;
	private static final int MSG_REFRESH_AFTER_GET_CPU_MEMORY_INFO = 0x105;

	private static final int MSG_PROCESS_SHOW_AFTER_HIDE = 0x102;
	private static final int MSG_CPU_SHOW_AFTER_HIDE = 0x106;

	private BlockingQueue<String> mBlockProcessStep = new PriorityBlockingQueue<String>();
	private BlockingQueue<String> mBlockCpuMemoryStep = new PriorityBlockingQueue<String>();
	private MainHandler mHandler;
	private ProcessCompare mCompare;

	private ActivityManager.MemoryInfo mSystemMemoryInfo = new ActivityManager.MemoryInfo();
	private float mCPUUsageRate;

	private List<Integer> mListLockedProcess;

	private List<PackageInfo> mPackagesInfoList;
	private boolean mNeedReGetPackageInfo;

	private boolean mLockedItemTop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
		initView();
		init();
	}

	private void initView() {
		mList = (ListView) findViewById(R.id.lv_process);
		mTBtnAutoProcessRefresh = (ToggleButton) findViewById(R.id.tbtn_process_auto_refresh);
		mTBtnAutoCPURefresh = (ToggleButton) findViewById(R.id.tbtn_cpu_auto_refresh);
		mTBtnSingleLine = (ToggleButton) findViewById(R.id.tbtn_prcesslist_lines);
		mImagback = (ImageView) findViewById(R.id.img_process_back);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_processlist);

		mLayoutCPUMemoryPannel = (LinearLayout) findViewById(R.id.layout_cpu_pannel);
		mTextSystemAllMemory = (TextView) findViewById(R.id.text_process_system_memoryinfo_all);
		mTextSystemFreeMemory = (TextView) findViewById(R.id.text_process_system_memoryinfo_left);
		mTextSystemCPU = (TextView) findViewById(R.id.text_processlist_cpu_info);

		mLayoutPidItem = (LinearLayout) findViewById(R.id.layout_processlist_pid);
		mLayoutUidItem = (LinearLayout) findViewById(R.id.layout_processlist_uid);
		mLayoutMemoryItem = (LinearLayout) findViewById(R.id.layout_processlist_memory);
		mLayoutNameItem = (RelativeLayout) findViewById(R.id.layout_processlist_package);

		mImagePidItem = (ImageView) findViewById(R.id.img_processlist_pid);
		mImageUidItem = (ImageView) findViewById(R.id.img_processlist_uid);
		mImageMemoryItem = (ImageView) findViewById(R.id.img_processlist_memory);
		mImageNameItem = (ImageView) findViewById(R.id.img_processlist_name);

		mImagback.setOnClickListener(this);
		mTBtnAutoProcessRefresh.setOnClickListener(this);
		mTBtnAutoCPURefresh.setOnClickListener(this);
		mLayoutPidItem.setOnClickListener(this);
		mLayoutUidItem.setOnClickListener(this);
		mLayoutMemoryItem.setOnClickListener(this);
		mLayoutNameItem.setOnClickListener(this);
		mTBtnSingleLine.setOnClickListener(this);
		mList.setOnItemClickListener(this);
	}

	private void init() {
		mListLockedProcess = new ArrayList<Integer>();
//		mPackageM = getApplicationContext().getPackageManager();
		mActivityM = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));

		mTBtnAutoCPURefresh.setChecked(true);
		mTBtnAutoProcessRefresh.setChecked(false);
		mHandler = new MainHandler();
		mCompare = new ProcessCompare();
		mTextSystemAllMemory.setText("共  " + PhoneUtil.readTotalMemory());
		refreshMemoryCpuShow(false);
		refreshItemTitle();

		new Thread(mRunnableUpdateProcessInThread).start();
		new Thread(mRunnableUpdateCpuMemoryInThread).start();
		putProgressStep();
	}

	private void refreshItemTitle() {
		int sortType = mCompare.getSrotItem();
		boolean sortOrder = mCompare.isOrderAsc();
		mLayoutPidItem.setBackgroundColor(Color.TRANSPARENT);
		mLayoutUidItem.setBackgroundColor(Color.TRANSPARENT);
		mLayoutMemoryItem.setBackgroundColor(Color.TRANSPARENT);
		mLayoutNameItem.setBackgroundColor(Color.TRANSPARENT);
		mImagePidItem.setVisibility(View.INVISIBLE);
		mImageUidItem.setVisibility(View.INVISIBLE);
		mImageMemoryItem.setVisibility(View.INVISIBLE);
		mImageNameItem.setVisibility(View.INVISIBLE);
		switch (sortType) {
		case ProcessCompare.ORDER_TYPE_MEMORY:
			mLayoutMemoryItem.setBackgroundColor(Color.GREEN);
			mImageMemoryItem.setVisibility(View.VISIBLE);
			mImageMemoryItem.setImageResource(!sortOrder ? R.drawable.ic_down : R.drawable.ic_up);
			break;
		case ProcessCompare.ORDER_TYPE_NAME:
			mLayoutNameItem.setBackgroundColor(Color.GREEN);
			mImageNameItem.setVisibility(View.VISIBLE);
			mImageNameItem.setImageResource(!sortOrder ? R.drawable.ic_down : R.drawable.ic_up);
			break;
		case ProcessCompare.ORDER_TYPE_PID:
			mLayoutPidItem.setBackgroundColor(Color.GREEN);
			mImagePidItem.setVisibility(View.VISIBLE);
			mImagePidItem.setImageResource(!sortOrder ? R.drawable.ic_down : R.drawable.ic_up);
			break;
		case ProcessCompare.ORDER_TYPE_UID:
			mLayoutUidItem.setBackgroundColor(Color.GREEN);
			mImageUidItem.setVisibility(View.VISIBLE);
			mImageUidItem.setImageResource(!sortOrder ? R.drawable.ic_down : R.drawable.ic_up);
			break;

		default:
			break;
		}

	}

	/**
	 * 获取完进程数据，刷新列表
	 * 
	 * @param flash
	 *            true 闪动，说明在刷新
	 * @author zhuanggy
	 * @date 2014-1-15
	 */
	private void refreshProcessList(boolean flash, boolean repeat, boolean checkBeforeShow) {
		if (mCompare == null || mProcesses == null || mSystemMemoryInfo == null) {
			return;
		}
		if (checkBeforeShow && !mTBtnAutoProcessRefresh.isChecked() && mProgressBar.getVisibility() != View.VISIBLE) {
			return;
		}
		if (mProgressBar.getVisibility() == View.VISIBLE) {
			mProgressBar.setVisibility(View.GONE);
		}

		for (Process p : mProcesses) {
			p.isLocked = mListLockedProcess.contains(p.processInfo.pid) ? true : false;
		}
		mCompare.setLockedTop(mLockedItemTop);
		Collections.sort(mProcesses, mCompare);

		if (mAdapter == null) {
			mAdapter = new ProcessListAdapter(ProcessListActivity.this, mProcesses, mTBtnSingleLine.isChecked());
			mList.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataChanged(mProcesses, mTBtnSingleLine.isChecked());
		}

		if (flash) {
			mList.setVisibility(View.INVISIBLE);
			mHandler.sendEmptyMessageDelayed(MSG_PROCESS_SHOW_AFTER_HIDE, REFRESH_FLASH_TIME);
		}
		if (repeat && mTBtnAutoProcessRefresh.isChecked()) {
			mHandler.sendEmptyMessageDelayed(MSG_GET_PROCESS_INFO, AUTO_REFRESH_PROGRESS_TIME);
		}
	}

	/**
	 * 读取完cpu和内存，刷新UI
	 * 
	 * @param
	 * @author zhuanggy
	 * @date 2014-1-15
	 */
	private void refreshMemoryCpuShow(boolean flash) {

		mTextSystemFreeMemory.setText("内存:  剩余 " + Util.sizeLongToString(mSystemMemoryInfo.availMem));
		mTextSystemCPU.setText("CPU使用率：" + mCPUUsageRate + "%");

		if (mTBtnAutoCPURefresh.isChecked()) {
			mHandler.sendEmptyMessageDelayed(MSG_GET_CPU_MEMORY_INFO, AUTO_REFRESH_CPU_MEMORY_TIME);
		}

		if (flash) {
			// mLayoutCPUMemoryPannel.setVisibility(View.INVISIBLE);
			mTextSystemFreeMemory.setVisibility(View.INVISIBLE);
			mTextSystemCPU.setVisibility(View.INVISIBLE);

			mHandler.sendEmptyMessageDelayed(MSG_CPU_SHOW_AFTER_HIDE, REFRESH_FLASH_TIME);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// mPackagesInfoList = mPackageM.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

		mNeedReGetPackageInfo = true;
		
		if (!mTBtnAutoProcessRefresh.isChecked()) {
			refreshProcessInfoIfTBtnUnChecked();
		}
		if (!mTBtnAutoCPURefresh.isChecked()) {
			putCpuMemoryStep();
		}
	}

	@Override
	protected void onDestroy() {
		mTBtnAutoProcessRefresh.setChecked(false);
		mTBtnAutoCPURefresh.setChecked(false);

		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		showListOperaDialog(mAdapter.getShowingProcessList().get(position));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tbtn_process_auto_refresh:
			if (mTBtnAutoProcessRefresh.isChecked()) {
				putProgressStep();
			} else {
				mHandler.removeMessages(MSG_GET_PROCESS_INFO);
				mBlockProcessStep.clear();
			}
			break;
		case R.id.tbtn_cpu_auto_refresh:
			if (mTBtnAutoCPURefresh.isChecked()) {
				putCpuMemoryStep();
			} else {
				mHandler.removeMessages(MSG_GET_CPU_MEMORY_INFO);
				mBlockCpuMemoryStep.clear();
			}
			break;
		case R.id.img_process_back:
			finish();
			break;
		case R.id.layout_processlist_memory:
			if (mCompare.getSrotItem() == ProcessCompare.ORDER_TYPE_MEMORY) {
				mCompare.setOrderAsc(!mCompare.isOrderAsc());
			} else {
				mCompare.setSrotItem(ProcessCompare.ORDER_TYPE_MEMORY);
			}

			refreshProcessList(false, false, false);
			refreshItemTitle();
			break;
		case R.id.layout_processlist_package:
			if (mCompare.getSrotItem() == ProcessCompare.ORDER_TYPE_NAME) {
				mCompare.setOrderAsc(!mCompare.isOrderAsc());
			} else {
				mCompare.setSrotItem(ProcessCompare.ORDER_TYPE_NAME);
			}

			refreshProcessList(false, false, false);
			refreshItemTitle();
			break;
		case R.id.layout_processlist_pid:
			if (mCompare.getSrotItem() == ProcessCompare.ORDER_TYPE_PID) {
				mCompare.setOrderAsc(!mCompare.isOrderAsc());
			} else {
				mCompare.setSrotItem(ProcessCompare.ORDER_TYPE_PID);
			}
			refreshProcessList(false, false, false);
			refreshItemTitle();
			break;
		case R.id.layout_processlist_uid:
			if (mCompare.getSrotItem() == ProcessCompare.ORDER_TYPE_UID) {
				mCompare.setOrderAsc(!mCompare.isOrderAsc());
			} else {
				mCompare.setSrotItem(ProcessCompare.ORDER_TYPE_UID);
			}
			refreshProcessList(false, false, false);
			refreshItemTitle();
			break;

		case R.id.tbtn_prcesslist_lines:
			refreshProcessList(false, false, false);
			break;
		default:
			break;
		}

	}

	class MainHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

			case MSG_GET_PROCESS_INFO:
				Log.v("", "refresh");
				if (mTBtnAutoProcessRefresh.isChecked()) {
					putProgressStep();
				}
				break;
			case MSG_REFRESH_AFTER_GET_PROCESS_INFO:
				refreshProcessList(true, true, true);
				break;
			case MSG_SHOW_AFTER_GET_PROCESS_INFO:
				refreshProcessList(true, true, false);
				break;
			case MSG_PROCESS_SHOW_AFTER_HIDE:
				mList.setVisibility(View.VISIBLE);
				break;
			case MSG_GET_CPU_MEMORY_INFO:
				if (mTBtnAutoCPURefresh.isChecked()) {
					putCpuMemoryStep();
				}
				break;
			case MSG_CPU_SHOW_AFTER_HIDE:
				// mLayoutCPUMemoryPannel.setVisibility(View.VISIBLE);
				mTextSystemFreeMemory.setVisibility(View.VISIBLE);
				mTextSystemCPU.setVisibility(View.VISIBLE);
				break;
			case MSG_REFRESH_AFTER_GET_CPU_MEMORY_INFO:
				refreshMemoryCpuShow(true);
				break;
			default:
				break;
			}
		}

	}

	private void refreshProcessInfoIfTBtnUnChecked() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Log.e("", "refreshProcessInfoIfTBtnUnChecked   run");
				readProcessListInfo();
				mHandler.sendEmptyMessage(MSG_SHOW_AFTER_GET_PROCESS_INFO);
			}
		}).start();
	}

	/**
	 * 可以阻塞的线程，读取进程信息
	 */
	private Runnable mRunnableUpdateProcessInThread = new Runnable() {

		@Override
		public void run() {
			while (true) {
				String getStr = mBlockProcessStep.peek();
				try {
					getStr = mBlockProcessStep.take();
					Log.e("", "mRunnableUpdateProcessInThread   run");
					readProcessListInfo();
					mHandler.sendEmptyMessage(MSG_REFRESH_AFTER_GET_PROCESS_INFO);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(1000);
					} catch (Exception e2) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	/**
	 * 可以阻塞的线程，读取cpu和内存信息
	 */
	private Runnable mRunnableUpdateCpuMemoryInThread = new Runnable() {

		@Override
		public void run() {
			while (true) {
				String getStr = mBlockCpuMemoryStep.peek();
				try {
					getStr = mBlockCpuMemoryStep.take();
					Log.e("", "mRunnableUpdateCpuMemoryInThread run");
					readMemoryCPUInfo();
					mHandler.sendEmptyMessage(MSG_REFRESH_AFTER_GET_CPU_MEMORY_INFO);
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(1000);
					} catch (Exception e2) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	/**
	 * 读取cpu和内存
	 * 
	 * @param
	 * @author zhuanggy
	 * @date 2014-1-15
	 */
	private void readMemoryCPUInfo() {
		mCPUUsageRate = PhoneUtil.readCPUUsage();
		mActivityM.getMemoryInfo(mSystemMemoryInfo);
	}

	/**
	 * 读取进程信息
	 * 
	 * @param
	 * @author zhuanggy
	 * @date 2014-1-15
	 */
	private void readProcessListInfo() {
		if(mPackagesInfoList == null || mNeedReGetPackageInfo) {
			mNeedReGetPackageInfo = false;
			//  耗时
			Log.v("", "read package info");
			//PackageManager.GET_SERVICES | PackageManager.GET_GIDS | PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_SIGNATURES
			mPackagesInfoList = MainApplication.mPackageManager.getInstalledPackages(PackageManager.GET_GIDS);//| PackageManager.GET_CONFIGURATIONS 
		}
		List<RunningAppProcessInfo> processInfos = mActivityM.getRunningAppProcesses();
		int[] pids = new int[processInfos.size()];
		for (int i = 0; i < pids.length; i++) {
			pids[i] = processInfos.get(i).pid;
		}
		MemoryInfo[] memories = mActivityM.getProcessMemoryInfo(pids);
		int count = memories.length;
		mProcesses = new ArrayList<Process>();
		for (int i = 0; i < count; i++) {
			Process p = new Process();
			p.memoryInfo = memories[i];
			p.processInfo = processInfos.get(i);
			p.getPackageInfosAfterSetRunningAppProcessInfo(mPackagesInfoList, MainApplication.mPackageManager);
			mProcesses.add(p);
		}
	}

	/**
	 * 触发线程走出阻塞，读取进程信息
	 * 
	 * @param
	 * @author zhuanggy
	 * @date 2014-1-15
	 */
	private void putProgressStep() {
		try {
			mBlockProcessStep.put("test");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 触发线程走出阻塞，读取cpu和内存
	 * 
	 * @param
	 * @author zhuanggy
	 * @date 2014-1-15
	 */
	private void putCpuMemoryStep() {
		try {
			mBlockCpuMemoryStep.put("test");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void showListOperaDialog(final Process process) {

		final OperasListDialog dialogMode = new OperasListDialog(this, R.style.dialog);

		List<Operas> listOpera = new ArrayList<Operas>();
		listOpera.add(new Operas(Operas.OPERA_ID_SHOW_INFO, ((process.processInfo.pkgList.length > 1) ? "程序组信息" : "程序信息"), null));
		listOpera.add(new Operas(Operas.OPERA_ID_KILL, ((process.processInfo.pkgList.length > 1) ? "终止程序组" : "终止此程序"), "系统或顽固进程可能无法终止"));

		if (process.isLocked) {
			listOpera.add(new Operas(Operas.OPERA_ID_ALERT, "取消关注", "被关注的进程背景颜色突显"));
			listOpera.add(new Operas(Operas.OPERA_ID_ALERT_TOP, mLockedItemTop ? "被关注进程取消置顶" : "被关注进程始终置顶", mLockedItemTop ? "被关注的进程将不在列表顶部显示" : "被关注的进程将在列表顶部始终显示"));
		} else {
			listOpera.add(new Operas(Operas.OPERA_ID_ALERT, "关注此进程", "被关注的进程背景颜色突显"));
		}

		if (process.appsInfos != null && process.appsInfos.length == 1) {
			listOpera.add(new Operas(Operas.OPERA_ID_START, "打开此程序", null));
			dialogMode.setTitleShow(process.appsInfos[0].appName);
		}

		dialogMode.setValues(listOpera);

		if (process.appsInfos != null) {
		 
			Drawable[] icons = new Drawable[process.appsInfos.length];
			for(int i=0; i< process.appsInfos.length; i++) {
				icons[i] = process.appsInfos[i].appLogo;
			}
			dialogMode.setIconDrawable(icons);
		}

		dialogMode.setOnItemClickedListener(new OnItemClickedListener() {

			@Override
			public void onItemClicked(Operas opear) {
				switch (opear.id) {
				case Operas.OPERA_ID_KILL:
					dialogMode.dismiss();
					// 终止
					for (String pkgname : process.processInfo.pkgList) {
						try {
							if (pkgname.equals(getPackageName())) {
								// android.os.Process.killProcess(android.os.Process.myPid());
								// System.exit(0);
								new AlertDialog.Builder(ProcessListActivity.this).setIcon(R.drawable.ic_launcher).setTitle("特殊警告").setMessage("\r\n竟然终止我？不可能\r\n").setPositiveButton("好吧", null).setCancelable(false).create().show();
							} else {
								// mActivityM.killBackgroundProcesses(pkgname);
								mActivityM.restartPackage(pkgname);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (!mTBtnAutoProcessRefresh.isChecked()) {
						refreshProcessInfoIfTBtnUnChecked();
					}
					break;
				case Operas.OPERA_ID_SHOW_INFO:
					showAppInfosDlg(process.appsInfos);
					break;
				case Operas.OPERA_ID_ALERT:
					dialogMode.dismiss();
					if (process.isLocked) {
						mListLockedProcess.remove((Integer) process.processInfo.pid);
					} else {
						mListLockedProcess.add((Integer) process.processInfo.pid);
					}
					refreshProcessList(false, false, false);
					break;
				case Operas.OPERA_ID_ALERT_TOP:
					dialogMode.dismiss();
					mLockedItemTop = !mLockedItemTop;
					refreshProcessList(false, false, false);
					break;
				case Operas.OPERA_ID_START:
					dialogMode.dismiss();

					if (process.appsInfos[0].pkgName.equals(ProcessListActivity.this.getPackageName())) {
						Toast.makeText(ProcessListActivity.this, "当前程序已打开", Toast.LENGTH_LONG).show();
						return;
					}
					Intent intent = getIntent(process.appsInfos[0].pkgName);
					try {
						startActivity(intent);
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(ProcessListActivity.this, "打开失败！", Toast.LENGTH_LONG).show();
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

	private void showAppInfosDlg(AppInfo[] appinfos) {
		final AppsInfoDialog dialogMode = new AppsInfoDialog(this, R.style.dialog, MainApplication.mPackageManager);
		dialogMode.setApps(appinfos, true);
		dialogMode.show();

		// 全屏
		WindowManager.LayoutParams params = dialogMode.getWindow().getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		ProcessListActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
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
