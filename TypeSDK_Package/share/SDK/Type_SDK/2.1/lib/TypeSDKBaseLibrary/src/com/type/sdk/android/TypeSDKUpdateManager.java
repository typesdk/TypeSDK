package com.type.sdk.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.type.utils.ApkUtils;
import com.type.utils.PatchUtils;
import com.type.utils.SignUtils;
import com.type.utils.HttpUtil;
import com.type.utils.MD5Util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class TypeSDKUpdateManager {

	private Context mContext;
	
	private String platformName;
	private String appVersion;
	private String packageName;
	private String androidVersion;
	private int androidSDK;
	private String checkUrl;
	//提示语
	private String updateMsg = "发现最新游戏版本";
	
	//返回的安装包url
	private String apkUrl;
	//返回的补丁包url
	private String patchUrl;
	//当前要下载的包url,用来区别下载安装包和补丁包
	private String curUrl;
	//服务器上最新apk的MD5值
	private String serverApkMd5Value;
	//用来区分强更和非强更，1：强更 ，0：非强更
	private String updateFlag = "0";
	private Dialog noticeDialog;
	
	private Dialog downloadDialog;
	 /* 下载包安装路径 */
    private static final String savePath = Environment.getExternalStorageDirectory() + File.separator+"temp"+File.separator;
    
    private static final String saveFileName = savePath + "GameUpdateRelease.apk";

    private static final String patchedFilePathName = savePath+"PatchedUpdateRelease.apk";
    
    private static final String apkPatchPath = savePath+"patch.apk";
       
    /* 进度条与通知ui刷新的handler和msg常量 */
    //private ProgressBar mProgress;
    
    private TextView mMsg;
    
    private static final int DOWN_UPDATE = 1;
    
    private static final int DOWN_OVER = 2;
    
    private static final int UPDATE_YES = 1;
    
    private static final int UPDATE_NO = 0;
    
    private int progress;
    private int apkSize;
    
    private Thread downLoadThread;
    
    private Thread checkUpdateThread;
    
    private boolean interceptFlag = false;
    
    private ProgressDialog mProgressDialog;
    
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mMsg.setText("安装包"+apkSize+"MB,下载"+progress+"%");
				//mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				mMsg.setText("下载完成");
				if(!curUrl.isEmpty()){
					if(curUrl.equals(patchUrl)){
						TypeSDKLogger.i("curUrl: "+curUrl+" patchUrl:"+patchUrl);
						new PatchApkTask().execute();
					}else{
						installApk(mContext,saveFileName);
					}
				}
				
				break;
			default:
				break;
			}
    	};
    };
    
    private Handler checkHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		TypeSDKLogger.i("Check Handler msg="+msg.what);
    		switch (msg.what){
    		case UPDATE_YES:   	
    			showNoticeDialog(updateFlag,updateMsg);
    			break;
    		case UPDATE_NO:
    			break;
    		default:
    			break;
    		}
    	};
    };
    
	public TypeSDKUpdateManager(Context context,String pfName,String checkUrl) {
		this.mContext = context;
		this.androidSDK = android.os.Build.VERSION.SDK_INT;
		this.androidVersion = android.os.Build.VERSION.RELEASE;
		this.packageName = mContext.getPackageName();
		this.appVersion = this.getVersion(mContext);
		this.platformName= pfName;
		this.checkUrl= checkUrl;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setMessage("doing..");
		mProgressDialog.setCancelable(false);
		TypeSDKLogger.i("Update manager init:checkUrl="+this.checkUrl+";platform="+this.platformName+"appVersion="+this.appVersion);
	}
	
	//检测并下载接口Activity调用
	public void checkUpdateInfo(){
		checkUpdate();
	}
	
	//直接下载url接口Activity调用
	public void downloadApk(String downloadURL){
		if(downloadURL.startsWith("http")){
			this.apkUrl= downloadURL;
			this.showNoticeDialog(updateFlag,updateMsg);
		}
	}
	
	private void showNoticeDialog(String flag,String dialogMsg){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("游戏版本更新");
		builder.setMessage(dialogMsg);
		if(flag.equals("1")){
			builder.setCancelable(false);
			builder.setPositiveButton("下载", new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					showDownloadDialog(1);			
				}
			});
		}
		if(flag.equals("0")){
			builder.setCancelable(true);
			builder.setPositiveButton("下载", new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					showDownloadDialog(0);			
				}
			});
			builder.setNegativeButton("以后再说", new OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();				
				}
			});
		}
		noticeDialog = builder.create();
		noticeDialog.show();
	}
	private void showDownloadDialog(int flag){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
		mMsg = new TextView(mContext);
		mMsg.setGravity(Gravity.CENTER_HORIZONTAL);
		mMsg.setTextSize(18);
		builder.setView(mMsg);
		if(flag==0){
			builder.setCancelable(true);
			builder.setNegativeButton("取消", new OnClickListener() {	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					interceptFlag = true;
				}
			});
		}else{
			builder.setCancelable(false);
		}
		downloadDialog = builder.create();
		downloadDialog.show();
		
		downloadApk();
	}
	
	private Runnable mcheckUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				String response;
				String checkUpdate = checkUrl + platformName + "/CheckVersion/?version=" + appVersion 
									+ "&packageName=" + packageName
									+ "&AndroidVersion=" + androidVersion
									+ "&AndroidSDK=" + androidSDK;
				TypeSDKLogger.i("Check update URL: url="+checkUpdate);
				response = HttpUtil.mHttpGet(checkUpdate);
				TypeSDKLogger.i("Apk Url="+response);
				TypeSDKData.BaseData updateData = new TypeSDKData.BaseData();
				updateData.StringToData(response);
				int updateCode = updateData.GetInt("code");
				if (updateCode == 1){
					apkUrl = updateData.GetData("ClientURL");
					patchUrl = updateData.GetData("updateURL");
					serverApkMd5Value = updateData.GetData("ClientMD5");
					updateFlag = updateData.GetData("force");
					curUrl = (patchUrl.isEmpty()||patchUrl.equals(""))?apkUrl:patchUrl;
					checkHandler.sendEmptyMessage(UPDATE_YES);
				}else
				{
					checkHandler.sendEmptyMessage(UPDATE_NO);
				}
			}catch (Exception e) {
				TypeSDKLogger.e("Check URL Exception:"+e.toString());
			}
		}
	};
	
	private Runnable mdownApkRunnable = new Runnable() {	
		@Override
		public void run() {
			try {
				URL url = new URL(curUrl);
			
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				apkSize = (int)length/1024/1024;
				InputStream is = conn.getInputStream();
				File file = new File(savePath);
				if(!file.exists()){
					file.mkdir();
				}
				String apkFile = (!curUrl.isEmpty()&&(curUrl==patchUrl))?apkPatchPath:saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{
		    		int numread = is.read(buf);
		    		count += numread;
		    	    progress =(int)(((float)count / length) * 100);
		    	    //更新进度
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){	
		    			//下载完成通知安装
		    			mHandler.sendEmptyMessage(DOWN_OVER);
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//点击取消就停止下载.
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
			
		}
	};
	
	
	/**
	 * 检查是否有下载
	 */
	
	private void checkUpdate(){
		checkUpdateThread = new Thread(mcheckUpdateRunnable);
		checkUpdateThread.start();
	}
	
	 /**
     * 下载apk
     * @param url
     */
	
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	 /**
     * 安装apk
     * @param url
     */
	public static void installApk(Context context, String filePathName){
		TypeAccessibility.isFromNeeded = true;
		File apkfile = new File(filePathName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        context.startActivity(i);
	
	}

	public String getVersion(Context context)//获取版本号
	{
		try {
			PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "unknow";
		}
	}
	public class PatchApkTask extends AsyncTask<String, Void, Integer> {
		// 合成成功
		private static final int PATCH_SUCCESS = 1;
		// 合成的APK签名和已安装的签名不一致
		private static final int PATCH_FAIL_SIGN = -1;
		// 合成失败
		private static final int PATCH_FAIL_ERROR = -2;
		// 获取源文件失败
		private static final int PATCH_FAIL_GET_SOURCE = -3;
		// 更新开始时间和结束时间
		private long mBeginTime, mEndTime;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
			mBeginTime = System.currentTimeMillis();
		}

		@Override
		protected Integer doInBackground(String... arg0) {

			// TODO Auto-generated method stub
			String oldApkSource = ApkUtils.getSourceApkPath(mContext,
					packageName);

			if (!TextUtils.isEmpty(oldApkSource)) {

				TypeSDKLogger.i( "patch begin");
				int patchResult = PatchUtils.patch(oldApkSource,
						patchedFilePathName, apkPatchPath);

				TypeSDKLogger.i("patch finished");
				if (patchResult == 0) {

					String signatureNew = SignUtils
							.getUnInstalledApkSignature(patchedFilePathName);

					String signatureSource = SignUtils.getInstalledApkSignature(
							mContext, packageName);

					if (!TextUtils.isEmpty(signatureNew)
							&& !TextUtils.isEmpty(signatureSource)
							&& signatureNew.equals(signatureSource)) {
						return PATCH_SUCCESS;
					} else {
						return PATCH_FAIL_SIGN;
					}
				} else {
					return PATCH_FAIL_ERROR;
				}
			} else {
				return PATCH_FAIL_GET_SOURCE;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			mEndTime = System.currentTimeMillis();
			showShortToast("升级耗时: " + (mEndTime - mBeginTime) + "ms");
			switch (result) {
			case PATCH_SUCCESS:
				showShortToast("新apk已合成成功：" + patchedFilePathName);
				try {
					String patchedApkMd5Value = MD5Util.getFileMD5String(new File(patchedFilePathName));
					if(patchedApkMd5Value.equals(serverApkMd5Value)){
						installApk(mContext,patchedFilePathName);
					}else{
						TypeSDKLogger.w("省流量更新失败，请整包更新");
						curUrl = apkUrl;
						updateMsg = "省流量更新失败，请整包更新";
						showNoticeDialog(updateFlag,updateMsg);						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				installApk(mContext,patchedFilePathName);
				break;
			case PATCH_FAIL_SIGN:
				showShortToast("新apk已合成失败，签名不一致");
				curUrl = apkUrl;
				updateMsg = "签名不一致，请整包更新";
				showNoticeDialog(updateFlag,updateMsg);
				break;
			case PATCH_FAIL_ERROR: 
				showShortToast("新apk已合成失败");
				curUrl = apkUrl;
				updateMsg = "新apk已合成失败，请整包更新";
				showNoticeDialog(updateFlag,updateMsg);
				break;
			
			case PATCH_FAIL_GET_SOURCE: 
				showShortToast("无法获取packageName为" + packageName
						+ "的源apk文件，只能整包更新了！");
				curUrl = apkUrl;
				updateMsg = "无法获取packageName为" + packageName
						+ "的源apk文件，只能整包更新了！"+"请整包更新";
				showNoticeDialog(updateFlag,updateMsg);
				break;		
			}
		}
	}
	private void showShortToast(final String text) {

		Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
	}		
}

