package com.zgy.debugtool.appinfo;

import java.util.List;

import com.zgy.debugtool.util.ScreenUtil;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

/**
 * 为方便展示进程列表，而写的一个bean，进程列表页展示的进程信息都包含在此了
 * 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version
 * @since
 */
public class Process {

	public RunningAppProcessInfo processInfo;
	public MemoryInfo memoryInfo;
	public boolean isLocked;// 是否是关注的，被关注的进程背景突显
	// public PackageInfo[] packageInfos;//一个进程容器里，可能会有多个包，主要是系统级
	public AppInfo[] appsInfos;

	public void getPackageInfosAfterSetRunningAppProcessInfo(List<PackageInfo> systemPackageInfoList, PackageManager pm) {
		String[] pkgs = processInfo.pkgList;
		if (pkgs != null) {
			appsInfos = new AppInfo[pkgs.length];
			for (int i = 0; i < pkgs.length; i++) {
				for (PackageInfo packageInfo : systemPackageInfoList) {
					if (packageInfo.packageName.equals(pkgs[i])) {
						// packageInfos[i] = packageInfo;
						appsInfos[i] = new AppInfo();
						appsInfos[i].pkgName = packageInfo.packageName;
						appsInfos[i].appLogo = packageInfo.applicationInfo.loadIcon(pm);
						appsInfos[i].appName = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
						break;
					}
				}
			}
		}
	}

}
