package com.zgy.debugtool.appinfo;

import java.util.Comparator;

/**
 * 用于排序的类
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class AppsInfoClassCompare implements Comparator<Object> {

	 
	@Override
	public int compare(Object lhs, Object rhs) {

		AppsInfoClass cla1 = (AppsInfoClass) lhs;
		AppsInfoClass cla2 = (AppsInfoClass) rhs;
		 
		return cla1.packageName.compareTo(cla2.packageName);
	}

	 
}
