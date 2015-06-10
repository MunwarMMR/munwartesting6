package com.ii.mobile.actionButtons;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ii.mobile.beacon.R;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tab.StatusType;
import com.ii.mobile.timers.Swoosh;
import com.ii.mobile.timers.Ticker;
import com.ii.mobile.util.L;

/**

 */
public class BreakFragment extends ActionFragment implements NamedFragment, SyncCallback {
	public final static String FRAGMENT_TAG = "breakFragment";

	public static String AVAILABLE = "Available";
	public static String ACTIVE = "Active";
	public static String ASSIGNED = "Assigned";
	public static String DELAYED = "Delayed";
	public static String AT_LUNCH = "Lunch";
	public static String ON_BREAK = "Break";
	public static String NOT_IN = "Not In";

	public static StatusType[] statusTypes = new StatusType[] {
			new StatusType(AVAILABLE, "1"),
			new StatusType(AT_LUNCH, "5"),
			new StatusType(ON_BREAK, "6"),
			new StatusType(NOT_IN, "7"),
	};

	private View view = null;

	private Swoosh breakSwoosh = null;

	private static final String TITLE = "On Break";
	public static final int BACKGROUND = R.drawable.frag_blue_swoosh;
	private static final int SHORT_BREAK_TIME = 15 * 60;
	private static final String SHORT_BREAK_NAME = "Break";
	private static final String LUNCH_BREAK_NAME = "Lunch";
	private static final int LONG_BREAK_TIME = 30 * 60;

	private final boolean demoMode = false;

	private String lastStatus = null;

