package com.ii.mobile.application;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ii.mobile.beacon.R;
import com.ii.mobile.users.User;

public enum Shortcut {
	INSTANCE;
	private final String PREFERENCE_FILE = User.PREFERENCE_FILE;
	private final String SHORTCUT_KEY = "shortcutKey";

	private final String APP_NAME = "Beacon";

	public void createShortCut(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(PREFERENCE_FILE, 0);

		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(SHORTCUT_KEY, true);
		editor.commit();

		addShortcut(activity);
	}

	private void addShortcut(Activity activity) {
		// Adding shortcut for MainActivity
		// on Home screen

		Intent shortcutIntent = new Intent();
		shortcutIntent.setClassName("com.ii.mobile.beacon", activity.getClass().getName());
		shortcutIntent.setAction(Intent.ACTION_MAIN);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, APP_NAME);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(activity,
						R.drawable.icon));
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		activity.sendBroadcast(addIntent);
	}

	//	private void removeShortCut(Activity activity) {
	//		Intent removeIntent = new Intent();
	//		removeIntent.setClassName("com.ii.mobile.beacon", activity.getClass().getName());
	//		removeIntent.setAction(Intent.ACTION_MAIN);
	//
	//		Intent addIntent = new Intent();
	//		addIntent
	//		.putExtra(Intent.EXTRA_SHORTCUT_INTENT, removeIntent);
	//		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, APP_NAME);
	//
	//		addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
	//		activity.sendBroadcast(addIntent);
	//	}
}
