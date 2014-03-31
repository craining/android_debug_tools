package com.zgy.debugtool.batteryinfo;

/**
 * 为方便电池信息列表展示而写的一个bean 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version 
 * @since
 */
public class BatteryItem {
	
	public String label;
	public String value;
	public int imgId;

	
	public BatteryItem(String label, String value, int imgId) {
		this.label = label;
		this.value = value;
		this.imgId = imgId;
	}
}
