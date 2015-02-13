package com.synature.util;

import android.os.Environment;
import android.os.StatFs;

public class MyStatFs {

	/**
	 * @return available disk space in megabyte
	 */
	public static long getAvailableSpace(){
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		return (long)stat.getAvailableBlocks() * (long)stat.getBlockSize() / (1024 * 1024);
	}
}
