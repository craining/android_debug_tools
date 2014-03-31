package com.zgy.debugtool.appinfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;

/**
 * app信息展示时用于排序类名的工具类
 * 
 * @Author zhuanggy
 * @Date:2014-3-25
 * @version
 * @since
 */
public class AppsInfoClassCompareUtil {

	private static void addClass(List<AppsInfoClass> classList, String packageName, String className) {
		boolean contains = false;
		for (AppsInfoClass clas : classList) {
			if (clas.packageName.equals(packageName)) {
				contains = true;
				if (clas.classes == null) {
					clas.classes = new ArrayList<String>();
				}
				clas.classes.add(className);
				break;
			}
		}
		if (!contains) {
			AppsInfoClass cls = new AppsInfoClass();
			cls.packageName = packageName;
			cls.classes = new ArrayList<String>();
			cls.classes.add(className);
			classList.add(cls);
		}
	}

	private static SpannableStringBuilder sortClasses(int count, List<AppsInfoClass> classList) {

		if (count > 0) {
			StringBuffer sb = new StringBuffer();
			// 排序按包名排序，每个包按类名排序
			sb.append(count + "个\r\n");
			Collections.sort(classList, new AppsInfoClassCompare());
			List<int[]> pkgInfos = new ArrayList<int[]>();

			for (AppsInfoClass cls : classList) {
				int[] pkginfo = new int[2];
				pkginfo[0] = sb.toString().length();
				pkginfo[1] = pkginfo[0] + cls.packageName.length();
				pkgInfos.add(pkginfo);
//				Log.e("", "pkginfo[0]=" + pkginfo[0] + "  pkginfo[1]=" + pkginfo[1]);
				sb.append(cls.packageName + "\r\n");
				
				
				Collections.sort(cls.classes);
				for (String clasname : cls.classes) {
					sb.append("\t").append(clasname).append("\r\n");
				}
			}
			return getTextStyle(sb.toString(), pkgInfos);
		}

		return getTextStyle("没有", null);
	}

	public static SpannableStringBuilder praseActivity(ActivityInfo[] acs) {
		if (acs == null) {
			return sortClasses(0, null);
		}
		int count = 0;
		List<AppsInfoClass> classes = new ArrayList<AppsInfoClass>();
		for (ActivityInfo ac : acs) {
			String className = "";
			Log.i("", "ac.name=" + ac.name + "ac.packageName=" + ac.packageName);
			if (ac.name.contains(".")) {
				String[] names = ac.name.split("\\.");
				className = names[names.length - 1];
				count++;
				addClass(classes, ac.name.replace("." + className, ""), "." + className);
			} else {
				count++;
				addClass(classes, ac.name, "." + className);
			}

		}
		return  sortClasses(count, classes);
	}

	public static SpannableStringBuilder praseService(ServiceInfo[] sers) {
		if (sers == null) {
			return sortClasses(0, null);
		}
		int count = 0;
		List<AppsInfoClass> classes = new ArrayList<AppsInfoClass>();
		for (ServiceInfo ac : sers) {
			String className = "";
			if (ac.name.contains(".")) {
				String[] names = ac.name.split("\\.");
				className = names[names.length - 1];
				count++;
				addClass(classes, ac.name.replace("." + className, ""), "." + className);
			} else {
				count++;
				addClass(classes, ac.name, "." + className);
			}

		}
		return sortClasses(count, classes);
	}

	public static SpannableStringBuilder praseProvider(ProviderInfo[] acs) {
		if (acs == null) {
			return sortClasses(0, null);
		}
		int count = 0;
		List<AppsInfoClass> classes = new ArrayList<AppsInfoClass>();
		for (ProviderInfo ac : acs) {
			String className = "";
			if (ac.name.contains(".")) {
				String[] names = ac.name.split("\\.");
				className = names[names.length - 1];
				count++;
				addClass(classes, ac.name.replace("." + className, ""), "." + className);
			} else {
				count++;
				addClass(classes, ac.name, "." + className);
			}

		}
		return sortClasses(count, classes);
	}

	public static SpannableStringBuilder praseReceiver(ActivityInfo[] acs) {
		if (acs == null) {
			return sortClasses(0, null);
		}
		int count = 0;
		List<AppsInfoClass> classes = new ArrayList<AppsInfoClass>();
		for (ActivityInfo ac : acs) {
			String className = "";
			if (ac.name.contains(".")) {
				String[] names = ac.name.split("\\.");
				className = names[names.length - 1];
				count++;
				addClass(classes, ac.name.replace("." + className, ""), "." + className);
			} else {
				count++;
				addClass(classes, ac.name, "." + className);
			}

		}
		return sortClasses(count, classes);
	}

	/**
	 * 对包名进行颜色处理
	 * @param @param outPut
	 * @param @param packageNamesInfo
	 * @param @return 
	 * @author zhuanggy
	 * @date 2014-3-25
	 */
	private static SpannableStringBuilder getTextStyle(String outPut, List<int[]> packageNamesInfo) {
		SpannableStringBuilder style = new SpannableStringBuilder(outPut);
		if (packageNamesInfo == null) {
			return style;
		}
		for (int[] info : packageNamesInfo) {
			style.setSpan(new BackgroundColorSpan(Color.GREEN), info[0], info[1], Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		}
		return style;
	}
}
