package com.zgy.debugtool.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.util.Log;

public class PhoneUtil {
	/**
	 * 读取cpu使用率
	 * @param 
	 * @author zhuanggy
	 * @date 2014-1-15
	 */
	public static float readCPUUsage() {
		try {
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();
			String[] toks = load.split(" ");
			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			try {
				Thread.sleep(360);
			} catch (Exception e) {
			}
			reader.seek(0);
			load = reader.readLine();
			reader.close();
			toks = load.split(" ");
			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]) + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);
			return (int) (100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	/**
	 * 读取手机内存
	 * @param 
	 * @author zhuanggy
	 * @date 2014-1-15
	 */
	public static String readTotalMemory() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		return Util.sizeLongToString(initial_memory);
		// return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
	}
}
