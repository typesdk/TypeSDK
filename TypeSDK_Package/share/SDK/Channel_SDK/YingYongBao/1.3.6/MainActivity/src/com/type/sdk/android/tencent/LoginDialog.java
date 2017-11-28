package com.type.sdk.android.tencent;

import com.tencent.ysdk.api.YSDKApi;
import com.tencent.ysdk.framework.common.ePlatform;
import com.type.sdk.android.TypeSDKLogger;
import com.type.sdk.android.TypeSDKTool;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class LoginDialog extends Dialog{
	private Context context;
	private ImageView WXButton,QQButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 setContentView(GetResId.getId(context,"layout","relativeloginlayout"));  
	     
	        setCanceledOnTouchOutside(false);  
	        setCancelable(false);
	        initView();
	     
	        initEvent();
	        TypeSDKLogger.e("LoginDialog onCreate");
	}
	public LoginDialog(Context context) {
		super(context,GetResId.getId(context,"style","MyDialog"));
		this.context=context;
	}
	
	private void initView(){
		WXButton=(ImageView) findViewById(GetResId.getId(context,"id","WXButton"));
		QQButton=(ImageView) findViewById(GetResId.getId(context,"id","QQButton"));
	}
	
	private void initEvent(){
		
		QQButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TypeSDKBonjour.Instance().tencentLogin(1);
				hide();
			}
		});
		WXButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isInstall = YSDKApi.isPlatformInstalled(ePlatform.WX);
				if(isInstall){
					TypeSDKBonjour.Instance().tencentLogin(2);
					hide();
				}else{
					TypeSDKTool.showDialog("微信未安装，请安装后再试", context);
				}
			}
		});
		
	}
	
}
