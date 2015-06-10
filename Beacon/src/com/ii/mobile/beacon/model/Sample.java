package com.ii.mobile.beacon.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.util.L;

public class Sample {
	@SerializedName("name")
	public String name;
	@SerializedName("location")
	public String location;
	@SerializedName("bssid")
	public String bssid;
	@SerializedName("strength")
	public int strength = -1;
	@SerializedName("type")
	public int type = -1;
	@SerializedName("MAC_address")
	String macAddress;
	@SerializedName("frequency")
	public String frequency;
	@SerializedName("samples")
	public int samples = 0;
	@SerializedName("dateStamp")
	public long dateStamp = -1;
	@SerializedName("speed")
	public float speed = -1;
	@SerializedName("log")
	public String log = "Total Samples : " + samples;
	@SerializedName("event")
	public String event = "";
	@SerializedName("failed")
	public boolean failed = false;

	// @SerializedName("adHoc")
	// public boolean adHoc = false;
	public boolean testing = false;

	public Sample(String location) {
		this.location = location;
		dateStamp = new GregorianCalendar().getTimeInMillis();
	}

	public Sample() {
	}

	@Override
	public String toString() {
		return "\n name: " + name
				+ "\n location: " + location
				+ "\n bssid: " + bssid
				+ "\n strength: " + strength
				+ "\n macAddress: " + macAddress
				+ "\n frequency: " + frequency
				+ "\n samples: " + samples
				+ "\n speed: " + speed
				+ "\n event: " + event
				+ "\n type: " + type
				+ "\n failed: " + failed;
	}

	public String toStringShort() {
		return "\n name: " + name
				+ "\n location: " + location
				+ "\n log: " + log
				+ "\n bssid: " + bssid
				+ "\n strength: " + strength
				+ "\n macAddress: " + macAddress
				+ "\n frequency: " + frequency
				+ "\n samples: " + samples
				+ "\n speed: " + speed
				+ "\n event: " + event
				+ "\n type: " + type
				+ "\n failed: " + failed;
	}

	@SuppressLint("NewApi")
	public void getData(Activity activity) {

		ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean is3G = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		boolean isWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		L.out("isWifi: " + isWifi + " is3G: " + is3G);
		if (!isWifi && is3G) {
			TelephonyManager telephonyManager =
					(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
			String strName = telephonyManager.getNetworkOperatorName();
			L.out("info.strName(): " + strName);
			if (strName == null || strName.trim().equalsIgnoreCase(""))
				strName = "******";
			name = strName;
			strength = 0;

			failed = false;
			samples += 1;
			// speed = ((int) (Math.random() * 100));
			// adHoc = ((Math.random() < .1d) ? true : false);
			// if (adHoc)
			// location = "_" + location;
			type = 0;
			return;
		}
		ScanResult scanResult = getSelectedScanResult(activity);
		L.out("scanResult: " + scanResult);
		if (scanResult != null) {
			WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
			ArrayList<ScanResult> scanResults = (ArrayList<ScanResult>) wifiManager.getScanResults();
			name = scanResult.SSID.trim();
			// for (ScanResult scan: scanResults) {
			// if
			// (scan.BSSID.equals(wifiManager.getConnectionInfo().getBSSID())) {
			// }
			strength = scanResult.level + 100;
			type = 1;
		}
		if (true)
			return;
		NetworkInfo info = getSelectedResult(activity);
		L.out("info: " + info);
		if (scanResult == null) {
			if (info == null)
				failed = true;
			else
			{
				L.out("info.getType(): " + info.getType() + " " + ConnectivityManager.TYPE_MOBILE);
				if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				{
					TelephonyManager telephonyManager =
							(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
					List<CellInfo> listCellinfo = telephonyManager.getAllCellInfo();
					L.out("listCellinfo: " + listCellinfo);
					if (null != listCellinfo)
						for (CellInfo cellInfo : listCellinfo)
						{
							CellInfoGsm cellinfogsm = (CellInfoGsm) cellInfo;
							CellSignalStrengthGsm cellSignalStrengthGsm =
									cellinfogsm.getCellSignalStrength();
							cellSignalStrengthGsm.getLevel();
							strength = cellSignalStrengthGsm.getLevel() * 25;
							String temp_name = info.getExtraInfo();
							if (temp_name.trim() == null || temp_name.trim().equals(""))
								name = "*****";

							name = temp_name;
							failed = false;
							samples += 1;
							// speed = ((int) (Math.random() * 100));
							// adHoc = ((Math.random() < .1d) ? true : false);
							// if (adHoc)
							// location = "_" + location;
							type = ((int) (Math.random() * 4));
						}
				}
			}
			return;
		}
	}

	private ScanResult getSelectedScanResult(Activity activity) {
		WifiManager wifiManager;

		wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		String state = wifiManager.getConnectionInfo().getBSSID();
		if (state == null) {
			MyToast.show("No WiFi connection!");
			return null;
		}
		ArrayList<ScanResult> scanResults = (ArrayList<ScanResult>) wifiManager.getScanResults();
		for (ScanResult scanResult : scanResults) {
			String temp = scanResult.BSSID;
			if (state.equalsIgnoreCase(temp))
				return scanResult;
		}
		return null;
	}

	private NetworkInfo getSelectedResult(Activity activity) {
		ConnectivityManager connectivityManager;

		connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
		if (!activeInfo.isConnected() && !activeInfo.isConnectedOrConnecting())
		{
			MyToast.show("No Cellular of Wi-Fi connection!");
			return null;
		}

		NetworkInfo infos[] = connectivityManager.getAllNetworkInfo();
		for (NetworkInfo info : infos)
		{
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
			{
				if (info == activeInfo)
					return info;
			}
		}
		return null;
	}

	public void random(Activity activity) {
		WifiManager wifiManager;
		wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		ArrayList<ScanResult> list = (ArrayList<ScanResult>) wifiManager.getScanResults();
		if (null != list && list.size() > 0)
		{
			ScanResult scanResult = list.get((int) (list.size() * Math.random()));
			name = scanResult.SSID;
			strength = scanResult.level;
			bssid = scanResult.BSSID;
			failed = ((Math.random() < .1d) ? true : false);
			samples = ((int) (Math.random() * 4));
			speed = ((int) (Math.random() * 100));
			// adHoc = ((Math.random() < .1d) ? true : false);
			// if (adHoc)
			// location = "_" + location;
			type = ((int) (Math.random() * 4));
		}
	}
}