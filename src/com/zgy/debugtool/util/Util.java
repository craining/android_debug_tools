package com.zgy.debugtool.util;

public class Util {
	/**
	 * 转换文件的大小，将文件的字节数转换为kb、mb、或gb
	 * 
	 * 本可以直接用api里的format里的方法，但由于字符长度不能过长，用此方法
	 * 
	 * @Description:
	 * @param size单位byte
	 * @return 保留1位小数
	 * @see:
	 * @since:
	 * @author: zhuanggy
	 * @date:2013-1-11
	 */
	public static String sizeLongToString(long size) {
		String a = "";
		if (size < 1024) {
			a = String.format("%dB", size);
		} else if (size / 1024 < 1024) {
			a = String.format("%.2fK", size / 1024.00);// 1024
		} else if (size / 1048576 < 1024) {
			a = String.format("%.2fM", size / 1048576.00);// 1048576
		} else {
			a = String.format("%.2fG", size / 1073740824.00);// 1073740824
		}
		return a;
	}

	public static String getStringOfStringArray(String[] array, boolean splitEnter) {
		String packageNames = "";
		if (array != null) {
			int length = array.length;
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					if (i != length - 1) {
						packageNames = packageNames + array[i].trim() +  (splitEnter ? "\r\n" : "; ");
					} else {
						packageNames = packageNames + array[i].trim();
					}
				}
			}
		}
		return packageNames;
	}
}