	private boolean paused;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.out("onCreate");
	}

	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// this.activity = (FragmentActivity) activity;
	// UpdateController.INSTANCE.registerCallback(this,
	// UpdateController.STATUS_WRAPPER);
	// }
	//
	// @Override
	// public void onDetach() {
	// super.onDetach();
	// UpdateController.INSTANCE.unRegisterCallback(this,
	// UpdateController.STATUS_WRAPPER);
	// }
	//
	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// L.out("onDestroy");
	// Ticker.INSTANCE.unregister(breakSwoosh);
	// }

	private void initClock() {
		// String employeeStatus =
		// UpdateController.INSTANCE.statusWrapper.currentStatus.employeeStatus;
		if (StaticFlow.ACTOR_BREAK.equals(UpdateController.getActorStatus.getActorStatusId())) {
			breakSwoosh.setTitle(SHORT_BREAK_NAME);
			startClock(SHORT_BREAK_TIME);
		} else {
			startClock(LONG_BREAK_TIME);
			breakSwoosh.setTitle(LUNCH_BREAK_NAME);
		}
	}

	public void startClock(int breakTime) {
		L.out("breakTime: " + breakTime);
		breakSwoosh.setTarget(breakTime);
		breakSwoosh.setForecast(breakTime);
		breakSwoosh.initArrived();
		breakSwoosh.setStarted(true);
		breakSwoosh.setBackground(BACKGROUND);
		breakSwoosh.setWantSubLabel(false);
		Ticker.INSTANCE.register(breakSwoosh, activity);
	}

	private void addLongClick() {
		// L.out("addedLongClick: " + addedLongClick);
		// MyToast.show("Long click is enabled");
		view.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				// L.out("long view: " + view);
				Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(200);

				if (Ticker.INSTANCE.demoMode)
					MyToast.show("Demo Mode Ended");
				else
					MyToast.show("Demo Mode Started");
				Ticker.INSTANCE.demoMode = !Ticker.INSTANCE.demoMode;
				return true;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		visible = true;
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		this.activity = getActivity();
		view = inflater.inflate(R.layout.frag_break, container, false);
		addLongClick();

		breakSwoosh = (Swoosh) view.findViewById(R.id.breakSwoosh);
		// Ticker.INSTANCE.register(breakSwoosh, getActivity());
		// initClock();
		Button shortBreakButton = (Button)
				view.findViewById(R.id.dataButton);
		shortBreakButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShortBreak(v);
			}
		});

		Button lunchButton = (Button)
				view.findViewById(R.id.cancelButton);
		lunchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLunchBreak(v);
			}
		});

		Button finishButton = (Button)
				view.findViewById(R.id.completeBreakButton);
		finishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onFinishBreakClick(v);
			}
		});

		update();
		L.out("created view: ");
		// UpdateController.INSTANCE.registerCallback(this,
		// FlowRestService.GET_ACTOR_STATUS);
		return view;
	}

	private void onFinishBreakClick(View v) {
		setEmployeeStatus(StaticFlow.ACTOR_AVAILABLE, false);
		// ((Cache) getActivity()).updateStatusTitle();
		// ((Cache) activity).update();
		Ticker.INSTANCE.unregister(breakSwoosh);
	}

	private void onShortBreak(View v) {
		L.out("onShortBreak: " + v);
		setEmployeeStatus(StaticFlow.ACTOR_BREAK, false);
		// ((Cache) getActivity()).updateStatusTitle();
		startClock(SHORT_BREAK_TIME);
		// ((Cache) activity).update();
	}

	private void onLunchBreak(View v) {
		L.out("onLunchBreak: " + v);
		setEmployeeStatus(StaticFlow.ACTOR_LUNCH, false);
		// ((Cache) getActivity()).updateStatusTitle();
		startClock(LONG_BREAK_TIME);
		// ((Cache) activity).update();
	}

	@Override
	public String getTitle() {
		return "Not Used";
	}

	@Override
	public void update() {
		L.out("BreakFragment update: " + visible);
		// if (!visible)
		// return;
		if (view == null)
			return;
		LinearLayout takeBreakLayout = (LinearLayout) view.findViewById(R.id.listItemLayout);
		LinearLayout finishBreakLayout = (LinearLayout) view.findViewById(R.id.finishBreakLayout);
		// Button completeButton = (Button)
		// view.findViewById(R.id.twoOneButton);
		// Button shortBreak = (Button) view.findViewById(R.id.oneOneButton);
		// Button lunch = (Button) view.findViewById(R.id.oneThreeButton);
		// ValidateUser user = User.getUser().getValidateUser();
		// StatusWrapper statusWrapper =
		// UpdateController.INSTANCE.statusWrapper;
		// String employeeStatus = statusWrapper.currentStatus.employeeStatus;
		// L.out("statusWrapper: " + statusWrapper);
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (lastStatus != null && lastStatus.equals(getActorStatus.getActorStatusId()))
			return;
		if (getActorStatus != null)
			lastStatus = getActorStatus.getActorStatusId();
		if (getActorStatus == null
				|| getActorStatus.getActorStatusId() == null
				|| getActorStatus.getActorStatusId().equals(StaticFlow.ACTOR_AVAILABLE)) {
			takeBreakLayout.setVisibility(View.VISIBLE);
			finishBreakLayout.setVisibility(View.GONE);
			// completeButton.setVisibility(View.INVISIBLE);
			// // completeButton.setText(SELF_TASK);
			// shortBreak.setVisibility(View.VISIBLE);
			// lunch.setVisibility(View.VISIBLE);
		}
		else {
			// completeButton.setVisibility(View.VISIBLE);
			// // completeButton.setText(FINISH_BREAK);
			// shortBreak.setVisibility(View.INVISIBLE);
			// lunch.setVisibility(View.INVISIBLE);
			takeBreakLayout.setVisibility(View.GONE);
			finishBreakLayout.setVisibility(View.VISIBLE);
			initClock();
		}
	}

	@Override
	public void onPause() {
		L.out("onPause");
		super.onPause();
		paused = true;
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	@Override
	public void onResume() {
		super.onResume();
		L.out("onResume");
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		paused = false;
		update();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// UpdateController.INSTANCE.unRegisterCallback(this,
		// FlowRestService.GET_ACTOR_STATUS);
		// Ticker.INSTANCE.unregister(delaySwoosh);
		Ticker.INSTANCE.unregister(breakSwoosh);
	}

	@Override
	public void callback(GJon gJon, String payloadName) {

		update();
	}
}
