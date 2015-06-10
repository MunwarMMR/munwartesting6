package com.ii.mobile.beacon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ii.mobile.beacon.fragments.WifiListFragment;

public class NetworkStateChangeReciever extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		StateChange.addStateChange(context, intent, false);

		if (WifiListFragment.wifiListFragment != null)
			WifiListFragment.wifiListFragment.updateNetworksList();

		// if (!isWifi && !is3G && false)
		// {
		// Intent intent1 = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
		// ComponentName cn = new ComponentName("com.android.phone",
		// "com.android.phone.Settings");
		// intent.setComponent(cn);
		// context.startActivity(intent1);
		// }
	}
}