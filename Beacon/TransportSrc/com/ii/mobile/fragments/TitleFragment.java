package com.ii.mobile.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ii.mobile.beacon.R;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

/**

 */
public class TitleFragment extends Fragment implements SyncCallback {
	public final static String FRAGMENT_TAG = "titleFragment";
	private LinearLayout ll;
	private TextView titleView = null;
	private TextView actionView = null;
	private BroadcastReceiver receiver = null;
	private TextView versionView;
	public static TitleFragment titleFragment = null;
	private static String HAVE_NETWORK = "#ffffff";
	private static String NO_NETWORK = "#ff9999";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// if (container == null) {
		// return null;
		// }
		// L.out("before");
		ll = (LinearLayout) inflater.inflate(R.layout.frag_title, container, false);

		ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		titleView = (TextView) ll.findViewById(R.id.titleSelection);
		versionView = (TextView) ll.findViewById(R.id.version);
		actionView = (TextView) ll.findViewById(R.id.titleAction);
		// getActivity().getSupportFragmentManager().beginTransaction().
		// add(R.id.title_fragment, this, TITLE_FRAGMENT_TAG).
		// commit();
		// Object result =
		// getActivity().getSupportFragmentManager().findFragmentByTag(TITLE_FRAGMENT_TAG);
		// L.out("result: " + result);

		// final TextView titleTextView = actionView;

		versionView.setLongClickable(true);
		versionView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// MyToast.show("long click");
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(200);
				String[] foo = new String[1];
				@SuppressWarnings("unused")
				String bar = foo[2];
				// int i = 10 / 0;
				return false;
			}
		});
		IntentFilter intentFilter = new IntentFilter("android.intent.action.SERVICE_STATE");
		receiver = new BroadcastReceiver() {

			boolean last = true;

			@Override
			public void onReceive(Context context, Intent intent) {
				L.sleep(1000);
				boolean onOff = isConnectedToInternet(context);
				@SuppressWarnings("unused")
				String tmp = "Connection is " + (onOff ? "on" :
						"off");
				// L.out(tmp);
				if (last != onOff) {
					// Bundle bundle = new Bundle();
					// String tmp = "Connection is " + (onOff ? "on" :
					// "off");
					// bundle.putString(Tickler.TEXT_MESSAGE, tmp);
					// bundle.putString(Tickler.RECEIVED_DATE, null);
					// bundle.putString(Tickler.FROM_USER_NAME, "B");
					// OpsInstantMessageFragment.receivedMessage(bundle);
					setInternetColor(onOff);
					last = onOff;
				}
			}
		};
		getActivity().registerReceiver(receiver, intentFilter);

		L.out("title: " + UpdateController.getActorStatus);
		update(UpdateController.getActorStatus);
		titleFragment = this;
		return ll;
	}

	public void onReceive(WifiManager wifiManager) {
		int numberOfLevels = 5;
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
		System.out.println("Bars =" + level);
	}

	private void setInternetColor(final boolean onOrOff) {
		// MyToast.show("set internet color: " + onOrOff);
		final boolean newOnOrOff = isConnectedToInternet(getActivity());
		// MyToast.show("set internet color newOnOrOff: " + newOnOrOff);
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (newOnOrOff)
					actionView.setTextColor(Color.parseColor(HAVE_NETWORK));
				else
					actionView.setTextColor(Color.parseColor(NO_NETWORK));
			}
		});
	}

	// private static boolean isAirplaneModeOn(Context context) {
	// // Toggle airplane mode.
	//
	// return Settings.System.getInt(context.getContentResolver(),
	// Settings.System.AIRPLANE_MODE_ON, 0) != 0;
	//
	// }

	public static boolean isConnectedToInternet(Context context) {
		// L.out("activity: " + context);
		if (context == null) {
			L.out("no context yet");
			return false;
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		boolean connected = info != null && info.isConnected();

		return connected;
	}

	public void setTitle(String title) {
		new UpdateDisplay().execute(null, title);
	}

	@Override
	public void onResume() {
		super.onResume();
		setInternetColor(isConnectedToInternet(getActivity()));
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		// update();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
		if (receiver != null && getActivity() != null)
			getActivity().unregisterReceiver(receiver);
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	// update();
	// }

	public void update() {
		L.out("update: " + UpdateController.getActorStatus + L.p());
		setInternetColor(isConnectedToInternet(getActivity()));
		update(UpdateController.getActorStatus);
	}

	public void update(GetActorStatus getActorStatus) {
		// L.out("statusWrapper: " + statusWrapper);
		// L.out("master statusWrapper: " + UpdateController.STATUS_WRAPPER);
		// String employeeStatus = statusWrapper.currentStatus.employeeStatus;
		if (getActorStatus == null)
			return;
		new UpdateDisplay().execute(getActorStatus.getActorStatusId(), null);
	}

	// labels
	class UpdateDisplay extends AsyncTask<String, Integer, Long> {
		String statusId = null;
		String title = null;

		@Override
		protected Long doInBackground(String... params) {
			statusId = params[0];
			title = params[1];

			if (statusId == null)
				return 0l;
			// if (statusId.equals(BreakFragment.AT_LUNCH))
			// statusId = "At Lunch";
			// if (statusId.equals(BreakFragment.ON_BREAK))
			// statusId = "On Break";
			// L.out("action: " + status);
			// L.out("title: " + title);
			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Long l) {
			if (titleView != null && title != null)
				titleView.setText(title);
			if (versionView != null)
				versionView.setText(getActivity().getString(R.string.crothall_version));
			// if (actionView != null && statusId != null)
			// actionView.setText(StaticFlow.INSTANCE.findActorStatusName(statusId));
			if (actionView != null)
				actionView.setText("Beacon");
		}
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("callback: " + gJon.getClass().getSimpleName());
		update(UpdateController.getActorStatus);
	}

}