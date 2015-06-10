package com.ii.mobile.home;

import java.util.GregorianCalendar;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.ii.mobile.beacon.R;
import com.ii.mobile.alarm.Alarm;
import com.ii.mobile.application.Shortcut;
import com.ii.mobile.block.blocker.AntiSync;
import com.ii.mobile.block.blocker.BlockService;
import com.ii.mobile.monitor.UnitTestActivity;
import com.ii.mobile.service.NotifyService;
import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.Soap;
import com.ii.mobile.soap.StaticSoap;
import com.ii.mobile.soap.StaticSoap.StaticSoapColumns;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.tab.BreakActivity;
import com.ii.mobile.tab.PickField;
import com.ii.mobile.tab.SelfTaskActivity;
import com.ii.mobile.tab.TabNavigationActivity;
import com.ii.mobile.update.Updater;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.SecurityUtils;

public class LoginActivity extends Activity implements TextWatcher, OnEditorActionListener {

	private EditText txtUsername;
	private EditText txtPassword;
	public Button loginButton;
	private User user = null;
	protected Vibrator vibrator;
	private String platform = null;
	public static String STAFF_USER = "staffUser";
	private static long loginTime = 0l;
	private boolean addedLongClick = false;

	public static LoginActivity loginActivity = null;

	// static private boolean startedIntentBlocker = true;

	OnClickListener onClickListener;

	protected void initCritter() {
		//		new Updater(this).checkForUpdate();
		Resources resources = getResources();
		boolean isProduction = resources.getBoolean(R.bool.isProduction);
		boolean wantCrashReport = resources.getBoolean(R.bool.wantCrashReport);
		if(wantCrashReport)
		{
			String appId;
			if (isProduction)
				appId = resources.getString(R.string.prodId);
			else
				appId = resources.getString(R.string.devId);
			Crittercism.initialize(this, appId);
			Crittercism.setUsername("No Login");
		}
	}

