package com.zgy.debugtool.appinfo;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

@SuppressLint("NewApi")
public class AppInfo {

	public String appName;
	public Drawable appLogo;
	public String pkgName;
	public long size;
	public long updateTime;

	public AppInfo() {

	}

	public AppInfo(PackageManager pm, PackageInfo pkginfo) {
		this.appName = pm.getApplicationLabel(pkginfo.applicationInfo).toString();
		this.appLogo = pkginfo.applicationInfo.loadIcon(pm);
		this.pkgName = pkginfo.packageName;
		size = pkginfo.applicationInfo.publicSourceDir.length() + pkginfo.applicationInfo.sourceDir.length();
		
		if (android.os.Build.VERSION.SDK_INT > 8) {
			this.updateTime = pkginfo.lastUpdateTime;
		}
	}

}
