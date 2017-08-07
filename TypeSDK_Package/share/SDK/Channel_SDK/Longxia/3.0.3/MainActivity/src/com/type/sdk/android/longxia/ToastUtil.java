package com.type.sdk.android.longxia;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastUtil {
	   private static String oldMsg;  
	    protected static Toast toast   = null;  
	    private static long oneTime=0;  
	    private static long twoTime=0;  
	      
	    public static void showToast(Context context, String s){      
	        if(toast==null){   
	            toast =Toast.makeText(context, s, Toast.LENGTH_SHORT);  
	            toast.show();  
	            oneTime=System.currentTimeMillis();  
	        }else{  
	            twoTime=System.currentTimeMillis();  
	            if(s.equals(oldMsg)){  
	                if(twoTime-oneTime>Toast.LENGTH_SHORT){  
	                    toast.show();  
	                }  
	            }else{  
	                oldMsg = s;  
	                toast.setText(s);  
	                toast.show();  
	            }         
	        }  
	        oneTime=twoTime;  
	    }  
	      
	    public static void show(Context context,String msg){
	    	showToast(context,msg);
	    	Log.w("ToastUtil", msg);
	    }
	    public static void showToast(Context context, int resId){     
	        showToast(context, context.getString(resId));  
	    }  
	    
	    public static void showToastInThread(final Activity context,final String msg){
			context.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
}
