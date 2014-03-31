package com.zgy.debugtool.appinfo;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zgy.debugtool.main.MainApplication;
import com.zgy.debugtool.main.R;
import com.zgy.debugtool.util.JumpUtil;
import com.zgy.debugtool.util.TimeUtil;
import com.zgy.debugtool.view.MarqueeTextView;

/**
 * 运行程序的信息弹窗
 * 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version
 * @since
 */
public class AppsInfoDialog extends Dialog {
	private Context con;
	private AppInfo[] apps;
	private View.OnClickListener cancelListener;
	private AppinfoListAdapter mAdapter;
	private TextView titleView;
	private ListView listViews;
	private ImageView cancelButton;
	private String title;
	private boolean isProcessInfo;

	private static final String TAG_NEW_APP = "new app";

	public AppsInfoDialog(Context context, int theme, PackageManager pm) {
		super(context, theme);
		this.con = context;
//		this.pm = pm;
	}

	public void setCancelListener(View.OnClickListener clickListener) {
		this.cancelListener = clickListener;
	}

	public void setApps(AppInfo[] appinfos, boolean isProcess) {
		this.apps = appinfos;
		this.isProcessInfo = isProcess;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_dialog_apps);
		initView();
	}

	public void initView() {
		titleView = (TextView) findViewById(R.id.title_list_dialog_apps);
		if (title != null && title.length() > 0) {
			titleView.setText(title);
		}
		listViews = (ListView) findViewById(R.id.list_dialog_apps_lists);
		cancelButton = (ImageView) findViewById(R.id.img_list_dialog_apps_close);
		if (cancelListener != null) {
			cancelButton.setOnClickListener(cancelListener);
		} else {
			cancelButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					AppsInfoDialog.this.cancel();
				}

			});
		}
		
		loadInfoInThread();
	}

	
	
	@SuppressLint("NewApi")
	private void loadInfoInThread() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//异步读取详细信息
				final List<AppsInfoItemTag> appinfos = new ArrayList<AppsInfoItemTag>();
				for (AppInfo appinfo : apps) {
					try {
						PackageInfo app = MainApplication.mPackageManager.getPackageInfo(appinfo.pkgName, PackageManager.GET_SERVICES | PackageManager.GET_GIDS | PackageManager.GET_ACTIVITIES | PackageManager.GET_PROVIDERS | PackageManager.GET_RECEIVERS | PackageManager.GET_SIGNATURES);
						 
						AppsInfoItemTag newapp = new AppsInfoItemTag(TAG_NEW_APP, appinfo.pkgName);
//						newapp.icon = app.applicationInfo.loadIcon(MainApplication.mPackageManager);
						newapp.icon = appinfo.appLogo;
//						newapp.name = (String) MainApplication.mPackageManager.getApplicationLabel(app.applicationInfo);
						newapp.name = appinfo.appName;
						appinfos.add(newapp);
						
						if(isProcessInfo) {
							appinfos.add(new AppsInfoItemTag("processName", app.applicationInfo.processName));
						} else {
							appinfos.add(new AppsInfoItemTag("packageName", appinfo.pkgName));
						}
						
						
						// appinfos.add(new Appinfo("className", app.applicationInfo.className));
						appinfos.add(new AppsInfoItemTag("dataDir", app.applicationInfo.dataDir));
						appinfos.add(new AppsInfoItemTag("sourceDir", app.applicationInfo.sourceDir));
						// appinfos.add(new Appinfo("publicSourceDir", app.applicationInfo.publicSourceDir));
						// appinfos.add(new Appinfo("uid", app.applicationInfo.uid + ""));
						// appinfos.add(new Appinfo("description", app.applicationInfo.descriptionRes == 0 ? "" : con.getString(app.applicationInfo.descriptionRes)));
						appinfos.add(new AppsInfoItemTag("targetSdkVersion", app.applicationInfo.targetSdkVersion + ""));
						appinfos.add(new AppsInfoItemTag("versionName", app.versionName));

						appinfos.add(new AppsInfoItemTag("activities", AppsInfoClassCompareUtil.praseActivity(app.activities)));
						appinfos.add(new AppsInfoItemTag("services", AppsInfoClassCompareUtil.praseService(app.services)));
						appinfos.add(new AppsInfoItemTag("receivers", AppsInfoClassCompareUtil.praseReceiver(app.receivers)));
						appinfos.add(new AppsInfoItemTag("providers", AppsInfoClassCompareUtil.praseProvider(app.providers)));
						if (app.signatures != null) {
							try {
								 Signature s = app.signatures[0];
								CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
								X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(s.toByteArray()));
								String pubKey = cert.getPublicKey().toString();
								String signNumber = cert.getSerialNumber().toString();
								appinfos.add(new AppsInfoItemTag("signatures", "pubKey:" + pubKey + "\r\n\r\nsignNumber:" + signNumber));
							} catch (Exception e) {
								appinfos.add(new AppsInfoItemTag("signatures", "null"));
							}
						} else {
							appinfos.add(new AppsInfoItemTag("signatures", "null"));
						}

						if (android.os.Build.VERSION.SDK_INT > 8) {
							appinfos.add(new AppsInfoItemTag("firstInstallTime", TimeUtil.longToDateTimeString(app.firstInstallTime)));
							appinfos.add(new AppsInfoItemTag("lastUpdateTime", TimeUtil.longToDateTimeString(app.lastUpdateTime)));
						}

						// appinfos.add(new Appinfo("targetSdkVersion", app.applicationInfo.targetSdkVersion + ""));
						// appinfos.add(new Appinfo("targetSdkVersion", app.applicationInfo.targetSdkVersion + ""));
						// appinfos.add(new Appinfo("targetSdkVersion", app.applicationInfo.targetSdkVersion + ""));
						// appinfos.add(new Appinfo("targetSdkVersion", app.applicationInfo.targetSdkVersion + ""));
						// appinfos.add(new Appinfo("targetSdkVersion", app.applicationInfo.targetSdkVersion + ""));
						// appinfos.add(new Appinfo("targetSdkVersion", app.applicationInfo.targetSdkVersion + ""));
						// appinfos.add(new Appinfo("targetSdkVersion", app.applicationInfo.targetSdkVersion + ""));

						// if(app.requestedPermissions != null) {
						// int permissionCount = app.requestedPermissions.length;
						// StringBuffer sb = new StringBuffer();
						// for (int i=0; i< permissionCount; i++) {
						// if(i < permissionCount-1) {
						// sb.append(app.requestedPermissions[i]+ "\r\n");
						// } else {
						// sb.append(app.requestedPermissions[i]);
						// }
						// }
						// appinfos.add(new Appinfo("permission", sb.toString()));
						// } else {
						// appinfos.add(new Appinfo("permission", "没有任何权限"));
						// }

						// if(app.permissions != null) {
						// int permissionCount = app.permissions.length;
						// StringBuffer sb = new StringBuffer();
						// for (int i=0; i< permissionCount; i++) {
						// if(i < permissionCount-1) {
						// sb.append(app.permissions[i].name+ "\r\n");
						// } else {
						// sb.append(app.permissions[i].name);
						// }
						// }
						// appinfos.add(new Appinfo("permission", sb.toString()));
						// } else {
						// appinfos.add(new Appinfo("permission", "没有任何权限"));
						// }
					} catch (Exception e) {
					e.printStackTrace();
					}
					

				}
				
				((Activity) con).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						listViews.setAdapter(new AppinfoListAdapter(appinfos));
					}
				});
				
				
			}
		}).start();
		
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
	class AppinfoListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		private List<AppsInfoItemTag> mAppInfos;

		public AppinfoListAdapter(List<AppsInfoItemTag> appInfos) {
			this.mAppInfos = appInfos;
			mInflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mAppInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return mAppInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_dialog_apps_item, null);
				holder = new ViewHolder();
				holder.textLabel = (TextView) convertView.findViewById(R.id.text_listitem_appinfo_label);
				holder.textValue = (MarqueeTextView) convertView.findViewById(R.id.text_listitem_appinfo_value);
				holder.layoutTag = (RelativeLayout) convertView.findViewById(R.id.layout_listitem_appinfo_tag);
				holder.layoutContent = (LinearLayout) convertView.findViewById(R.id.layout__listitem_appinfo_content);
				holder.textAppname = (MarqueeTextView) convertView.findViewById(R.id.text_listitem_appinfo_name);
				holder.imgAppIcon = (ImageView) convertView.findViewById(R.id.img_listitem_appinfo_icon);
				holder.btnMore = (Button) convertView.findViewById(R.id.btn_listitem_appinfo_more);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (mAppInfos.get(position).label.equals(TAG_NEW_APP)) {
				holder.layoutContent.setVisibility(View.GONE);
				holder.layoutTag.setVisibility(View.VISIBLE);
				if (mAppInfos.get(position).icon != null) {
					holder.imgAppIcon.setVisibility(View.VISIBLE);
					holder.imgAppIcon.setImageDrawable(mAppInfos.get(position).icon);
				} else {
					holder.imgAppIcon.setVisibility(View.GONE);
				}
				holder.textAppname.setText(mAppInfos.get(position).name + "");
				holder.textAppname.setMarquee(true);
				holder.btnMore.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						JumpUtil.showInstalledAppDetails(con, mAppInfos.get(position).value);
					}
				});
			} else {
				holder.layoutContent.setVisibility(View.VISIBLE);
				holder.layoutTag.setVisibility(View.GONE);
				holder.textLabel.setText(mAppInfos.get(position).label);
				if(mAppInfos.get(position).value == null && mAppInfos.get(position).ssb != null) {
					holder.textValue.setText(mAppInfos.get(position).ssb);
				} else {
					holder.textValue.setText(mAppInfos.get(position).value);
				}
				
			}

			return convertView;
		}

		public class ViewHolder {
			TextView textLabel;
			MarqueeTextView textValue;
			RelativeLayout layoutTag;
			LinearLayout layoutContent;
			MarqueeTextView textAppname;
			ImageView imgAppIcon;
			Button btnMore;
		}

	}

}
