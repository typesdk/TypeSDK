package com.type.sdk.android.tencent;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.type.sdk.android.TypeSDKLogger;
import com.type.utils.*;

import android.content.Context;
import android.os.AsyncTask;

public class PostHelper extends AsyncTask<Integer, Void, Integer> {

	Context context;
	String openId;

	public PostHelper(Context _in_context, String openId) {
		// TODO Auto-generated constructor stub
		this.context = _in_context;
		this.openId = openId;
	}

	@Override
	protected Integer doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		try {
			TypeSDKLogger.i("PostHelper doInBackground start:openId:" + openId);
			
			SharedPreferencesUtil sh = new SharedPreferencesUtil(context,
					"payData" + openId);
			
			Map<String, Object> map = sh.getAll("payData" + openId);
			for(String key : map.keySet()){
				final String data = sh.read(key);
				JSONObject js = new JSONObject(data);
				js.put("time", "0");
				TypeSDKLogger.i("post start js:" + js.toString());
				TypeSDKBonjour.Instance().start(js, key);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
}
