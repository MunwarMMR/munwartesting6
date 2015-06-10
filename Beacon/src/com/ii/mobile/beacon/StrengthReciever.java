package com.ii.mobile.beacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.ii.mobile.beacon.fragments.EventOutputFragment;
import com.ii.mobile.beacon.fragments.WifiListFragment;
import com.ii.mobile.util.L;

public class StrengthReciever extends BroadcastReceiver {

	int currentStrength = 0;
	int oldStrength = 0;
	int first = 0;
	WifiManager manager;

	private final int connectionLow = 15;
	private final int ConnectionHigh = 40;

	public enum CONNECTION
	{
		LOW, NORMAL, HIGH;
	}

	private static CONNECTION connection = CONNECTION.NORMAL;

	@Override
	public void onReceive(Context context, Intent intent) {
		manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		int newRssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
		// L.out("newRssi: " + newRssi);
		// L.out("speed rated: " + wifiInfo.getLinkSpeed() + " " +
		// WifiInfo.LINK_SPEED_UNITS);
		//
		// L.out("getSSID name of wifi: " + String.valueOf(wifiInfo.getSSID()));
		// L.out("getBSSID hex of wifi" + String.valueOf(wifiInfo.getBSSID()));
		// L.out("getRssi speed : " + String.valueOf(wifiInfo.getRssi()));
		currentStrength = wifiInfo.getRssi() + 100;
		L.out("currentStrength: " + currentStrength);
		CONNECTION currentConnection = getConnection(wifiInfo.getRssi() + 100);
		if (currentConnection != connection) {
			StateChange.addStateChange(context, currentConnection);
			EventOutputFragment.receiveMessage("Signal Strength ", connection
					+ " to " + currentConnection);
			connection = currentConnection;
		}
		if (WifiListFragment.wifiListFragment != null) {
			L.out("update: " + currentStrength);
			WifiListFragment.wifiListFragment.updateNetworksList();
		}
	}

	private CONNECTION getConnection(int connectionSpeed) {
		if (connectionSpeed < connectionLow)
			return CONNECTION.LOW;
		if (connectionSpeed > ConnectionHigh)
			return CONNECTION.HIGH;
		return CONNECTION.NORMAL;
	}
}