package com.zgy.debugtool.batteryinfo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgy.debugtool.main.R;

/**
 *  电池信息列表的适配 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class BatteryListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;

	private List<BatteryItem> mBatteryList;

	public BatteryListAdapter(Context context, List<BatteryItem> batterys) {
		this.mBatteryList = batterys;
		mInflater = LayoutInflater.from(context);
	}

	public void notifyDataChanged(List<BatteryItem> batterys) {
		this.mBatteryList = batterys;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mBatteryList.size();
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
			convertView = mInflater.inflate(R.layout.listrow_battery, null);
			holder = new ViewHolder();
			holder.textLabel = (TextView) convertView.findViewById(R.id.text_batteryitem_label);
			holder.textValue = (TextView) convertView.findViewById(R.id.text_batteryitem_value);
			holder.imgLogo = (ImageView) convertView.findViewById(R.id.img_batteryitem_value);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.textLabel.setText(mBatteryList.get(position).label);
		if (position == 0) {
			holder.imgLogo.setVisibility(View.VISIBLE);
			holder.imgLogo.setImageResource(mBatteryList.get(position).imgId);
		} else {
			holder.imgLogo.setVisibility(View.GONE);
		}
		holder.textValue.setText(mBatteryList.get(position).value);
		return convertView;
	}

	private class ViewHolder {

		TextView textLabel;
		TextView textValue;
		ImageView imgLogo;
	}
}
