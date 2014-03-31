package com.zgy.debugtool.appinfo;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.zgy.debugtool.main.R;
import com.zgy.debugtool.util.ScreenUtil;
import com.zgy.debugtool.view.MarqueeTextView;

/**
 * 进程的点击操作列表弹窗
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class OperasListDialog extends Dialog {
	private Context con;
	private List<Operas> values;
	private View.OnClickListener cancelListener;
	private DialogAdapter mAdapter;
	private TextView titleView;
	private ListView listView;
	private ImageView cancelButton;
	private String title;
	private Drawable[] icons;

	private OnItemClickedListener mOnItemClickedListener;

	public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
		this.mOnItemClickedListener = onItemClickedListener;
	}

	public OperasListDialog(Context context, int theme) {
		super(context, theme);
		con = context;
	}

	public void setCancelListener(View.OnClickListener clickListener) {
		this.cancelListener = clickListener;
	}

	public void setTitleShow(String title) {
		this.title = title;
	}

	public void setValues(List<Operas> values) {
		this.values = values;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setIconDrawable(Drawable[] ic) {
		this.icons = ic;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_dialog_processopera);
		mAdapter = new DialogAdapter();
		initView();
	}

	public void initView() {
		if (icons != null) {
			LinearLayout layoutIcons = (LinearLayout) findViewById(R.id.layout_list_dialog_opera_icons);
			layoutIcons.removeAllViews();
			for (Drawable d : icons) {
				ImageView img = new ImageView(con);
				int wh=ScreenUtil.convertDIP2PX(con, 40);
				LayoutParams lp = new LayoutParams(wh, wh);
				lp.setMargins(ScreenUtil.convertDIP2PX(con, 10), 0, 0, 0);
				img.setLayoutParams(lp);
				img.setImageDrawable(d);
				layoutIcons.addView(img);
			}
		}
		titleView = (TextView) findViewById(R.id.text_title_list_dialog_opera);
		if (title != null) {
			titleView.setText(title);
		}
		listView = (ListView) findViewById(R.id.value_list);
		listView.setAdapter(mAdapter);
		cancelButton = (ImageView) findViewById(R.id.img_list_dialog_opera_close);
		if (cancelListener != null) {
			cancelButton.setOnClickListener(cancelListener);
		} else {
			cancelButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					OperasListDialog.this.cancel();
				}

			});
		}
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mOnItemClickedListener != null) {
					mOnItemClickedListener.onItemClicked(values.get(position));
				}
			}

		});
	}

	/**
	 * 
	 * @Description:dialog的ListView 的Adapter
	 * @author:hanlx
	 * @see:
	 * @since:
	 * @copyright © 35.com
	 * @Date:2013-1-14
	 */
	class DialogAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public DialogAdapter() {
			mInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return values.size();
		}

		@Override
		public Object getItem(int position) {
			return values.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			TextView value;
			MarqueeTextView summary;

			if (convertView == null) {
				view = mInflater.inflate(R.layout.list_dialog_processopera_item, parent, false);
			} else {
				view = convertView;
			}
			value = (TextView) view.findViewById(R.id.value);
			summary = (MarqueeTextView) view.findViewById(R.id.summary);
			value.setText(values.get(position).name);
			if (values.get(position).summary != null) {
				summary.setVisibility(View.VISIBLE);
				summary.setText(values.get(position).summary);
			} else {
				summary.setVisibility(View.GONE);
			}

			return view;
		}

	}

	public interface OnItemClickedListener {
		public void onItemClicked(Operas opera);
	}

}