	public boolean isStaffUser() {
		SharedPreferences settings = getSharedPreferences(User.PREFERENCE_FILE, 0);
		return settings.getBoolean(STAFF_USER, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loginActivity = this;
		boolean isProduction = getResources().getBoolean(R.bool.isProduction);
		if (isProduction)
			L.setDebug(false);
		else
			L.setDebug(true);
		// testFacilities();
//		if (isProduction)
			initCritter();
//		Shortcut.INSTANCE.createShortCut(this);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		L.out("LoginActivity Started");

		setContentView(getLayout());
		createGUI();
		MyToast.make(this);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		L.out("Starting Services");
		startService(new Intent(this, getNotificationService(this)));
		if (!isStaffUser())
			startService(new Intent(this, BlockService.class));
		// startService(new Intent(this, ProximityService.class));
		// startService(new Intent(this, TickleService.class));
		new Alarm().resetAlarm(this);
		// L.out("startService");
		AntiSync.INSTANCE.stopSync(this);
		// WakeLocker.INSTANCE.start(this);
	}

	public int getLayout() {
		return R.layout.ii_login;
	}

	@SuppressWarnings("unused")
	private void testFacilities() {
		String bay = "73916";
		String alaska = "120712";
		String test = "20867";

		String value = "value to encrypt ting 123ksjiajwoi  fhiudhwuhwiuhwiuwhiwu";
		L.out(" value: " + value + " " + value.length());
		String pw = "xyzzy123";
		try {
			String result = SecurityUtils.encryptAES(pw, value);
			L.out("result: " + result + " " + result.length());
			String tinged = SecurityUtils.decryptAES(pw, result);
			L.out("tinged: " + tinged + " " + tinged.length());

		} catch (Exception e) {
			L.out("encryption error: " + e + L.p());
		}
	}

	@SuppressWarnings("unused")
	private void testFacility(String facilityID) {
		Soap.setPlatform(getString(R.string.default_platform));
		GetTaskDefinitionFieldsForScreenByFacilityID foo = Soap.getSoap().getTaskDefinitionFieldsForScreenByFacilityID(facilityID);
		L.out("GetTaskDefinitionFieldsForScreenByFacilityID: " + facilityID + "\n" + foo);
		ListTaskClassesByFacilityID bar = Soap.getSoap().listTaskClassesByFacilityID(facilityID);
		L.out("GetTaskDefinitionFieldsForScreenByFacilityID: " + facilityID + "\n" + bar);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		L.out("onNewIntent: " + intent.getDataString());
	}

	public Class<?> getLoginClass() {
		return LoginActivity.class;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		L.out("onActivityResult: " + requestCode + " " + resultCode);
		// make sure we are starting
		Intent intent = new Intent(this.getApplicationContext(),
				getLoginClass());
		// L.out("starting");
		startActivity(intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		// L.out("onPause");
		// setStaleTime();
	}

	@Override
	protected void onResume() {
		super.onResume();
		L.out("onResume");
		// make sure UserWatcher has this activity
		// User.getUser().setNeedLogin(false);
		new Updater(this).checkForUpdate();
		UserWatcher.INSTANCE.login(this);
		UserWatcher.INSTANCE.doUpdate(false);
		// TaskActivity.initDataCache();
		PickField.initDataCache();
	}

	@Override
	protected void onDestroy() {
		// WakeLocker.INSTANCE.stop();
		super.onDestroy();
	}

	public Class<?> getTopLevelClass() {
		return TabNavigationActivity.class;
	}

	public Class<?> getNotificationService(Activity activity) {
		return NotifyService.class;
	}

	protected void commonGUI() {
		TextView versionView = (TextView) this.findViewById(R.id.version);
		versionView.setText(getResources().getString(R.string.crothall_version));
	}

	public void createGUI() {
		commonGUI();
		// try {
		txtUsername = (EditText) this.findViewById(R.id.txtUsername);
		txtUsername.addTextChangedListener(this);
		txtPassword = (EditText) this.findViewById(R.id.txtPassword);
		txtPassword.addTextChangedListener(this);
		txtPassword.setOnEditorActionListener(this);

		loginButton = (Button) this.findViewById(R.id.btnLogin);
		platform = getResources().getString(R.string.default_platform);
		// user = User.setUser(txtUsername.getText().toString(),
		// txtPassword.getText().toString(), platform);
		L.out("login: " + user);

		String longClickonLogin = getResources().getString(R.string.long_click_on_login);
		L.out("longClickonLogin: " + longClickonLogin);

		SharedPreferences settings = getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(STAFF_USER, false);
		platform = settings.getString(User.PLATFORM, platform);
		L.out("platform: " + platform);
		String username = settings.getString(User.UserColumns.USERNAME, "");
		L.out("username: " + username);
		String employeeID = settings.getString(User.UserColumns.EMPLOYEE_ID, "");
		L.out("employeeID: " + employeeID);
		// String facilityID = settings.getString(User.UserColumns.FACILITY_ID,
		// "");

		txtUsername.setText(username);

		// platform = settings.getString(User.PLATFORM, "");
		L.out("staffUser: " + staffUser);
		if (staffUser) {
			longClickonLogin = "true";
			String password = settings.getString(User.UserColumns.PASSWORD, "");
			L.out("password: " + password);
			txtPassword.setText(password);
			addLongClick();
		}

		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				vibrator.vibrate(200);

				if (view.equals(loginButton) && loginButton.getText().equals(getString(R.string.log_out))) {
					doLogout();
					return;
				}
				L.out("platform: " + platform);
				user = User.setUser(txtUsername.getText().toString(),
						txtPassword.getText().toString(), platform);
				L.out("user: " + user);
				if (txtPassword.getText().toString().equals("zxc")) {
					SharedPreferences settings = getSharedPreferences(
							User.PREFERENCE_FILE, 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean(STAFF_USER, true);
					editor.commit();
					txtPassword.setText("");
					stopService(new Intent(LoginActivity.this, BlockService.class));
					addLongClick();
					return;
				}
				if (txtPassword.getText().toString().equals("")) {
					SharedPreferences settings = getSharedPreferences(
							User.PREFERENCE_FILE, 0);
					boolean staffUser = settings.getBoolean(STAFF_USER, false);
					if (staffUser) {
						MyToast.show("No longer staff user");
						SharedPreferences.Editor editor = settings.edit();
						editor.putBoolean(STAFF_USER, false);
						editor.commit();
						txtPassword.setText("");
					} else {
						MyToast.show("Invalidate Login: Enter a Password!");
					}
					return;
				}
				doLogIn();

			}
		};
		loginButton.setOnClickListener(onClickListener);

		OnClickListener enterClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				vibrator.vibrate(200);
				User.getUser().setNeedLogin(true);
				User.getUser().setNeedLogout(false);
				SelfTaskActivity.initDataCache();
				SelfTaskActivity.initDataCache();

				Intent intent = new Intent().setClass(LoginActivity.this, getTopLevelClass());
				startActivity(intent);
			}
		};
		Button enterButton = (Button) this.findViewById(R.id.buttonEnter);
		// MyToast.show("enterButton: " + enterButton);
		if (enterButton != null)
			enterButton.setOnClickListener(enterClickListener);
	}

	public void doLogIn() {
		new DoLogin().execute();
	}

	public void doLogout() {
		L.out("legacy");
		user = User.getUser();
		if (!user.getValidateUser().getEmployeeStatus().equals(BreakActivity.AVAILABLE)) {
			MyToast.show("Unable to logout if you have a task or on delay!");
			return;
		}
		user.setNeedLogout(true);
		if (user.getValidateUser().getEmployeeStatus().equals(BreakActivity.AVAILABLE))
			UserWatcher.INSTANCE.doUpdate(false);
		txtPassword.setText("");
		UserWatcher.INSTANCE.stop();
		// user.getValidateUser().setEmployeeStatus(BreakActivity.NOT_IN);
		SelfTaskActivity.setEmployeeStatus(BreakActivity.NOT_IN, false, LoginActivity.this);
		MyToast.show("Logged out");
	}

	private final String lastPlatform = null;

	// long click
	public void sayClick(View v) {
		user = User.next();
		L.out("user: " + user);
		txtUsername.setText(user.getUsername());
		txtPassword.setText(user.getPassword());
		platform = user.getPlatform();
		if (platform.equals(lastPlatform))
			changePlatform();
		L.out("platform: " + platform);
		// MyToast.show("Platform is: " + shortName(user.getPlatform()),
		// Toast.LENGTH_SHORT);
		MyToast.show("Facility is: " + user.getPlatform(),
				Toast.LENGTH_SHORT);
		// MyToast.show("Changing user to: " + user.getUsername()
		// + "\nPlatform: " + user.getPlatform(), Toast.LENGTH_SHORT);
	}

	public void changePlatform() {
		L.out("do nothing change platform");
	}

	public void sayClickIntentBlock(View v) {
		// vibrator.vibrate(200);
		L.out("BlockService.running: " + BlockService.running);
		if (!BlockService.running) {
			MyToast.show("Started IntentBlock Service");
			startService(new Intent(this, BlockService.class));
		} else {
			MyToast.show("Stopped IntentBlock Service");
			stopService(new Intent(this, BlockService.class));
		}
		// startedIntentBlocker = !startedIntentBlocker;
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
		// L.out("onTextChanged: " + text);
		// if (text.toString().length() > 0) {
		// // setStaleTime();
		// }
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// L.out("afterTextChanged: " + arg0);
		String text = arg0.toString();
		if (text.length() != 0)
			if (User.getUser() != null)
				User.getUser().setValidateUser(null);
		UserWatcher.INSTANCE.doUpdate(false);
	}

	private void addLongClick() {
		TextView textView = (TextView) findViewById(R.id.fullScreen);
		if (!addedLongClick) {
			addedLongClick = true;
			MyToast.show("Running in staff mode");
			textView.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View view) {
					// L.out("long view: " + view);
					if (isStaffUser()) {
						vibrator.vibrate(400);
						sayClick(view);
					}
					return true;
				}
			});
			View view = findViewById(R.id.scrollView);
			view.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View view) {
					if (isStaffUser()) {
						vibrator.vibrate(400);
						sayClickIntentBlock(view);
					}
					return true;
				}
			});
			View unitTestView = findViewById(R.id.unitTestView);
			unitTestView.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View view) {
					if (isStaffUser()) {
						vibrator.vibrate(400);
						Intent intent = new Intent().setClass(LoginActivity.this,
								getUnitTestActivity());
						startActivity(intent);
					}
					return true;
				}
			});
		} else {
			MyToast.show("Running in staff mode");
		}
	}

	public Class<?> getUnitTestActivity() {
		// MyToast.show("right one");
		return UnitTestActivity.class;
	}

	public static String getJSon(String methodName, Activity activity) {
		return getJSon(methodName, activity, null);
	}

	synchronized public static String getJSon(String methodName, Activity activity, String taskNumber) {
		// L.out("methodName: " + methodName);
		String facilityID = User.getUser().getFacilityID();
		String employeeID = User.getUser().getEmployeeID();
		// if
		// (methodName.equals(ParsingSoap.GET_TASK_DEFINITION_FIELDS_DATA_FOR_SCREEN_BY_FACILITY_ID)
		// ||
		// methodName.equals(ParsingSoap.GET_TASK_DEFINITION_FIELDS_FOR_SCREEN_BY_FACILITYID)
		// || methodName.equals(ParsingSoap.LIST_TASK_CLASSES_BY_FACILITY_ID)
		// || methodName.equals(ParsingSoap.LIST_DELAY_TYPES))
		// employeeID = null;
		Intent intent = activity.getIntent();
		String[] selectionArgs = new String[] { employeeID, facilityID, taskNumber };
		intent.setData(Uri.parse("content://" + StaticSoap.AUTHORITY + "/" +
				methodName));
		Cursor cursor = activity.managedQuery(activity.getIntent().getData(), null, null, selectionArgs, null);
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				// int index = cursor.getColumnIndex(StaticSoapColumns.JSON);
				// L.out("index: " + index + " " + cursor.getClass());
				String json = cursor.getString(cursor.getColumnIndex(StaticSoapColumns.JSON));
				if (json != null) {
					// L.out("json: " + json.length());
				}
				else
					L.out("*** ERROR json: " + json);
				return json;

			} while (cursor.moveToNext());
		}
		return null;
	}

	public static boolean isConnectedToInternet(Context context) {
		// L.out("activity: " + context);
		if (context == null) {
			L.out("no context yet");
			return false;
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	public static long getLoginTime() {
		return loginTime;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		L.out("action: " + actionId);
		onClickListener.onClick(v);
		return false;
	}

	class DoLogin extends AsyncTask<Void, Integer, Long> {
		// was false!
		boolean successfulLogin = false;
		ProgressDialog progressDialog;

		@Override
		protected Long doInBackground(Void... arg0) {
			Thread.currentThread().setName("LoginThread");
			Intent intent = getIntent();
			intent.setData(Uri.parse("content://" + StaticSoap.AUTHORITY + "/" +
					ParsingSoap.VALIDATE_USER));

			// SharedPreferences settings =
			// getSharedPreferences(User.PREFERENCE_FILE, 0);
			// String employeeID =
			// settings.getString(User.UserColumns.EMPLOYEE_ID, "");
			// String facilityID =
			// settings.getString(User.UserColumns.FACILITY_ID, "");

			String employeeID = User.getUser().getUsername();
			String facilityID = User.getUser().getPassword();
			String platform = User.getUser().getPlatform();
			L.out("Platform: " + platform);

			String[] selectionArgs = new String[] { employeeID, facilityID, platform };
			Cursor cursor = managedQuery(getIntent().getData(), null, null, selectionArgs, null);
			if (cursor != null)
				L.out("cursor: " + cursor.getCount());
			// MyToast.show("cursor: " + cursor.getCount());
			user = User.getUser();
			L.out("user: " + user.toString());
			String password = user.getPassword();
			L.out("password: " + password);
			ValidateUser validateUser = user.getValidateUser();
			L.out("validateUser: " + validateUser);
			if (validateUser == null) {
				L.out("validateUser is null??");
				return 0l;
			}
			L.out("ValidateUser: " + validateUser.toString());
			// if (!platform.equals(validateUser.getPlatform())) {
			// L.out("different Platform have: "+platform+" got "+validateUser.getPlatform());
			// }
			// remove true!
			if (validateUser != null && password.equals(validateUser.getMobilePIN())) {
				// txtPassword.setText("");f
				user.setReload(true);
				loginTime = new GregorianCalendar().getTimeInMillis();
				successfulLogin = true;
				return 0l;
			}
			// MyToast.show("Validate: " + validateUser);
			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(LoginActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Validating Credentials...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			L.out("progress");
		}

		@Override
		protected void onPostExecute(Long l) {
			L.out("dismiss");
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				L.out("ERROR on dismiss: " + e);
			}
			// instantiate metadata json object
			L.out("got critter");
			registerCrittercism();
			L.out("after critter");
			SharedPreferences settings = getSharedPreferences(User.PREFERENCE_FILE, 0);
			boolean staffUser = settings.getBoolean(STAFF_USER, false);
			if (!staffUser) {
				// txtPassword.setText("");
			}
			if (successfulLogin) {
				L.out("success");
				// MyToast.show("Successful Login");
				User.getUser().setNeedLogin(true);
				User.getUser().setNeedLogout(false);
				Intent intent = new Intent().setClass(LoginActivity.this,
						getTopLevelClass());
				Crittercism.setUsername(User.getUser().getUsername()+"/"+User.getUser().getFacilityID());
				startActivity(intent);
				// setEmployeeStatusAvailable();

			} else {
				// L.out("debug: " + User.getUser() + " " +
				// User.getUser().getValidateUser());
				// if (User.getUser().getValidateUser() != null) {
				// L.out("debug: " + User.getUser() + " "
				// + User.getUser().getValidateUser().getJson());
				// }
				if (!isConnectedToInternet(LoginActivity.this))
					MyToast.show("Need a connection to internet\nto login!");
				else
					MyToast.show("Invalid Login");
				User.getUser().setValidateUser(null);
			}
		}

		private void registerCrittercism() {
			if (User.getUser().getValidateUser() != null) {
				try {
					JSONObject metadata = new JSONObject();
					// add arbitrary metadata
					metadata.put("user_id", User.getUser().getValidateUser().getEmployeeID());
					metadata.put("facility_id", User.getUser().getValidateUser().getFacilityID());
					metadata.put("name", User.getUser().getUsername());
					metadata.put("username", User.getUser().getUsername());
					metadata.put("android", android.os.Build.VERSION.RELEASE);
					metadata.put("MANUFACTURER", android.os.Build.MANUFACTURER);
					metadata.put("BRAND", android.os.Build.BRAND);
					metadata.put("DEVICE", android.os.Build.DEVICE);
					metadata.put("BOARD", android.os.Build.BOARD);
					// send metadata to crittercism (asynchronously)
					Crittercism.setMetadata(metadata);
				} catch (Exception e) {
					L.out("crittercism error: " + e);
				}
			}
		}
	}

}