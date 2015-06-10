package com.ii.mobile.instantMessage;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.util.L;

/**

 */
public class OpsInstantMessageFragment extends InstantMessageFragment {
	// private final EditText chatInputWindow = null;
	// private final String currentText = "";
	// private View view;
	// private final String title = "Instant Message";
	private static OpsInstantMessageFragment opsInstantMessageFragment = null;

	WifiManager mainWifiObj;
	WifiScanReceiver wifiReciever;

	// private final boolean wantSampleText = true;

	// private boolean running = false;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		L.out("OpsInstantMessageFragment: " + bundle);
		OpsInstantMessageFragment.opsInstantMessageFragment = this;
	}

	public static void receivedMessage(Bundle bundle) {
		L.out("received message");
		if (opsInstantMessageFragment == null) {
			L.out("*** ERROR No instantMessageFragment to receive message");
			return;
		}
		opsInstantMessageFragment.addMessage(bundle);
	}

	@Override
	public void addDebugLongClick() {
		chatOutputWindow.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {

				Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(1000);
				L.out("before");
				mainWifiObj = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
				wifiReciever = new WifiScanReceiver();
				getActivity().registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				mainWifiObj.startScan();

				L.out("after");
				//
				// WifiInfo wifiInfo = mainWifiObj.getConnectionInfo();
				// String tmp = "No Wifi";
				// if (wifiInfo != null) {
				// tmp = "Connected";
				// SupplicantState bar = wifiInfo.getSupplicantState();
				// // bar.
				// Bundle bundle = new Bundle();
				// // String tmp = "suplicant: "+bar.
				// bundle.putString(Tickler.TEXT_MESSAGE, tmp);
				// bundle.putString(Tickler.RECEIVED_DATE, null);
				// bundle.putString(Tickler.FROM_USER_NAME, "");
				// receivedMessage(bundle);
				//
				// }
				return true;
			}
		});
	}

	class WifiScanReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context c, Intent intent) {
			L.out("gotit");
			List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
			String[] wifis = new String[wifiScanList.size()];
			for (int i = 0; i < wifiScanList.size(); i++) {
				wifis[i] = ((wifiScanList.get(i)).toString());
				printScanResult(wifiScanList.get(i));
			}
			getActivity().unregisterReceiver(wifiReciever);
		}

		private void printScanResult(ScanResult scanResult) {
			Bundle bundle = new Bundle();
			WifiInfo foo = mainWifiObj.getConnectionInfo();
			String tmp = "SSID: " + scanResult.SSID + " %: " + scanResult.level;
			bundle.putString(Tickler.TEXT_MESSAGE, tmp);
			bundle.putString(Tickler.RECEIVED_DATE, null);
			bundle.putString(Tickler.FROM_USER_NAME, "");
			receivedMessage(bundle);
		}
	}

	@Override
	public String[] getDialog() {
		return SHORT_DIALOGUE;
	}

	public static final String[] SHORT_DIALOGUE =
	{
			"Your Beacon welcomes you!"
	};

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("callback: " + payloadName);
		// receiveMessage("I/O", gJon.getNewJson());
	}

}
