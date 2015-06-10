package com.ii.mobile.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.ii.mobile.flow.Flow;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

class DialogLogin extends AsyncTask<Void, Integer, Long> {
	// was false!
	boolean successfulLogin = false;
	ProgressDialog progressDialog;
	private final FragLoginActivity fragLoginActivity;

	public DialogLogin(FragLoginActivity fragLoginActivity) {
		this.fragLoginActivity = fragLoginActivity;
	}

	@Override
	protected Long doInBackground(Void... arg0) {
		User user = User.getUser();
		L.out("user: " + user + " " + user.getUsername() + " " + user.getPassword());
		// remove this! signOnWithPassword allows in without password!
		successfulLogin =
				Flow.getFlow().signOnWithPassword(user.getUsername(),
						user.getPassword());
		// remove this! it allows in without password!
		// successfulLogin = true;
		if (successfulLogin) {
			user.saveUser();
			user.setNeedLogout(false);
			// for UserWatcher
			User.validateUser = new ValidateUser();
			UpdateController.INSTANCE.clearStaticLoad();

		} else {
			Login.INSTANCE.reset();
			// user.setNeedLogout(true);
			// MyToast.show("Failed to login as: " + user.getUsername());
			// doingLogin = false;
		}
		return 0l;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		L.out("probably not used but executes in UI thread");
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(fragLoginActivity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Validating Credentials ...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		L.out("progress");
	}

	@Override
	protected void onPostExecute(Long l) {
		L.out("dismissing");
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			MyToast.show("ERROR on dismiss: " + e);
		}
		// instantiate metadata json object

		// L.out("after critter");
		SharedPreferences settings = fragLoginActivity.getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(FragLoginActivity.STAFF_USER, false);
		if (!staffUser) {
			// txtPassword.setText("");
		}
		if (successfulLogin) {
			L.out("success");
			// MyToast.show("Successful Login");
			User.getUser().setNeedLogin(true);
			User.getUser().setNeedLogout(false);
			Tickler.onResume();
			Intent intent = new Intent().setClass(fragLoginActivity,
					fragLoginActivity.getTopLevelClass());
			fragLoginActivity.startActivity(intent);
			// setEmployeeStatusAvailable();

		} else {
			// L.out("debug: " + User.getUser() + " " +
			// User.getUser().getValidateUser());
			// if (User.getUser().getValidateUser() != null) {
			// L.out("debug: " + User.getUser() + " "
			// + User.getUser().getValidateUser().getJson());
			// }
			if (!FragLoginActivity.isConnectedToInternet(fragLoginActivity))
				MyToast.show("Need a connection to internet\nto login!");
			else
				MyToast.show("Invalid Login");
			User.getUser().setValidateUser(null);
		}
	}
}
