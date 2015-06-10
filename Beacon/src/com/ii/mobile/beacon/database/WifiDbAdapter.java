//package com.ii.mobile.beacon.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//
//import com.ii.mobile.util.L;
//
//public class WifiDbAdapter extends AbstractDbAdapter {
//	private static WifiDbAdapter wifiDbAdapter;
//	public final static boolean WANT_SECURITY = true;
//	public final static boolean WANT_SECURITY_DEBUG = false;
//	
//	public static WifiDbAdapter getWifiDbAdapter() {
//		return wifiDbAdapter;
//	}
//
//	public WifiDbAdapter(Context context) {
//		super(context);
//		setTableName(TABLE_WIFI);
//		L.out("created: " + this);
//		WifiDbAdapter.setWifiDbAdapter(this);
//	}
//	
//	public long create(String sessionId, String name, int Strength, 
//			String throuput) {
//		ContentValues values = new ContentValues();
//		values.put(KEY_SESSIONID, sessionId);
//		values.put(KEY_NAME, name);
//		values.put(KEY_STRENGTH, Strength);
//		values.put(KEY_THROUGHPUT, throuput);
//		L.out("values: " + values);
//		if (WANT_SECURITY) {
////			out("before create json: " + json + " user: " + User.getUser());
////			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
////			values.put(StaticFlowColumns.JSON, json);
////			out("after create json: " + json);
////			if (WANT_SECURITY_DEBUG)
////				out("test : " + SecurityUtils.decryptAES(User.getUser().getPassword(), json));
//		}
//		long result = getDB().insert(getTableName(), null, values);
//		L.out("result: " + result);
//
////		showEvents(getTableName());
//		return result;
//	}
//
//	public static void setWifiDbAdapter(WifiDbAdapter wifiDbAdapter) {
//		WifiDbAdapter.wifiDbAdapter = wifiDbAdapter;
//	}
// }