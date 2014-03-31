package com.zgy.debugtool.appinfo;

/**
 * 为方便控制操作菜单而写的一个bean，不同的进程会有不同的一系列操作
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class Operas {
	public static final int OPERA_ID_KILL = 1;
	public static final int OPERA_ID_SHOW_INFO = 2;
	public static final int OPERA_ID_ALERT = 3;
	public static final int OPERA_ID_ALERT_TOP = 4;
	public static final int OPERA_ID_START = 5;
	public static final int OPERA_ID_UNINSTALL = 6;

	public int id;
	public String name;
	public String summary;

	public Operas(int id, String name, String sum) {
		this.id = id;
		this.name = name;
		this.summary = sum;
	}
}
