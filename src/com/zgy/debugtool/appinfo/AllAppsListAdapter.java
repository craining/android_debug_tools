package com.zgy.debugtool.appinfo;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zgy.debugtool.main.R;
import com.zgy.debugtool.util.TimeUtil;
import com.zgy.debugtool.view.MarqueeTextView;

/**
 * 进程列表页，列的适配
 * 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version
 * @since
 */
public class AllAppsListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<AppInfo> mAppsList;
	private boolean mSingleLine;
	private Context mContext;

	public AllAppsListAdapter(Context context, List<AppInfo> apps, boolean singleLine) {
		this.mAppsList = apps;
		this.mContext = context;
		this.mSingleLine = singleLine;
		mInflater = LayoutInflater.from(context);
	}

	public void notifyDataChanged(List<AppInfo> apps, boolean singleLine) {
		this.mAppsList = apps;
		this.mSingleLine = singleLine;
		notifyDataSetChanged();
	}

	public List<AppInfo> getShowingAppsList() {
		return mAppsList;
	}

	@Override
	public int getCount() {
		return mAppsList.size();
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
			convertView = mInflater.inflate(R.layout.listrow_apps, null);
			holder = new ViewHolder();
			holder.imgLogo = (ImageView) convertView.findViewById(R.id.img_appsitem_app_icon);
			holder.textName = (MarqueeTextView) convertView.findViewById(R.id.text_appsitem_name);
			holder.textPackageTime = (MarqueeTextView) convertView.findViewById(R.id.text_appsitem_package_time);
			holder.textSize = (MarqueeTextView) convertView.findViewById(R.id.text_appsitem_size);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		SpannableStringBuilder name = null;
		if (mSingleLine) {
			holder.textName.setMarquee(true);
			holder.textPackageTime.setMarquee(true);
			holder.textSize.setMarquee(true);
		} else {
			holder.textName.setMarquee(false);
			holder.textPackageTime.setMarquee(false);
			holder.textSize.setMarquee(false);
		}
		if(mAppsList.get(position).appLogo != null) {
			holder.imgLogo.setImageDrawable(mAppsList.get(position).appLogo);
		}
		if (android.os.Build.VERSION.SDK_INT > 8) {
			holder.textPackageTime.setText(TimeUtil.longToDateTimeString(mAppsList.get(position).updateTime) + "\r\n" + mAppsList.get(position).pkgName); 
		} else {
			holder.textPackageTime.setText(mAppsList.get(position).pkgName); 
		}
		holder.textName.setText(mAppsList.get(position).appName==null ? "" : mAppsList.get(position).appName);
//		holder.textSize.setText(android.text.format.Formatter.formatFileSize(mContext, mAppsList.get(position).size)); 
		return convertView;
	}

	private class ViewHolder {

		ImageView imgLogo;
		MarqueeTextView textName;
		MarqueeTextView textPackageTime;
		MarqueeTextView textSize;
	}
}
