/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.type.sdk.android.TypeSDKLogger;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class SystemUtils {

	public static final char SEPARATOR = 0x02;
	private static String TAG = "TypeSDK";
	private static final int NETWORK_TYPE_UNAVAILABLE = -1;
	private static final int NETWORK_TYPE_WIFI = -101;
	private static final int NETWORK_CLASS_WIFI = -101;
	private static final int NETWORK_CLASS_UNAVAILABLE = -1;
	/** Unknown network class. */
	private static final int NETWORK_CLASS_UNKNOWN = 0;
	/** Class of broadly defined "2G" networks. */
	private static final int NETWORK_CLASS_2_G = 1;
	/** Class of broadly defined "3G" networks. */
	private static final int NETWORK_CLASS_3_G = 2;
	/** Class of broadly defined "4G" networks. */
	private static final int NETWORK_CLASS_4_G = 3;

	// 适配低版本手机
	/** Network type is unknown */
	public static final int NETWORK_TYPE_UNKNOWN = 0;
	/** Current network is GPRS */
	public static final int NETWORK_TYPE_GPRS = 1;
	/** Current network is EDGE */
	public static final int NETWORK_TYPE_EDGE = 2;
	/** Current network is UMTS */
	public static final int NETWORK_TYPE_UMTS = 3;
	/** Current network is CDMA: Either IS95A or IS95B */
	public static final int NETWORK_TYPE_CDMA = 4;
	/** Current network is EVDO revision 0 */
	public static final int NETWORK_TYPE_EVDO_0 = 5;
	/** Current network is EVDO revision A */
	public static final int NETWORK_TYPE_EVDO_A = 6;
	/** Current network is 1xRTT */
	public static final int NETWORK_TYPE_1xRTT = 7;
	/** Current network is HSDPA */
	public static final int NETWORK_TYPE_HSDPA = 8;
	/** Current network is HSUPA */
	public static final int NETWORK_TYPE_HSUPA = 9;
	/** Current network is HSPA */
	public static final int NETWORK_TYPE_HSPA = 10;
	/** Current network is iDen */
	public static final int NETWORK_TYPE_IDEN = 11;
	/** Current network is EVDO revision B */
	public static final int NETWORK_TYPE_EVDO_B = 12;
	/** Current network is LTE */
	public static final int NETWORK_TYPE_LTE = 13;
	/** Current network is eHRPD */
	public static final int NETWORK_TYPE_EHRPD = 14;
	/** Current network is HSPA+ */
	public static final int NETWORK_TYPE_HSPAP = 15;

	/**
	 * 功能描述：返回当前应用版本名
	 */
	public static String getAppVersionName(Context context) {
		if (context == null) {
			Log.e(TAG, "context is null");
			return "";
		}
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return versionName;
	}

	/**
	 * 功能描述：获取手机型号
	 */
	public static String getModel() {
		return Build.MODEL == null ? "NAN" : Build.MODEL;
	}

	/**
	 * 功能描述：手机制造商
	 */
	public static String getProduct() {
		return Build.MANUFACTURER;
	}

	/**
	 * 功能描述：获取当前安卓设备唯一编号
	 */

	public static String getAndroidID(Activity activity) {
		return Settings.Secure.getString(activity.getContentResolver(),
				Settings.Secure.ANDROID_ID);
	}

	/**
	 * 功能描述：获取当前设备操作系统版本
	 */
	public static String getSystemVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * 功能描述：获取屏幕宽度分辨率
	 */
	@SuppressWarnings({ "unused", "deprecation" })
	public static int getScreenWidthSize(Context context) {
		if (context == null) {
			Log.e(TAG, "context is null");
			return 0;
		}
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int screenWidth = wm.getDefaultDisplay().getWidth();
		if (screenWidth != 0) {
			return screenWidth;
		}
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		if (null != dm) {
			return dm.widthPixels;
		}
		return 0;
	}

	/**
	 * 功能描述：获取屏幕宽度分辨率
	 */
	@SuppressWarnings({ "unused", "deprecation" })
	public static int getScreenHeightSize(Context context) {
		if (context == null) {
			Log.e(TAG, "context is null");
			return 0;
		}
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int screenHeight = wm.getDefaultDisplay().getHeight();
		if (screenHeight != 0) {
			return screenHeight;
		}
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		if (null != dm) {
			return dm.heightPixels;
		}
		return 0;
	}

	/**
	 * 功能描述：判断当前手机是否有ROOT权限
	 */
	public static boolean getRootAhth() {
		boolean bool = false;
		try {
			if ((!new File("/system/bin/su").exists())
					&& (!new File("/system/xbin/su").exists())) {
				bool = false;
			} else {
				bool = true;
			}
		} catch (Exception e) {
		}
		return bool;
	}

	/**
	 * 获取内存路径
	 */
	public static String getMemoryFilesPath(Context context) {
		if (context == null) {
			Log.e(TAG, "context is null");
			return "";
		}
		return context.getFilesDir().getAbsolutePath();
	}

	/**
	 * 功能描述：查询手机内非系统应用
	 */
	@SuppressWarnings("static-access")
	public static List<PackageInfo> getAllApps(Context context) {
		if (context == null) {
			Log.e(TAG, "context is null");
			return null;
		}
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			// 判断是否为非系统预装的应用程序
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				// customs applications
				apps.add(pak);
			}
		}
		return apps;
	}

	/**
	 * 功能描述：获得当前应用的包信息
	 */
	public static PackageInfo getPackageInfo(Context context) {
		if (context == null) {
			Log.e(TAG, "context is null");
			return null;
		}
		PackageManager pManager = context.getPackageManager();
		PackageInfo info;
		try {
			info = pManager.getPackageInfo(context.getPackageName(), 0);
			return info;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 功能描述：判断是否前台显示
	 */
	public static boolean isAppOnForeground(Context context) {
		if (context == null) {
			Log.e(TAG, "context is null");
			return false;
		}
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取cpu总使用率
	 */
	public static String getTotalCpuUsage() {
		float cpuRate = 0;
		try {
			float totalCpuTime1 = getTotalCpuTime();
			float idle1 = getIdleCpuTime();
			Thread.sleep(1000);
			float totalCpuTime2 = getTotalCpuTime();
			float idle2 = getIdleCpuTime();
			cpuRate = 100 * ((totalCpuTime2 - totalCpuTime1) - (idle2 - idle1))
					/ (totalCpuTime2 - totalCpuTime1);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		DecimalFormat df = new DecimalFormat("#.00");
		return df.format(cpuRate) + "%";
	}

	public static long getTotalCpuTime() {
		String[] cpuInfos = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		long totalCpu = Long.parseLong(cpuInfos[2])
				+ Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
				+ Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
				+ Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
		return totalCpu;
	}

	private static long getIdleCpuTime() {
		String[] cpuInfos = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		long IdleCpu = Long.parseLong(cpuInfos[4]);
		return IdleCpu;
	}

	/**
	 * 实时获取CPU当前频率（单位KHZ）
	 */
	public static int getCurCpuFreq() {
		int result = 0;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(
					"/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
			br = new BufferedReader(fr);
			String text = br.readLine();
			result = Integer.parseInt(text.trim());
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} finally {
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return result;
	}

	// 获取CPU型号
	public static String getCPUType() {
		return android.os.Build.CPU_ABI;
	}

	/**
	 * 获取总内存大小
	 */
	public static long getMemoryTotalMB(Context context) {
		return getTotalMemoryKB() / 1024L;
	}

	private static long getTotalMemoryKB() {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("\\s+");

			initial_memory = Long.valueOf(arrayOfString[1]).longValue();// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e) {
			TypeSDKLogger.e(e.toString());
		}
		return initial_memory;
	}

	/**
	 * 获取内存使用率
	 */
	public static long getMemoryUsageMB(Context context) {
		long total = getTotalMemoryKB();
		long avail = getAvailMemoryKB(context);
		return (total - avail) / 1024L;
	}

	/**
	 * 获取android当前可用内存大小
	 */
	private static long getAvailMemoryKB(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return (mi.availMem) / 1024L;
	}

	/*
	 * 获取Mac地址
	 */
	public static String getWifiMac(Context context) {
		String macAddress = "";
		try {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			String wifiMac = info.getMacAddress();
			if (!TextUtils.isEmpty(wifiMac)) {
				macAddress = wifiMac;
			} else {
				macAddress = "Get Mac Value Error";
			}
		} catch (Exception e) {
			TypeSDKLogger.e(e.toString());
		}
		return macAddress;
	}

	/*
	 * 获取IMEI
	 */
	public static String getIMEI(Context context) {
		String phoneIMEI = "";
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (!TextUtils.isEmpty(imei)) {
				phoneIMEI = imei;
			} else {
				phoneIMEI = "Get IMEI value Error";
			}
		} catch (Exception e) {
			TypeSDKLogger.e(e.toString());
		}
		return phoneIMEI;
	}

	/*
	 * 获取IMSI
	 */
	public static String getIMSI(Context context) {
		String phoneIMSI = "";
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imsi = tm.getSubscriberId();
			if (!TextUtils.isEmpty(imsi)) {
				phoneIMSI = imsi;
			} else {
				phoneIMSI = "Get IMSI value Error";
			}
		} catch (Exception e) {
			TypeSDKLogger.e(e.toString());
		}
		return phoneIMSI;
	}

	/*
	 * 获取SimSerialNumber
	 */
	public static String getPhoneSN(Context context) {
		String phoneSN = "";
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String sn = tm.getSimSerialNumber();
			if (!TextUtils.isEmpty(sn)) {
				phoneSN = sn;
			} else {
				phoneSN = "Get SimSerialNumber Error";
			}
		} catch (Exception e) {
			TypeSDKLogger.e(e.toString());
		}
		return phoneSN;
	}

	// 获取当前时区
	public static String getTimeZone() {
		TimeZone tz = TimeZone.getDefault();
		return createGmtOffsetString(true, true, tz.getRawOffset());
	}

	public static String createGmtOffsetString(boolean includeGmt,
			boolean includeMinuteSeparator, int offsetMillis) {
		int offsetMinutes = offsetMillis / 60000;
		char sign = '+';
		if (offsetMinutes < 0) {
			sign = '-';
			offsetMinutes = -offsetMinutes;
		}
		StringBuilder builder = new StringBuilder(9);
		if (includeGmt) {
			builder.append("GMT");
		}
		builder.append(sign);
		appendNumber(builder, 2, offsetMinutes / 60);
		if (includeMinuteSeparator) {
			builder.append(':');
		}
		appendNumber(builder, 2, offsetMinutes % 60);
		return builder.toString();
	}

	private static void appendNumber(StringBuilder builder, int count, int value) {
		String string = Integer.toString(value);
		for (int i = 0; i < count - string.length(); i++) {
			builder.append('0');
		}
		builder.append(string);
	}

	// 获取当前时间
	public static String getCurTime() {
		Calendar c = Calendar.getInstance();
		String strTime = "";
		strTime += c.get(Calendar.YEAR) + "_";
		strTime += (c.get(Calendar.MONTH) + 1) + "_";
		strTime += c.get(Calendar.DAY_OF_MONTH) + "_";
		strTime += c.get(Calendar.HOUR_OF_DAY) + "_";
		strTime += c.get(Calendar.MINUTE) + "_";
		strTime += c.get(Calendar.SECOND);
		return strTime;
	}

	// 获取运营商
	public static String getSimOperatorName(Activity activity) {
		TelephonyManager telManager = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = telManager.getSimOperator();
		String opName = "";
		if (operator != null) {
			if (operator.equals("46000") || operator.equals("46002")
					|| operator.equals("46007")) {
				// 中国移动
				opName = "中国移动";

			} else if (operator.equals("46001")) {
				// 中国联通
				opName = "中国联通";
			} else if (operator.equals("46003")) {
				// 中国电信
				opName = "中国电信";
			}

		}
		return opName;
	}

	// 获取网络类型
	public static String getNetworkType(Activity activity) {
		int networkClass = getNetworkClass(activity);
		String type = "未知";
		switch (networkClass) {
		case NETWORK_CLASS_UNAVAILABLE:
			type = "无";
			break;
		case NETWORK_CLASS_WIFI:
			type = "Wi-Fi";
			break;
		case NETWORK_CLASS_2_G:
			type = "2G";
			break;
		case NETWORK_CLASS_3_G:
			type = "3G";
			break;
		case NETWORK_CLASS_4_G:
			type = "4G";
			break;
		case NETWORK_CLASS_UNKNOWN:
			type = "未知";
			break;
		}
		return type;
	}

	private static int getNetworkClassByType(int networkType) {
		switch (networkType) {
		case NETWORK_TYPE_UNAVAILABLE:
			return NETWORK_CLASS_UNAVAILABLE;
		case NETWORK_TYPE_WIFI:
			return NETWORK_CLASS_WIFI;
		case NETWORK_TYPE_GPRS:
		case NETWORK_TYPE_EDGE:
		case NETWORK_TYPE_CDMA:
		case NETWORK_TYPE_1xRTT:
		case NETWORK_TYPE_IDEN:
			return NETWORK_CLASS_2_G;
		case NETWORK_TYPE_UMTS:
		case NETWORK_TYPE_EVDO_0:
		case NETWORK_TYPE_EVDO_A:
		case NETWORK_TYPE_HSDPA:
		case NETWORK_TYPE_HSUPA:
		case NETWORK_TYPE_HSPA:
		case NETWORK_TYPE_EVDO_B:
		case NETWORK_TYPE_EHRPD:
		case NETWORK_TYPE_HSPAP:
			return NETWORK_CLASS_3_G;
		case NETWORK_TYPE_LTE:
			return NETWORK_CLASS_4_G;
		default:
			return NETWORK_CLASS_UNKNOWN;
		}
	}

	private static int getNetworkClass(Activity activity) {
		int networkType = NETWORK_TYPE_UNKNOWN;
		try {
			final NetworkInfo network = ((ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (network != null && network.isAvailable()
					&& network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					networkType = NETWORK_TYPE_WIFI;
				} else if (type == ConnectivityManager.TYPE_MOBILE) {
					TelephonyManager telephonyManager = (TelephonyManager) activity
							.getSystemService(Context.TELEPHONY_SERVICE);
					networkType = telephonyManager.getNetworkType();
				}
			} else {
				networkType = NETWORK_TYPE_UNAVAILABLE;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getNetworkClassByType(networkType);

	}

	public static String getWifiSsid(Activity activity) {
		String ssid = "";
		try {
			final NetworkInfo network = ((ConnectivityManager) activity
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (network != null && network.isAvailable()
					&& network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					WifiManager wifiManager = (WifiManager) activity
							.getSystemService(Context.WIFI_SERVICE);

					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					if (wifiInfo != null) {
						ssid = wifiInfo.getSSID();
						if (ssid == null) {
							ssid = "";
						}
						ssid = ssid.replaceAll("\"", "");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ssid;
	}

	// 获取当前IP
	public static String getPhoneIp() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
			TypeSDKLogger.e(e.toString());
		}
		return "";
	}

	// 获取当前语言环境
	public static String getCurLanguage(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		return language;
	}

	// 获取当前音量 import android.media.AudioManager;
	public static String getVolume(Context _in_context) {
		AudioManager audioManager = (AudioManager) _in_context
				.getSystemService(Context.AUDIO_SERVICE);
		return "" + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);// 当前的媒体音量
	}

	// 获取当前应用所占内存
	public static String getRunningAppProcessInfo(Context _in_context) {
		String packageNames = null;
		try {
			PackageInfo info = _in_context.getPackageManager().getPackageInfo(
					_in_context.getPackageName(), 0);
			// 当前应用的版本名称
			String versionName = info.versionName;
			// 当前版本的版本号
			int versionCode = info.versionCode;
			// 当前版本的包名
			packageNames = info.packageName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		ActivityManager mActivityManager = (ActivityManager) _in_context
				.getSystemService(Context.ACTIVITY_SERVICE);

		// 获得系统里正在运行的所有进程
		List<RunningAppProcessInfo> runningAppProcessesList = mActivityManager
				.getRunningAppProcesses();

		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
			// 进程ID号
			int pid = runningAppProcessInfo.pid;
			// 用户ID
			int uid = runningAppProcessInfo.uid;
			// 进程名
			String processName = runningAppProcessInfo.processName;
			if (processName.equals(packageNames)) {
				// 占用的内存
				int[] pids = new int[] { pid };
				Debug.MemoryInfo[] memoryInfo = mActivityManager
						.getProcessMemoryInfo(pids);
				// int memorySize = memoryInfo[0].dalvikPrivateDirty;
				int memorySize = memoryInfo[0].dalvikPss
						+ memoryInfo[0].nativePss;
				int dalvikPss = memoryInfo[0].dalvikPss;
				int nativePss = memoryInfo[0].nativePss;

				Log.i("TypeDemo", "processName=" + processName + ",pid="
						+ pid + ",uid=" + uid + ",memorySize=" + memorySize
						+ "kb");
				Log.i("TypeDemo", "dalvikPss=" + dalvikPss + ",nativePss="
						+ nativePss);
				return "" + memorySize;
			}
		}
		return "";
	}

	// 获取应用占用CPU使用率
	public static String getAppCpuUsage() {
		float cpuRate = 0;
		try {
			float totalCpuTime1 = getTotalCpuTime();
			float idle1 = getAppCpuTime();
			Thread.sleep(1000);
			float totalCpuTime2 = getTotalCpuTime();
			float idle2 = getAppCpuTime();
			cpuRate = (100 * ((idle2 - idle1)) / (totalCpuTime2 - totalCpuTime1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(cpuRate) + "%";
	}

	// 获取应用占用的CPU时间
	private static long getAppCpuTime() {
		String[] cpuInfos = null;
		try {
			int pid = android.os.Process.myPid();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream("/proc/" + pid + "/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		long appCpuTime = Long.parseLong(cpuInfos[13])
				+ Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
				+ Long.parseLong(cpuInfos[16]);
		return appCpuTime;
	}
}
