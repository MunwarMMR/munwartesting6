package com.ii.mobile.home;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.ii.mobile.beacon.R;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.flow.authenticate.AuthenticateCallBack;
import com.ii.mobile.flow.authenticate.AzureToken;
import com.ii.mobile.service.TransportNotificationService;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class FragLoginActivity extends LoginActivity implements AuthenticateCallBack {

	private FragLoginActivity fragLoginActivity = null;

	@Override
	public Class<?> getTopLevelClass() {
		// MyToast.show("right one");
		return TransportActivity.class;
	}

	@Override
	public Class<?> getLoginClass() {
		return FragLoginActivity.class;
	}

	@Override
	public Class<?> getUnitTestActivity() {
		// MyToast.show("right one");
		return DevTestActivity.class;
	}

	@Override
	public Class<?> getNotificationService(Activity activity) {
		// MyToast.show("right one");
		this.fragLoginActivity = (FragLoginActivity) activity;
		L.out("fragLoginActivity: " + fragLoginActivity);
		return TransportNotificationService.class;
	}

	@Override
	public int getLayout() {
		// L.out("got the right one");
		return R.layout.transport_ii_login;
	}

	@Override
	public void createGUI() {
		L.out("created the correct gui");
		// BeaconSample.test();
		// commonGUI();
		super.createGUI();
		addUnitTestListener();
		UpdateController.INSTANCE.setActivity(this);
		// Button legacyToggle = (Button) this.findViewById(R.id.legacyToggle);
		// if (Legacy.INSTANCE.isLegacy)
		// legacyToggle.setText(LEGACY);
		// else
		// legacyToggle.setText(TEAM_FLOW);
		// setLegacyListener(this, legacyToggle);

		// Button enterButton = (Button)
		// this.findViewById(R.id.transportButtonEnter);
		// if (new Authenticate().restoreFromSettings(this) != null)
		// enterButton.setText(ENTER);
		// else
		// enterButton.setText(LOGIN);
		// setEnterListener(this, enterButton);
		// boolean result = UpdateController.INSTANCE.staticLoad();
		// L.out("static load: " + result);
	}

	private void addUnitTestListener() {

		View unitTestView = findViewById(R.id.unitTestView);
		unitTestView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				if (isStaffUser()) {
					vibrator.vibrate(400);
					Intent intent = new Intent().setClass(FragLoginActivity.this,
							getUnitTestActivity());
					startActivity(intent);
				}
				return true;
			}
		});
	}

	@Override
	protected void initCritter() {
		L.out("initing critter");

		// new Updater(this).checkForUpdate();
		// Critter.makeInstance(this, null);
		Critter.makeInstance(this, "540fb5e6d478bc79e5000004");
		// View view = ((ViewGroup)
		// findViewById(android.R.id.content)).getChildAt(0).findViewById(R.id.TextView02);

		// if (view == null) {
		// L.out("oops not it");
		// 50af
		// }
		// InputMethodManager in = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// in.hideSoftInputFromWindow(view.getApplicationWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onPause() {
		L.out("onPause");
		super.onPause();

	}

	@Override
	public void onResume() {
		L.out("onResume");
		super.onResume();
	}

	@Override
	public void changePlatform() {
		L.out("change platform");
		UpdateController.INSTANCE.clearStaticLoad();

	}

	// boolean doingLogin = false;

	@Override
	public void doLogIn() {
		// if (doingLogin) {
		// MyToast.show("Ignoring extra button press - wait please");
		// return;
		// }
		// doingLogin = true;
		// loginButton.setEnabled(false);
		User user = User.getUser();
		L.out("user: " + user + " " + user.getUsername() + " " + user.getPassword());
		// L.out("validateuser: " + user.getValidateUser());
		// ValidateUser validateUser = user.getValidateUser();
		new DialogLogin(this).execute();
	}

	@Override
	public void doLogout() {

		// ((EditText) this.findViewById(R.id.txtPassword)).setText("");
		new DialogLogout(this, (EditText) this.findViewById(R.id.txtPassword)).execute();
		// User.getUser().setNeedLogout(true);
		// L.out("doLogout: " + User.getUser());
		//
		// UserWatcher.INSTANCE.doUpdate(false);
		// ((EditText) this.findViewById(R.id.txtPassword)).setText("");
		// UserWatcher.INSTANCE.stop();
		// // user.getValidateUser().setEmployeeStatus(BreakActivity.NOT_IN);
		// Flow.getFlow().signOff();
		// Login.authorization = null;
		// MyToast.show(User.getUser().getUsername() + " Logged out");
	}

	@Override
	public void callBack(AzureToken azureToken) {
		MyToast.show("not used!");

	}
}
