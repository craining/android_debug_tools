package com.zgy.debugtool.appinfo;

import java.util.Comparator;


/**
 * 用于排序的类
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class ProcessCompare implements Comparator<Object> {

	public static final int ORDER_TYPE_PID = 0;
	public static final int ORDER_TYPE_MEMORY = 1;
	public static final int ORDER_TYPE_UID = 2;
	public static final int ORDER_TYPE_NAME = 3;

	private int mSortItem;
	private boolean mOrderAsc;
	private boolean mLockedTop;

	public boolean isLockedTop() {
		return mLockedTop;
	}

	public void setLockedTop(boolean LockedTop) {
		this.mLockedTop = LockedTop;
	}

	public boolean isOrderAsc() {
		return mOrderAsc;
	}

	public void setOrderAsc(boolean mSortAsc) {
		this.mOrderAsc = mSortAsc;
	}

	public int getSrotItem() {
		return mSortItem;
	}

	public void setSrotItem(int mOrderType) {
		this.mSortItem = mOrderType;
	}

	@Override
	public int compare(Object lhs, Object rhs) {

		Process process0 = (Process) lhs;
		Process process1 = (Process) rhs;

		int checkLockedResult = checkLocked(process0, process1);
		if (checkLockedResult != 0) {
			return checkLockedResult;
		}

		switch (mSortItem) {
		case ORDER_TYPE_PID:
			return mOrderAsc ? process0.processInfo.pid - process1.processInfo.pid : process1.processInfo.pid - process0.processInfo.pid;
		case ORDER_TYPE_UID:
			return mOrderAsc ? process0.processInfo.uid - process1.processInfo.uid : process1.processInfo.uid - process0.processInfo.uid;
		case ORDER_TYPE_NAME:
			// result = mOrderAsc ? Util.getStringOfStringArray(process0.processInfo.pkgList).compareTo(Util.getStringOfStringArray(process1.processInfo.pkgList)) : Util.getStringOfStringArray(
			// process1.processInfo.pkgList).compareTo(Util.getStringOfStringArray(process0.processInfo.pkgList));
			int checkPkgResult = checkPkgSize(process0, process1);
			if(checkPkgResult != 0) {
				return checkPkgResult;
			}
			return mOrderAsc ? process0.processInfo.processName.trim().compareTo(process1.processInfo.processName.trim()) : process1.processInfo.processName.trim().compareTo(
					process0.processInfo.processName.trim());
		case ORDER_TYPE_MEMORY:
			return mOrderAsc ? process0.memoryInfo.getTotalPrivateDirty() - process1.memoryInfo.getTotalPrivateDirty() : process1.memoryInfo.getTotalPrivateDirty()
					- process0.memoryInfo.getTotalPrivateDirty();
		default:
			break;
		}
		return 0;
	}

	private int checkLocked(Process process0, Process process1) {

		if (mLockedTop) {
			if (process0.isLocked && !process1.isLocked) {
				return -1;
			} else if (!process0.isLocked && process1.isLocked) {
				return 1;
			}
		}
		return 0;
	}

	private int checkPkgSize(Process process0, Process process1) {
		int length0 = process0.processInfo.pkgList.length;
		int length1 = process1.processInfo.pkgList.length;

		if (length0 > length1) {
			return -1;
		} else if (length0 < length1) {
			return 1;
		}
		return 0;
	}
}
