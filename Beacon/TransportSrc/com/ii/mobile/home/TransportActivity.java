package com.ii.mobile.home;

import java.util.GregorianCalendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.ii.mobile.application.Shortcut;
import com.ii.mobile.beacon.R;
import com.ii.mobile.bus.Binder;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.fragments.RadioFragment;
import com.ii.mobile.fragments.TitleFragment;
import com.ii.mobile.paging.ActionFragmentController;
import com.ii.mobile.paging.PageFragmentController;
import com.ii.mobile.tickle.TickleService;
import com.ii.mobile.update.Updater;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

/**
 * 
 */
public class TransportActivity extends FragmentActivity {
	public static PageFragmentController pageFragmentController;
	private ActionFragmentController actionFragmentController;
	// private final static int TASK_PAGE_POSITION = 2;

	private static final int RESULT_SETTINGS = 1;

	private static FragTickled fragTickled;

	public static final int INSTANT_MESSAGE_DISPATCH_OPS = 0;
	public static final int INSTANT_MESSAGE_DISPATCH_PAGE = 1;
	public static final int ACTION_VIEW_PAGE = 1;
	public static final int SELF_ACTION_PAGE = 3;
	public static final int ACTION_HISTORY_PAGE = 4;

	// private static DataCache dataCache = null;

	private Binder binder = null;
	private GregorianCalendar startTimer;
	public static TransportActivity transportActivity = null;

	public static boolean running = false;

	public static boolean showToast = false;

	protected void initCritter() {
		// new Updater(this).checkForUpdate();
		Resources resources = getResources();
		boolean isProduction = resources.getBoolean(R.bool.isProduction);
		boolean wantCrashReport = resources.getBoolean(R.bool.wantCrashReport);
		boolean isTestVersion = resources.getBoolean(R.bool.isTestVersion);
		if (wantCrashReport)
		{
			String appId;
			if (isProduction)
				appId = resources.getString(R.string.prodId);
			else
				if(isTestVersion)
					appId = resources.getString(R.string.testId);
				else
					appId = resources.getString(R.string.devId);
			Crittercism.initialize(this, appId);
			String username;
			if (!User.getUser().getUsername().equals(""))
				username = User.getUser().getUsername() + "/" + User.getUser().getFacilityID();
			else
				username = "No Login";
			Crittercism.setUsername(username);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transport);
		SharedPreferences preferences = getSharedPreferences(User.PREFERENCE_FILE, MODE_PRIVATE);
		initCritter();
		transportActivity = this;
		// dataCache = DataCache.makeInstance(this);
		pageFragmentController = new PageFragmentController(this);
		actionFragmentController = new ActionFragmentController(this);
		pageFragmentController.setPosition(ACTION_VIEW_PAGE);
		// FragTaskController.getFragTaskController(this);
		// L.out("startService" + TickleService.class);
		startService(new Intent(this, TickleService.class));
		binder = new Binder(this, incomingMessenger, TickleService.class);
		if(!preferences.getBoolean("shortcutKey", false))
		{
			preferences.edit().putBoolean("shortcutKey", true).commit();
			Shortcut.INSTANCE.createShortCut(this);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		L.out("onResume");
		// new FlowStaticLoader(this).execute();
		// dataCache = DataCache.makeInstance(this);
		running = true;
		// Alert.resume(this);
		// startTimer = L.startTimer();
		// fragTickled.onResume();
		// update();
		// boolean success = UpdateController.INSTANCE.staticLoad();
		// if (!success) {
		// MyToast.show("Failed to load static content!");
		// finish();
		// }

		UserWatcher.INSTANCE.start();
		UpdateController.INSTANCE.registerCallback(actionFragmentController, FlowRestService.GET_ACTOR_STATUS);
		UpdateController.INSTANCE.setActivity(this);
		// new FlowStaticLoader(this).execute();
		// if (UpdateController.getActorStatus == null)
		// finish();
		if (UpdateController.getActorStatus != null)
			UpdateController.INSTANCE.callback(UpdateController.getActorStatus, FlowRestService.GET_ACTOR_STATUS);
		// update();
		// UpdateController.INSTANCE.callback(UpdateController.getActorStatus,
		// FlowRestService.GET_ACTOR_STATUS);
		L.out("finished onResume");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		binder.onDestroy();
		UserWatcher.INSTANCE.stop();
		UpdateController.INSTANCE.unRegisterCallback(actionFragmentController, FlowRestService.GET_ACTOR_STATUS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actor_options_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.update:
			// MyToast.show("update");
			User.getUser().initUpdate();
			new Updater(this).checkForUpdate();
			return true;

		default:
		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.frag_options_menu, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.volume:
	// MyToast.show("volume");
	// return true;
	// case R.id.preferences:
	// MyToast.show("preferences");
	// Intent i = new Intent(this, PreferencesActivity.class);
	// startActivityForResult(i, RESULT_SETTINGS);
	// return true;
	// default:
	// }
	// return super.onOptionsItemSelected(item);
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		L.out("requestCode: " + requestCode);
		switch (requestCode) {
		case RESULT_SETTINGS:
			showUserSettings();
			break;
		}
	}

	private void showUserSettings() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		StringBuilder builder = new StringBuilder();

		builder.append("\n Username: "
				+ sharedPrefs.getString("prefUsername", "NULL"));

		builder.append("\n Send report:"
				+ sharedPrefs.getBoolean("prefSendReport", false));

		builder.append("\n Sync Frequency: "
				+ sharedPrefs.getString("prefSyncFrequency", "NULL"));
		MyToast.show("preferences: " + builder);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		L.out("newConfig: " + newConfig.screenLayout);
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		L.out("onPause");
		running = false;
		// fragTickled.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		L.out("onStop");
	}

	public void updatePageTitle(String title) {
		TitleFragment titleFragment = (TitleFragment)
				getSupportFragmentManager().findFragmentByTag(TitleFragment.FRAGMENT_TAG);
		L.out("titleFragment: " + title);
		if (titleFragment != null)
			titleFragment.setTitle(title);
	}

	//
	// public void startTask() {
	// actionFragmentController.startTask();
	// }

	private final Handler incomingHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			// L.out("message: " + message);
			Bundle data = message.getData();
			// L.out("data: " + data + "message.arg1 " + message.arg1 + " ");
			if (data != null) {
				getTickled().checkTickle(data);
			}
		}
	};

	private FragTickled getTickled() {
		if (fragTickled != null)
			return fragTickled;
		fragTickled = new FragTickled(this);
		return fragTickled;
	}

	final Messenger incomingMessenger = new Messenger(incomingHandler);

	public static void setNotify(int position) {
		if (transportActivity == null) {
			L.out("ERROR: Unable to setNotify: " + position);
			return;
		}
		RadioFragment radioFragment = (RadioFragment)
				transportActivity.getSupportFragmentManager().findFragmentByTag(RadioFragment.RADIO_FRAGMENT_TAG);

		radioFragment.setNotify(position);
		setPosition(position);
	}

	public static void setPosition(int position) {
		if (transportActivity == null) {
			L.out("ERROR: Unable to setPosition: " + position);
			return;
		}
		if (running)
			transportActivity.pageFragmentController.setPosition(position);
	}

}
