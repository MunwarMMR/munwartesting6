package com.ii.mobile.beacon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.model.Network;

public class MainActivity extends Activity implements OnClickListener {

	TableLayout lout;
	ArrayList<Network> networksList;
	WifiManager wifiManager;
	ConnectivityManager connectivityManager;
	Handler timeHandler = new Handler();
	File file;
	WritableWorkbook workbook;
	WritableSheet sheet;
	WifiConfiguration wifiConfiguration;
	Network current;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beacon_wifi_list_fragment);
		lout = (TableLayout) findViewById(R.id.lout_networks);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// ((TextView) findViewById(R.id.tv_geo)).setText("Geography : " +
		// getIntent().getExtras().get("geo"));
		updateNetworkStatus();
		// findViewById(R.id.btn_sample).setOnClickListener(this);
		// findViewById(R.id.btn_email).setOnClickListener(this);

		// timeHandler.removeCallbacks(updateTimeTask);
		// timeHandler.postDelayed(updateTimeTask, 1000);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		// case R.id.btn_sample:
		// // Save the list to DB and go back to previous screen
		// Toast.makeText(this, "Sample is Saved", Toast.LENGTH_LONG).show();
		// finish();
		//
		// break;
		//
		// case R.id.btn_email:
		// if (null != networksList && networksList.size() > 0)
		// sendMail();
		// else
		// Toast.makeText(this, "Nothing to Mail", Toast.LENGTH_LONG).show();
		// break;

		default:

			break;
		} // TODO Auto-generated method stub
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
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + file));

			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			System.out.println("Email file_exists!");
		}
		else
		{
			System.out.println("@@@@@@@@@@@@@ ------  Email file does not exist!");
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

	private void updateNetworkStatus()
	{
		lout.removeViews(1, lout.getChildCount() - 1);
		ArrayList<ScanResult> list = (ArrayList<ScanResult>) wifiManager.getScanResults();
		NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
		if (null == infos && null == list)
			System.out.println(" @@@@@ %%%%% $$$$ No Networks Available");
		else
		{
			String Fnamexls = "Beacon" + ".xls";
			File sdCard = Environment.getExternalStorageDirectory();
			File directory = new File(sdCard.getAbsolutePath() + "/newfolder");
			directory.mkdirs();

			file = new File(directory, Fnamexls);

			WorkbookSettings wbSettings = new WorkbookSettings();
			wbSettings.setLocale(new Locale("en", "EN"));

			try {
				workbook = Workbook.createWorkbook(file, wbSettings);
				sheet = workbook.createSheet("Beacon Session", 0);
				Label label_slno = new Label(0, 0, " Sl No ");
				Label label_ssid = new Label(1, 0, " SSID ");
				Label label_mac = new Label(2, 0, " MAC Address ");
				Label label_strength = new Label(3, 0, " Strength ");
				Label label_frequency = new Label(4, 0, " Frequency(Hz) ");

				try {
					sheet.addCell(label_slno);
					sheet.addCell(label_ssid);
					sheet.addCell(label_mac);
					sheet.addCell(label_strength);
					sheet.addCell(label_frequency);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			networksList = new ArrayList<Network>();

			if (list.size() > 0)
			{
				try {
					System.out.println(" @@@@@ %%%%% $$$$ Many Wifi Networks Available " + list.size());

					for (ScanResult scan : list)
					{
						Network network = new Network();
						network.setName(scan.SSID);
						network.setStrength(scan.level);
						network.setBssid(scan.BSSID);
						network.setFrequency(scan.frequency);
						network.setType(0);

						String securityMode = getScanResultSecurity(scan);

						if (securityMode.equalsIgnoreCase("OPEN")) {
							network.setHasPassword(false);
						} else if (securityMode.equalsIgnoreCase("WEP")) {
							network.setHasPassword(true);
						}
						networksList.add(network);
					}
				} catch (Exception e)
				{
					System.out.println(" From Catch : " + e.toString());
				}
			}
			else
				System.out.println(" @@@@@ %%%%% $$$$ No Wifi Networks Available");

			// if(null == infos)
			// System.out.println(" @@@@@ %%%%% $$$$ No Networks Available");
			// else
			// {
			// for(NetworkInfo info : infos)
			// {
			// if(info.getType() == ConnectivityManager.TYPE_MOBILE)
			// {
			// TelephonyManager telephonyManager =
			// (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
			// System.out.println("Phone Number is : "+telephonyManager.getLine1Number());
			// List<CellInfo> listCellinfo = telephonyManager.getAllCellInfo();
			// for(CellInfo cellInfo : listCellinfo)
			// {
			// CellInfoGsm cellinfogsm = (CellInfoGsm)cellInfo;
			// CellSignalStrengthGsm cellSignalStrengthGsm =
			// cellinfogsm.getCellSignalStrength();
			// cellSignalStrengthGsm.getLevel();
			// cellSignalStrengthGsm.getDbm();
			// Network network = new Network();
			// network.setName(info.getExtraInfo());
			// network.setStrength(cellSignalStrengthGsm.getDbm());
			// network.setType(1);
			// networksList.add(network);
			//
			// System.out.println(" Strength "+cellSignalStrengthGsm.getLevel());
			// }
			// }
			// }
			// }
		}

		int i = 1;
		for (final Network network : networksList)
		{
			try {
				TableRow row = new TableRow(MainActivity.this);
				System.out.println("%%%%%%% Writing to Excel");
				Label label_slno = new Label(0, i, " " + i + " ");
				Label label_ssid = new Label(1, i, " " + network.getName() + " ");
				Label label_mac = new Label(2, i, " " + network.getBssid() + " ");
				Label label_strength = new Label(3, i, " " + network.getStrength() + " ");
				Label label_frequency = new Label(4, i, " " + network.getFrequency() + " ");

				sheet.addCell(label_slno);
				sheet.addCell(label_ssid);
				sheet.addCell(label_mac);
				sheet.addCell(label_strength);
				sheet.addCell(label_frequency);

				current = network;
				if (wifiManager.getConnectionInfo().getBSSID().equalsIgnoreCase(
						current.getBssid()))
					row.setBackgroundColor(Color.GRAY);
				row.addView(getTextView("" + i));
				row.addView(getText(" " + network.getName() + " "));
				row.addView(getTextView(" " + network.getBssid() + " "));
				row.addView(getTextView(" " + network.getStrength() + " "));
				row.addView(getTextView(" " + network.getFrequency() + " "));
				System.out.println(network.getName() + "," + network.getBssid() + "," + network.getStrength()
						+ "," + network.getFrequency());
				row.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						connectToWifi(network);
					}
				});

				lout.addView(row);
				++i;
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
		try {
			if (null != workbook)
			{
				workbook.write();
				workbook.close();
			}
		} catch (IOException e) {

			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
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
		TextView tv = new TextView(this);
		tv.setTextSize(18);
		tv.setText(text);
		tv.setTextColor(Color.WHITE);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);

		return tv;
	}

	private TextView getText(String text)
	{
		TextView tv = new TextView(this);
		tv.setTextSize(18);
		tv.setPadding(10, 0, 5, 0);
		tv.setText(text);
		tv.setTextColor(Color.WHITE);

		return tv;
	}

	String getNetworkName(int type)
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
			name = "Mobile";
			break;

		case ConnectivityManager.TYPE_ETHERNET:
			name = "EtherNet";
			break;

		default:
			break;
		}
		return name;
	}
}
