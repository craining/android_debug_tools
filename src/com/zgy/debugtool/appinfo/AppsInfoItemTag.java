package com.zgy.debugtool.appinfo;

import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;

/**
 * 为方便展示“程序信息”弹窗里的信息列表而写的一个bean，
 * 
 * @Author zhuanggy
 * @Date:2014-1-17
 * @version
 * @since
 */
public class AppsInfoItemTag {

	public String label;
	public String value;
	public SpannableStringBuilder ssb;

	// 展示标题部分需要的数据
	public String name;
	public Drawable icon;

	public AppsInfoItemTag(String label, String value) {
		this.label = label;
		this.value = value;
	}

	public AppsInfoItemTag(String label, SpannableStringBuilder ssb) {
		this.label = label;
		this.value = null;
		this.ssb = ssb;
	}
}
