/**
 *@ TypeSDKClient
 *@ 2016-10
 *@ Copyright© 2016 www.typesdk.com. All rights reserved. 
 */

package com.type.sdk.android;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TypeSDKData {
	public static class BaseData {

		public Map<String, String> m_attMap;

		public BaseData() {
			if (null == m_attMap)
				m_attMap = new HashMap<String, String>();
		}

		public void SetData(String attName, String attValue) {
			//String value = m_attMap.get(attName);
			// if(null== value)
			// {
			m_attMap.put(attName, attValue);
			// }
			// else
			// {
			// value = attValue;
			// }
		}

		// 将数组结构的Json转换为数组
		public String[] GetStringArray(String AttName) {
			TypeSDKLogger.i(" get data :" + GetData(AttName));
			if (GetData(AttName) == "" || GetData(AttName).isEmpty()) {
				TypeSDKLogger.w("is null");
				return new String[0];
			}
			JSONArray jValue = null;
			try {
				jValue = new JSONArray(GetData(AttName));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String[] strs = new String[jValue.length()];
			for (int i = 0; i < jValue.length(); i++) {
				try {
					TypeSDKLogger.i("Value" + i + jValue.get(i));
					strs[i] = jValue.get(i).toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return strs;

		}

		public String GetData(String attName) {
			String value = m_attMap.get(attName);
			if (null == value) {
				m_attMap.put(attName, "");
				value = "";
			}
			return value;
		}

		public int GetInt(String attName) {
			String value = GetData(attName);
			if ("" != value)
				return Integer.parseInt(value);
			else
				return 0;
		}

		public float GetFloat(String AttName) {
			String value = GetData(AttName);
			if ("" != value)
				return Float.parseFloat(value);
			else
				return 0.0f;
		}

		public boolean GetBool(String attName) {
			int value = GetInt(attName);

			if (0 == value)
				return false;
			else
				return true;
		}

		public void CopyAtt(TypeSDKData.BaseData _in_data, String _in_att_name) {
			SetData(_in_att_name, _in_data.GetData(_in_att_name));
		}

		public void CopyAttByData(TypeSDKData.BaseData _in_data) {
			for (Entry<String, String> entry : _in_data.m_attMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				SetData(key, value);
			}
		}

		public String DataToString() {
			String outStr = "";

			JSONObject jsData = new JSONObject();

			for (Entry<String, String> entry : m_attMap.entrySet()) {

				String key = entry.getKey();

				String value = entry.getValue();

				try {
					jsData.put(key, value);
				} catch (JSONException expt) {

					throw new RuntimeException(expt);
				}
			}
			outStr += jsData.toString();
			return outStr;
		}

		public void StringToData(String _in_str) {
			m_attMap.clear();
			try {
				JSONObject attJS = new JSONObject(_in_str);
				Iterator<?> it = attJS.keys();
				while (it.hasNext()) {
					String attName = (String) it.next().toString();
					String attValue = attJS.getString(attName);
					SetData(attName, attValue);
				}

			} catch (JSONException ex) {
			}
		}
	}

	public static class PlatformData extends BaseData {

	}

	public static class UserInfoData extends BaseData {

	}

	public static class PayInfoData extends BaseData {

	}

	public static class LoginResultData extends BaseData {

	}

	public static class PayResultData extends BaseData {

	}

	public static class ShareData extends BaseData {

	}
	
	public static class ItemListData extends BaseData {

	}
}
