/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */
package com.type.sdk.notification;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.IBinder;

import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.base.R;
import com.type.utils.GetResId;
import com.type.utils.SharedPreferencesUtil;

public class PushService extends Service {

	private Timer timer;
	public static boolean stop = false;
	public SharedPreferencesUtil util;
	public static String channelName = "";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("PushService onBind");
		return null;
	}

	@Override
	public void onDestroy() {
		TypeSDKLogger.d("PushService onDestroy");
		super.onDestroy();
		if (timer != null) {
			stopForeground(true);
			timer.cancel();
			timer = null;
		}
		Intent intent = new Intent(this, PushService.class);
		TypeSDKLogger.d("Try to restart PushService");
		intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		this.startService(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		TypeSDKLogger.d("push service start package name:" + getPackageName());

		if (null == timer) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					TypeSDKLogger.e("Push Service schedule start.");
					Date curTime = new Date(System.currentTimeMillis());
					SimpleDateFormat formatter1 = new SimpleDateFormat(
							"MM-dd HH:mm", Locale.getDefault());
					String formatCurTime1 = formatter1.format(curTime);
					SimpleDateFormat formatter2 = new SimpleDateFormat(
							"dd HH:mm", Locale.getDefault());
					String formatCurTime2 = formatter2.format(curTime);
					final Calendar c = Calendar.getInstance();
					c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
					String stringWeek = String.valueOf(c
							.get(Calendar.DAY_OF_WEEK)) + " ";
					SimpleDateFormat formatter3 = new SimpleDateFormat("HH:mm",
							Locale.getDefault());
					String formatCurTime3 = stringWeek + formatter3.format(curTime);
					SimpleDateFormat formatter4 = new SimpleDateFormat("HH:mm",
							Locale.getDefault());
					String formatCurTime4 = formatter4.format(curTime);

					String[] CurTimes = { formatCurTime1, formatCurTime2,
							formatCurTime3, formatCurTime4 };
					List<String> curTimeList = Arrays.asList(CurTimes);
					util = null;
					util = new SharedPreferencesUtil(PushService.this);

					if (util.read("id") != null) {
						String[] stringsId = util.read("id").split(";");
						for (int i = 0; i < stringsId.length; i++) {
							if (util.read(stringsId[i]) != null) {
								String[] strings = util.read(stringsId[i])
										.split(";");
								TypeSDKLogger.e(getPackageName());
								TypeSDKLogger.e(util.read(stringsId[i]));
								//if (true) {
								TypeSDKLogger.e("curTimeList:" + curTimeList.toString());
								TypeSDKLogger.e("util:" + util.read(stringsId[i]));
								TypeSDKLogger.e("bl:" + curTimeList.contains(strings[1]));
								if (curTimeList.contains(strings[1])) {
									if (Build.VERSION.SDK_INT >= 23) {
										notify_23(strings);
									} else {
										notify_14(strings);
									}
								}
							} else {
								TypeSDKLogger.i("util.read(stringsId[i])");
							}
						}
					}
				}
			}, (long) 1 * 1000, (long) 60 * 1000);
		}

		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}
	
	@TargetApi(23)
	private void notify_23(String[] notify_string) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent notificationIntent = getPackageManager()
				.getLaunchIntentForPackage(
						getPackageName());
		PendingIntent contentIntent = PendingIntent
				.getActivity(PushService.this, 0,
						notificationIntent, 0);
		Notification.Builder builder = new Notification.Builder(
				PushService.this);
		builder.setContentIntent(contentIntent);
		builder.setTicker(notify_string[3]); // 测试通知栏标题
		// intent.getStringExtra("tickerText")
		builder.setContentTitle(notify_string[3]); // 下拉通知栏标题
		// intent.getStringExtra("contentTitle")
		builder.setContentText(notify_string[4]); // 下拉通知啦内容
		// intent.getStringExtra("contentText")
		builder.setAutoCancel(true);
		builder.setDefaults(Notification.DEFAULT_ALL);
		try{
		PackageManager pm = getPackageManager();
		
		ApplicationInfo info = pm.getApplicationInfo(getPackageName(), 0);
		
		Bitmap bitmap = ((BitmapDrawable) pm.getApplicationIcon(info)).getBitmap();
		builder.setLargeIcon(Icon.createWithBitmap(bitmap));
		builder.setSmallIcon(Icon.createWithBitmap(bitmap));
		Notification notification = builder.build();
		mNotificationManager.notify(
				(int) System.currentTimeMillis(),
				notification);
		}catch(Exception e){
			TypeSDKLogger.e("Push error, msg=" + e.getMessage());
			e.printStackTrace();
		} 
	}
	

	@SuppressLint("NewApi")
	private void notify_14(String[] notify_string) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent notificationIntent = getPackageManager()
				.getLaunchIntentForPackage(
						getPackageName());
		PendingIntent contentIntent = PendingIntent
				.getActivity(PushService.this, 0,
						notificationIntent, 0);
		
		Notification.Builder builder = new Notification.Builder(
				PushService.this);
		builder.setContentIntent(contentIntent);
		builder.setTicker(notify_string[3]);
		builder.setContentTitle(notify_string[3]);
		builder.setContentText(notify_string[4]);
		builder.setSmallIcon(GetResId.getId(this, "drawable", "app_icon")); 
		builder.setAutoCancel(true);
		builder.setDefaults(Notification.DEFAULT_ALL);
		Notification notification = builder.build();
		mNotificationManager.notify(
				(int) System.currentTimeMillis(),
				notification);
	}
}
