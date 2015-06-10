package com.ii.mobile.beacon;

import android.content.Intent;
import android.os.Bundle;

import com.ii.mobile.home.FragLoginActivity;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.util.L;

public class LoginActivity extends FragLoginActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this, getTopLevelClass()));
		finish();
	}

	@Override
	public Class<?> getTopLevelClass() {
		// MyToast.show("right one");
		return TransportActivity.class;
	}

	@Override
	public int getLayout() {
		L.out("got the beacon one");
		return R.layout.transport_ii_login;
	}

	@Override
	public void createGUI() {
		super.createGUI();
		//		Shortcut.INSTANCE.createShortCut(this);
		L.out("created the beacon gui");
		// BeaconSample.test();
	}
}
