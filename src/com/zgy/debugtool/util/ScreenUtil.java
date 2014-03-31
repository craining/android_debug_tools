package com.zgy.debugtool.util;

import android.content.Context;

/**
 * 用于屏幕分辨率适配
 * @Author zhuanggy
 * @Date:2013-11-29
 * @version 
 * @since
 */
public class ScreenUtil {
	//转换dip为px 
		public static int convertDIP2PX(Context context, int dip) { 
		    float scale = context.getResources().getDisplayMetrics().density; 
		    return (int)(dip*scale + 0.5f*(dip>=0?1:-1)); 
		} 
		 
		//转换px为dip 
		public static int convertPX2DIP(Context context, int px) { 
		    float scale = context.getResources().getDisplayMetrics().density; 
		    return (int)(px/scale + 0.5f*(px>=0?1:-1)); 
		}
}
