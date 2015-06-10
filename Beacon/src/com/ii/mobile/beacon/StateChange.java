package com.ii.mobile.beacon;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.ii.mobile.beacon.StrengthReciever.CONNECTION;
import com.ii.mobile.beacon.fragments.SampleFragment;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.util.L;

public class StateChange {

	public static final String NO_NETWORK = "No Network";
	public static final String WIFI_TO_CELL = "Wifi to Cell";
	public static final String CELL_TO_WIFI = "Cell to Wifi";
	public static final String COMPLETE = "Complete";
	public static final String WIFI_TO_WIFI = "Wifi to Wifi";
	public static final String NONE_TO_CELL = "None to Cell";
	public static final String NONE_TO_WIFI = "None to Wifi";
	public static final String WEAK_SIGNAL = "Weak Signal";
	public static final String STRONG_SIGNAL = "Strong Signal";
	public static final String START_TO_CELL = "Start to Cell";
	public static final String START_TO_WIFI = "Start to Wifi";
	public static final String AD_HOC = "Ad Hoc";
	public static final String AUTOMATIC = "Automatic";
	public static final String START_TO_ADHOC = "Start to Ad Hoc";
	public static final String NORMAL_SIGNAL = "Normal Signal";

	public static boolean begin = true;
	public static boolean lastBegin = true;
	public static boolean lastIsWifi = false;
	public static boolean lastIsCell = false;
	public static boolean lastWeakSignal = false;
	public static boolean lastStrongSignal = false;
	public static String lastWifiName = "";

	public static String[] getDialog() {
		String[] temp = new String[15];
		temp[0] = NO_NETWORK;
		temp[1] = WIFI_TO_CELL;
		temp[2] = CELL_TO_WIFI;
		temp[3] = COMPLETE;
		temp[4] = WIFI_TO_WIFI;
		temp[5] = NONE_TO_CELL;
		temp[6] = NONE_TO_WIFI;
		temp[7] = WEAK_SIGNAL;
		temp[8] = STRONG_SIGNAL;
		temp[9] = START_TO_CELL;
		temp[10] = START_TO_WIFI;
		temp[11] = AD_HOC;
		temp[12] = AUTOMATIC;
		temp[13] = START_TO_ADHOC;
		temp[14] = NORMAL_SIGNAL;

		return temp;
	}

	public static void addStateChange(Context context, CONNECTION connection) {
		String message = "";
		if (connection == CONNECTION.LOW)
			message = WEAK_SIGNAL;
		else if (connection == CONNECTION.NORMAL)
			message = NORMAL_SIGNAL;
		else if (connection == CONNECTION.HIGH)
			message = STRONG_SIGNAL;
		SampleFragment.getSample().event = message;
		if (TransportActivity.transportActivity == null) {
			L.out("transportActivity not created yet");
		} else if(!TransportActivity.transportActivity.isFinishing()){
			if (Alert.dialog == null || !Alert.dialog.isShowing()) {
				new Alert().createDialog(SampleFragment.getSample().location, message, TransportActivity.transportActivity);
				lastBegin = false;
			}
		}
	}

	public static void addStateChange(Context context, Intent intent, boolean strengthChange) {
		boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
		boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
		// L.out("onReceive: " + noConnectivity + " " + reason + " " +
		// isFailover);
		// L.out("state Change: " + reason);
		ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isCell = mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		boolean isWifi = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

		lastBegin = begin;
		boolean signalChange = false;
		if (reason != null) {
			boolean weakSignal = ((reason.equals(WEAK_SIGNAL) ? true : false));
			boolean strongSignal = ((reason.equals(STRONG_SIGNAL) ? true : false));
			signalChange = ((strengthChange && (lastWeakSignal != weakSignal || lastStrongSignal != strongSignal)) ? true
					: false);
			if (signalChange)
				L.out("signalChange: " + signalChange);
		}

		if (signalChange || isWifi != lastIsWifi || isCell != lastIsCell || begin) {
			L.out("isWifi: " + isWifi + " isCell: " + isCell + " isFailover: " + isFailover);
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			String message = reason;
			L.out("isWifi: " + isWifi + " isCell: " + isCell);
			if (lastIsWifi && isCell && !begin)
				message = WIFI_TO_CELL;
			else if (lastIsCell && isWifi && !begin) {
				message = CELL_TO_WIFI;
				lastWifiName = wifiManager.getConnectionInfo().getBSSID();
			}
			else if (!isCell && !isWifi && !begin)
				message = NO_NETWORK;
			else if ((!lastIsCell && !lastIsWifi) && isWifi) {
				message = NONE_TO_WIFI;
				if (begin) {
					message = START_TO_WIFI;
					begin = false;
				}
			}
			else if ((!lastIsCell && !lastIsWifi) && isCell) {
				message = NONE_TO_CELL;
				if (begin) {
					message = START_TO_CELL;
					begin = false;
				}
			}
			else if (lastWifiName != null && !lastWifiName.equals(wifiManager.getConnectionInfo().getBSSID())
					&& isWifi && !begin) {
				message = WIFI_TO_WIFI;
			}

			// if (message != null) {
			// EventOutputFragment.receiveMessage("State Changed", message);
			// }
			SampleFragment.getSample().event = message;
			if (TransportActivity.transportActivity == null) {
				L.out("transportActivity not created yet");
			} else
				if(!TransportActivity.transportActivity.isFinishing()){
					// waitForInited();
					new Alert().createDialog(SampleFragment.getSample().location, message, TransportActivity.transportActivity);
					L.out("lastIsWifi: " + lastIsWifi + " lastIsCell: " + lastIsCell);
					lastBegin = false;
				}
		}
		lastIsWifi = isWifi;
		lastIsCell = isCell;
	}

	private static void waitForInited() {
		while (!TransportActivity.running) {
			L.out("waiting ...");
			L.sleep(1000);
		}
	}
}
