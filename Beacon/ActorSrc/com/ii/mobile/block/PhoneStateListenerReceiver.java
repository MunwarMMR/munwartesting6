package com.ii.mobile.block;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.ii.mobile.block.blocker.BlockService;
import com.ii.mobile.util.L;

public class PhoneStateListenerReceiver extends BroadcastReceiver {
	BroadcastReceiver CallBlocker;
	TelephonyManager telephonyManager;
	ITelephony telephonyService;

	PhoneNumber blockedPhoneNumber = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		hangUpCall(context);
	}

	private void hangUpCall(Context context) {
		// L.out("Hangup Call!");
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		Class c = null;
		try {
			c = Class.forName(telephonyManager.getClass().getName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Method m = null;
		try {
			m = c.getDeclaredMethod("getITelephony");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		m.setAccessible(true);
		try {
			telephonyService = (ITelephony) m.invoke(telephonyManager);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		telephonyManager.listen(callBlockListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	PhoneStateListener callBlockListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state,
				String incomingNumber) {
			// L.out("state changed: " + state + " number: " + incomingNumber);
			// L.out("running: " + BlockService.running);
			// L.out("phoneNumber: " + PhoneNumber.getBlockCalls());
			if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
				if (PhoneNumber.getBlockCalls()) {
					try {
						if (BlockService.running) {
							L.out("ending it");
							telephonyService.endCall();
						} else {
							L.out("allowing call to: " + incomingNumber);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

}