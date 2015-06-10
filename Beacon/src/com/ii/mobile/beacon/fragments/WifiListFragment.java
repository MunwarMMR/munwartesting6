package com.ii.mobile.beacon.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.CellInfo;
//import android.telephony.CellInfoGsm;
//import android.telephony.CellSignalStrengthGsm;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.StateChange;
//import com.ii.mobile.beacon.database.WifiDbAdapter;
import com.ii.mobile.beacon.model.Network;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.util.L;

public class WifiListFragment extends Fragment implements NamedFragment, OnClickListener
{
	TableLayout lout;
	public static ArrayList<Network> networksList;
	public static ArrayList<String> networksNames = new ArrayList<String>();
	WifiManager wifiManager;
	ConnectivityManager connectivityManager;
	Handler timeHandler = new Handler();
	File file;
	WritableWorkbook workbook;
	WritableSheet sheet;
	WifiConfiguration wifiConfiguration;
	Network current;
	public static Network selected;
	public static Activity activity;

	public static WifiListFragment wifiListFragment;
	// TextView rl_sample_view;
	SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		wifiListFragment = this;
		return inflater.inflate(R.layout.beacon_wifi_list_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		activity = getActivity();
		prefs = activity.getSharedPreferences("session", Context.MODE_PRIVATE);
		lout = (TableLayout) activity.findViewById(R.id.lout_networks);
		wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		// rl_sample_view = ((TextView) activity.findViewById(R.id.tv_geo));
		prefs = activity.getSharedPreferences("session", Context.MODE_PRIVATE);
		// ((TextView)activity.findViewById(R.id.tv_geo)).setText("Geography : "+activity.getIntent().getExtras().get("geo"));
		updateNetworksList();
	}

	@Override
	public void onClick(View arg0) {

		int count = 0;

		switch (arg0.getId()) {

		case R.id.sampleButton:
			// Save the list to DB and go back to previous screen
			if (count == 0)
			{
				Toast.makeText(activity, "Sample is Saved", Toast.LENGTH_LONG).show();
				++count;
			}
			else
				Toast.makeText(activity, "Sample is Already Saved", Toast.LENGTH_LONG).show();
			// activity.finish();

			break;

		case R.id.deleteSampleButton:
			if (null != networksList && networksList.size() > 0)
				sendMail();
			else
				Toast.makeText(activity, "Nothing to Mail", Toast.LENGTH_LONG).show();
			break;

		default:

			break;
		}
	}

