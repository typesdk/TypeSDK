package com.type.sdk.android.youlong;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

import com.yx19196.base.Constant;
import com.yx19196.utils.YLGameSDK;

public class TypeSDKSplash extends Activity {

	private ImageView mSplashItem_iv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 游龙sdk 初始化操作
		YLGameSDK.initYLsdk(this);

		setContentView(getResources().getIdentifier("yx_splash", "layout", getPackageName()));
		Display mDisplay = getWindowManager().getDefaultDisplay();
		Constant.SCREEN_WIDTH = mDisplay.getWidth();
		Constant.SCREEN_HEIGHT = mDisplay.getHeight();

		mSplashItem_iv = (ImageView) findViewById(getResources().getIdentifier("splash_loading_item", "id", getPackageName()));
		
		mSplashItem_iv.setBackgroundResource(getResources().getIdentifier("splash_loading_horizonal", "anim", getPackageName()));
		final AnimationDrawable drawable = (AnimationDrawable) mSplashItem_iv.getBackground();
		drawable.start();
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run()
			{
				drawable.stop();
				Intent intent = new Intent("TypeMAIN");
				// 闪屏后所跳转的activity
//			
				startActivity(intent);
				TypeSDKSplash.this.finish();
			}
		};
		timer.schedule(task, 3000);
		YLGameSDK.bindYxFloat(this); // 调用浮窗
	}

}
