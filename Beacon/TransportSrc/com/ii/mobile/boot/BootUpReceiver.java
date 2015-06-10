package com.ii.mobile.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ii.mobile.home.FragLoginActivity;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, FragLoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

}