	private void sendMail()
	{
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "malli930@gmail.com" });

		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Beacon Excel");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check Excel");
		emailIntent.setType("application/excel");
		if (file.exists())
		{
			int id = 0;
			if (prefs.contains(getDate()))
				id = prefs.getInt(getDate(), 0);
			else
			{
				prefs.edit().putInt(getDate(), 0).commit();
				id = 1;
			}
			// WifiDbAdapter adapter = new WifiDbAdapter(activity);
			for (final Network network : networksList)
			{
				// adapter.create(getDate() + "/" + id, network.getName(),
				// network.getStrength(), network.throughput
				// + " %");
				++id;
			}
			prefs.edit().putInt(getDate(), (id + 1)).commit();

			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + file));
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		}
		else
		{
			L.out("@@@@@@@@@@@@@ ------  Email file does not exist!");
		}
	}

	public String getScanResultSecurity(ScanResult scanResult) {

		final String cap = scanResult.capabilities;
		final String[] securityModes = { "WEP", "PSK", "EAP" };

		for (int i = securityModes.length - 1; i >= 0; i--) {
			if (cap.contains(securityModes[i])) {
				return securityModes[i];
			}
		}

		return "OPEN";
	}

	@SuppressLint("NewApi")
	public void updateNetworksList()
	{
		if (connectivityManager == null) {
			L.out("ERROR ConnectivityManager is null!");
			return;
		}
		boolean is3G = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		boolean isWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		L.out("isWifi: " + isWifi + " is3G: " + is3G);
		// L.out("updateNetworksList");
		lout.removeViews(1, lout.getChildCount() - 1);
		networksNames.clear();
		wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		ArrayList<ScanResult> scanResult = (ArrayList<ScanResult>) wifiManager.getScanResults();
		NetworkInfo infos[] = connectivityManager.getAllNetworkInfo();
		if (null == infos && null == scanResult)
			L.out(" @@@@@ %%%%% $$$$ No Networks Available");
		else
		{
			networksList = new ArrayList<Network>();
			if (null != scanResult && scanResult.size() > 0)
			{
				try {
					// L.out(" @@@@@ %%%%% $$$$ Many Wifi Networks Available " +
					// scanResult.size());

					for (ScanResult scan : scanResult)
					{
						String strName = scan.SSID.trim();
						// L.out("strName: " + strName);
						if (strName == null || strName.trim().equalsIgnoreCase("")) {
							strName = "******";
						}
						if (!strName.equals("******") || !networksNames.contains(strName))
						{
							Network network = new Network();
							networksNames.add(strName);
							network.setName(strName);
							network.setStrength(scan.level + 100);
							L.out("(scan.level: " + (scan.level + 100));
							network.setBssid(scan.BSSID);
							network.setFrequency(scan.frequency);
							int throughput = scan.level + 100;
							network.setThroughput(throughput);
							network.setType(0);

							String securityMode = getScanResultSecurity(scan);

							if (securityMode.equalsIgnoreCase("OPEN")) {
								network.setHasPassword(false);
							} else if (securityMode.equalsIgnoreCase("WEP")) {
								network.setHasPassword(true);
							}
							networksList.add(network);
						}
					}
				} catch (Exception e)
				{
					L.out(" From Catch : " + e.toString());
				}
			}

			TelephonyManager telephonyManager2 =
					(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
			String operatorName = telephonyManager2.getNetworkOperatorName();
			// L.out("operatorName: " + operatorName);
			if (null != infos && infos.length > 0)
			{
				for (NetworkInfo info : infos)
				{
					String netWorkInfo = getNetworkName(info.getType());
					// L.out("info.getType(): " + info.getType() + " "
					// + netWorkInfo);
					if (info.getType() == ConnectivityManager.TYPE_MOBILE)
					{
						// L.out("info.TYPE_MOBILE(): ");
						TelephonyManager telephonyManager =
								(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
						// List<CellInfo> listCellinfo =
						// telephonyManager.getAllCellInfo();
						List<CellInfo> listCellinfo = null;
						// L.out("info.listCellinfo(): " + listCellinfo);

						if (listCellinfo != null)
							for (CellInfo cellInfo : listCellinfo)
							{
								// CellInfoGsm cellinfogsm = (CellInfoGsm)
								// cellInfo;
								// CellSignalStrengthGsm cellSignalStrengthGsm =
								// cellinfogsm.getCellSignalStrength();
								// cellSignalStrengthGsm.getLevel();
								// cellSignalStrengthGsm.getDbm();
								String strName = info.getExtraInfo();
								// L.out("info.strName(): " + strName);
								if (strName == null || strName.trim().equalsIgnoreCase(""))
									strName = "******";
								if (!strName.equals("******") || !networksNames.contains(strName))
								{
									networksNames.add(strName);
									Network network = new Network();
									String name = info.getExtraInfo();
									network.setName(name);
									// network.setStrength(cellSignalStrengthGsm.getDbm());
									network.setStrength(10);
									network.setType(1);
									network.setHasPassword(false);
									networksList.add(network);
								}
							}
					} else {
						String strName = telephonyManager2.getNetworkOperatorName();
						// L.out("info.strName(): " + strName);
						if (strName == null || strName.trim().equalsIgnoreCase("")) {
							strName = "******";
							networksNames.add(strName);
						}
						if (!networksNames.contains(strName))
						{
							networksNames.add(strName);
							Network network = new Network();
							// String name = info.getExtraInfo();
							network.setName(strName);
							network.setStrength(telephonyManager2.getNetworkType());
							network.setType(1);
							network.setHasPassword(false);
							networksList.add(network);
						}
					}
				}
			}
			else
				System.out.println(" @@@@@ %%%%% $$$$ No Wifi Networks Available");
		}

		int i = 1;
		for (final Network network : networksList)
		{
			try {
				TableRow row = new TableRow(activity);
				row.setId(i - 1);

				current = network;
				if (wifiManager.getConnectionInfo() != null
						&& wifiManager.getConnectionInfo().getBSSID() != null)
					if (wifiManager.getConnectionInfo().getBSSID().equalsIgnoreCase(
							current.getBssid())) {
						row.setBackgroundColor(Color.GRAY);
						network.setStrength(getStrength());
					}
				if (!isWifi && is3G && network.getType() == 1)
					row.setBackgroundColor(Color.GRAY);
				row.addView(getTextView("" + i));
				row.addView(getText(" " + network.getName() + " "));
				row.addView(getTextView(" " + network.getStrength() + " "));
				// row.addView(getTextView(" " + network.getThroughput() +
				// "%"));
				if (network.getType() == 0)
					row.addView(getText("WiFi"));
				else
					row.addView(getText("Cell"));
				if (network.getType() == 0)
					row.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							connectToWifi(network);
							int id = v.getId();
							selected = networksList.get(id);
							updateNetworksList();
						}
					});
				lout.addView(row);
				++i;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (StateChange.begin)
			StateChange.addStateChange(getActivity(), getActivity().getIntent(), false);
	}

	private int getStrength() {
		WifiManager manager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		return wifiInfo.getRssi() + 100;
	}

	private void connectToWifi(Network network)
	{
		int res = -1;
		wifiConfiguration = new WifiConfiguration();
		wifiConfiguration.SSID = "\"" + network.getBssid() + "\"";
		if (network.hasPassword()) {
			// wifiConfiguration.wepKeys[0] = "\"" + networkPass + "\"";
			wifiConfiguration.wepTxKeyIndex = 0;
			wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			res = wifiManager.addNetwork(wifiConfiguration);
			System.out.println("%%%%%% Password");
		} else {

			wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			res = wifiManager.addNetwork(wifiConfiguration);
			System.out.println("%%%%% No Password");
		}

		wifiManager.enableNetwork(res, true);
		wifiManager.setWifiEnabled(true);
	}

	private TextView getTextView(String text)
	{
		TextView tv = new TextView(activity);
		tv.setTextSize(18);
		tv.setText(text);
		tv.setTextColor(Color.BLACK);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);

		return tv;
	}

	private TextView getText(String text)
	{
		TextView tv = new TextView(activity);
		tv.setTextSize(18);
		tv.setPadding(10, 0, 5, 0);
		tv.setText(text);
		tv.setTextColor(Color.BLACK);

		return tv;
	}

	public static String getNetworkName(int type)
	{
		String name = "";

		switch (type) {
		case ConnectivityManager.TYPE_WIFI:
			name = "Wifi";
			break;

		case ConnectivityManager.TYPE_WIMAX:
			name = "Wifi Max";
			break;

		case ConnectivityManager.TYPE_BLUETOOTH:
			name = "Bluetooth";
			break;

		case ConnectivityManager.TYPE_MOBILE:
			name = "Cell";
			break;

		case ConnectivityManager.TYPE_ETHERNET:
			name = "EtherNet";
			break;

		default:
			name = "Wifi";
			break;
		}
		return name;
	}

	public String getDate()
	{
		int day, month;
		StringBuffer date = new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		day = calendar.get(Calendar.DAY_OF_MONTH);
		month = calendar.get(Calendar.MONTH);

		String mday = day + "";
		String mmonth = month + "";

		if (day < 10) {
			mday = "0" + day;
		}
		date.append(mday + "-");
		if (month < 10) {
			mmonth = "0" + (month + 1);
		}
		date.append(mmonth + "-");
		date.append(calendar.get(Calendar.YEAR));
		return date.toString();
	}

	@Override
	public String getTitle() {
		return "Sampler";
	}

	@Override
	public void update() {
	}

	@Override
	public boolean wantActions() {
		return false;
	}
}