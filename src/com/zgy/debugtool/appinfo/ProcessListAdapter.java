package com.zgy.debugtool.appinfo;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.zgy.debugtool.main.R;
import com.zgy.debugtool.util.ScreenUtil;
import com.zgy.debugtool.util.Util;
import com.zgy.debugtool.util.ViewUtil;
import com.zgy.debugtool.view.MarqueeTextView;

/**
 * 进程列表页，列的适配
 * 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version
 * @since
 */
public class ProcessListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Process> mProcessList;
	private boolean mSingleLine;
	private Context mContext;

	public ProcessListAdapter(Context context, List<Process> processes, boolean singleLine) {
		this.mProcessList = processes;
		this.mContext = context;
		this.mSingleLine = singleLine;
		mInflater = LayoutInflater.from(context);
	}

	public void notifyDataChanged(List<Process> processes, boolean singleLine) {
		this.mProcessList = processes;
		this.mSingleLine = singleLine;
		notifyDataSetChanged();
	}

	public List<Process> getShowingProcessList() {
		return mProcessList;
	}

	@Override
	public int getCount() {
		return mProcessList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listrow_process, null);
			holder = new ViewHolder();
			holder.textPid = (MarqueeTextView) convertView.findViewById(R.id.text_processitem_pid);
			holder.textUid = (MarqueeTextView) convertView.findViewById(R.id.text_processitem_uid);
			holder.textPackage = (MarqueeTextView) convertView.findViewById(R.id.text_processitem_package);
			holder.textMemory = (MarqueeTextView) convertView.findViewById(R.id.text_processitem_memory);
			holder.layoutAppIcons = (LinearLayout) convertView.findViewById(R.id.layout_processitem_app_icons);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SpannableStringBuilder name = null;
		if (mSingleLine) {
			holder.textPid.setMarquee(true);
			holder.textUid.setMarquee(true);
			holder.textPackage.setMarquee(true);
			holder.textMemory.setMarquee(true);
			holder.layoutAppIcons.setVisibility(View.GONE);
			name = ViewUtil.getProcessPKGnameSpan(mProcessList.get(position).processInfo.processName.trim() + "; ", Util.getStringOfStringArray(mProcessList.get(position).processInfo.pkgList, false));
		} else {
			holder.layoutAppIcons.setVisibility(View.VISIBLE);
			holder.textPid.setMarquee(false);
			holder.textUid.setMarquee(false);
			holder.textPackage.setMarquee(false);
			holder.textMemory.setMarquee(false);
			name = ViewUtil
					.getProcessPKGnameSpan(mProcessList.get(position).processInfo.processName.trim() + "\r\n", Util.getStringOfStringArray(mProcessList.get(position).processInfo.pkgList, true));

			holder.layoutAppIcons.removeAllViews();
			
			for (AppInfo appInfo : mProcessList.get(position).appsInfos) {
				Drawable icon = appInfo.appLogo;
				if (icon != null) {
					ImageView img = new ImageView(mContext);
					int wh = ScreenUtil.convertDIP2PX(mContext, 30);
					LayoutParams lp = new LayoutParams(wh, wh);
					lp.setMargins(0, ScreenUtil.convertDIP2PX(mContext, 5), 0, 0);
					img.setLayoutParams(lp);
					img.setImageDrawable(icon);
					holder.layoutAppIcons.addView(img);
				}
			}
			
		}
		holder.textPid.setText(mProcessList.get(position).processInfo.pid + "");
		holder.textUid.setText(mProcessList.get(position).processInfo.uid + "");
		holder.textMemory.setText(Util.sizeLongToString(mProcessList.get(position).memoryInfo.getTotalPrivateDirty() * 1000));// 占用内存信息
		if (name != null) {
			holder.textPackage.setText(name);
		}
		convertView.setBackgroundResource(mProcessList.get(position).isLocked ? R.drawable.selector_process_item_bg_locked : R.drawable.selector_process_item_bg);
		return convertView;
	}

	private class ViewHolder {

		MarqueeTextView textPid;
		MarqueeTextView textUid;
		MarqueeTextView textPackage;
		MarqueeTextView textMemory;
		LinearLayout layoutAppIcons;
	}
}
