package com.type.sdk.android;

import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class TypeAccessibility extends AccessibilityService {
	public static boolean isFromNeeded = false;
	@Override
	protected boolean onKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
	}
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		processinstallApplication(event);
	}
	@SuppressLint("InlinedApi")
	private void processinstallApplication(AccessibilityEvent event) {

		if (event.getSource() != null) {
//			TypeSDKLogger.i(event.getPackageName().toString());
			if (event.getPackageName().toString().equals("com.android.packageinstaller")&&isFromNeeded) {				
				TypeSDKLogger.i("nodeinfo:"+event.getSource().toString());
				List<AccessibilityNodeInfo> sure_nodes = event.getSource()
						.findAccessibilityNodeInfosByText("确定");
				TypeSDKLogger.i("1:"+(sure_nodes != null)+"2:"+!sure_nodes.isEmpty());
				if (sure_nodes != null && !sure_nodes.isEmpty()) {
					TypeSDKLogger.i("确定");
					AccessibilityNodeInfo node;
					for (int i = 0; i < sure_nodes.size(); i++) {
						node = sure_nodes.get(i);
						if (node.getClassName().equals("android.widget.Button")
								&& node.isEnabled()) {
							TypeSDKLogger.i("node.isEnabled()");
							node.performAction(AccessibilityNodeInfo.ACTION_CLICK);							
						}
					}
				}
				List<AccessibilityNodeInfo> unintall_nodes = event.getSource()
						.findAccessibilityNodeInfosByText("安装");
				TypeSDKLogger.i("1:"+(unintall_nodes != null)+"2:"+!unintall_nodes.isEmpty());
				if (unintall_nodes != null && !unintall_nodes.isEmpty()) {
					TypeSDKLogger.i("安装");
					AccessibilityNodeInfo node;
					for (int i = 0; i < unintall_nodes.size(); i++) {
						node = unintall_nodes.get(i);
						if (node.getClassName().equals("android.widget.Button")
								&& node.isEnabled()) {
							TypeSDKLogger.i("node.isEnabled()");
							node.performAction(AccessibilityNodeInfo.ACTION_CLICK);							
						}
					}
				}

				List<AccessibilityNodeInfo> next_nodes = event.getSource()
						.findAccessibilityNodeInfosByText("下一步");
				TypeSDKLogger.i("1:"+(next_nodes != null)+"2:"+!next_nodes.isEmpty());
				if (next_nodes != null && !next_nodes.isEmpty()) {
					TypeSDKLogger.i("下一步");
					AccessibilityNodeInfo node;
					for (int i = 0; i < next_nodes.size(); i++) {
						node = next_nodes.get(i);
						if (node.getClassName().equals("android.widget.Button")
								&& node.isEnabled()) {
							node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
						}
					}
				}

				List<AccessibilityNodeInfo> ok_nodes = event.getSource()
						.findAccessibilityNodeInfosByText("打开");
				TypeSDKLogger.i("1:"+(ok_nodes != null)+"2:"+!ok_nodes.isEmpty());
				if (ok_nodes != null && !ok_nodes.isEmpty()) {
					TypeSDKLogger.i("打开");
					AccessibilityNodeInfo node;
					for (int i = 0; i < ok_nodes.size(); i++) {
						node = ok_nodes.get(i);
						if (node.getClassName().equals("android.widget.Button")
								&& node.isEnabled()) {
							node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
							isFromNeeded = false;
						}
					}
				}

			}
		}
	}	
}
