/*
 * 
 */
package com.ii.mobile.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class ApplicationContext extends Application {

	// used by toast
	public static Activity activity = null;
	public static String PREFERENCE_FILE = "crothallBeacon";

	private static ApplicationContext applicationContext = null;
	public static User user;

	public ApplicationContext() {
		super();
		applicationContext = this;
		L.out("applicationContext: " + applicationContext);

		// test

	}

	public static Context getAppContext() {
		// L.out("applicationContext: " + applicationContext);
		return applicationContext;
	}

}
