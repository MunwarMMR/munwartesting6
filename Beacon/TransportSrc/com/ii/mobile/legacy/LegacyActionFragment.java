package com.ii.mobile.legacy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ii.mobile.beacon.R;
import com.ii.mobile.actionView.ActionViewFragment;
import com.ii.mobile.cache.Cache;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.payload.StatusWrapper;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListDelayTypes.TaskDelayType;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.timers.Swoosh;
import com.ii.mobile.timers.Ticker;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

/**

 */
public class LegacyActionFragment extends LegacyDataFragment implements NamedFragment, SyncCallback {
	public final static String FRAGMENT_TAG = "actionFragment";
	// private static final String DELAY_START_TEXT = "Start";
	// private static final String DELAY_DELAY_TEXT = "Delay";
	private static final String START_TASK = "Start Action";
	private static final String COMPLETE_TASK = "Complete To Available";
	// private static final String FINISH_DELAY = "Finish Delay";

	private static final int DELAY_TIME = 5 * 60;
	private static final String DELAY_NAME = "Delayed";

	public final String[] completeStates = new String[] { LegacyBreakFragment.AVAILABLE,
			LegacyBreakFragment.ON_BREAK,
			LegacyBreakFragment.AT_LUNCH, LegacyBreakFragment.NOT_IN };
	private View view;
	protected FragmentActivity activity = null;
	protected Swoosh delaySwoosh;

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();
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
		view = inflater.inflate(R.layout.frag_actions, container, false);

		delaySwoosh = (Swoosh) view.findViewById(R.id.delaySwoosh);

		final Button delayButton = (Button) view.findViewById(R.id.actionDelayButton);
		delayButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onDelayClick(v);
			}
		});

		final Button completeOtherButton = (Button) view.findViewById(R.id.actionCompleteOtherButton);
		completeOtherButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onCompleteOtherClick(v);
			}
		});

		final Button completeButton = (Button) view.findViewById(R.id.actionCompleteButton);
		completeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (completeButton.getText().equals(START_TASK)) {
					onStartClick(v);
				} else
					onCompleteClick(v);
			}
		});

		final Button cancellationButton = (Button) view.findViewById(R.id.actionRequestCancellationButton);
		cancellationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onCancellationClick(v);
			}
		});

		final Button completeDelayButton = (Button) view.findViewById(R.id.actionCompleteDelayButton);
		completeDelayButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				onStartClick(v);
			}
		});
		L.out("registered actionFragment: ");
		UpdateController.INSTANCE.registerCallback(this, UpdateController.STATUS_WRAPPER);
		update();
		return view;
	}

	//
	//
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// UpdateController.INSTANCE.registerCallback(this,
	// UpdateController.STATUS_WRAPPER);
	// }
	//
	//
	// public void onDetach() {
	// super.onDetach();
	// UpdateController.INSTANCE.unRegisterCallback(this,
	// UpdateController.STATUS_WRAPPER);
	// }

	private String[] getDisplayItems(TaskDelayType[] taskDelayType) {
		String[] items = new String[taskDelayType.length];
		for (int i = 0; i < taskDelayType.length; i++) {
			items[i] = taskDelayType[i].taskDelay;
			// L.out(i + " item: " + items[i]);
		}
		return items;
	}

	public void onCompleteOtherClick(View w) {
		final ArrayAdapter<String> aa = new ArrayAdapter<String>(getActivity(), R.layout.list_item, completeStates);
		aa.setDropDownViewResource(R.layout.list_item);
		new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.completeOther))
				.setAdapter(aa, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						L.out("yahoo: " + which);
						// TODO: user specific action
						String completeTo = aa.getItem(which);
						setEmployeeStatus(LegacyBreakFragment.AVAILABLE, activity, false);
						setTaskStatus(ActionViewFragment.COMPLETED, completeTo, false);
						((Cache) activity).updateStatusTitle();
						dialog.dismiss();
					}
				}).create().show();
	}

	public void onStartClick(View w) {
		// MyToast.show("start it");
		setTaskStatus(ActionViewFragment.ACTIVE, null, false);
		// ((Cache) activity).updateStatusTitle();
		// ((Cache) activity).startTask();
		update();
	}

	public void onDelayClick(View w) {
		final ArrayAdapter<String> arrayAdapter =
				new ArrayAdapter<String>(getActivity(), R.layout.list_item, getDisplayItems(((Cache) activity).getTaskDelayType()));
		arrayAdapter.setDropDownViewResource(R.layout.list_item);
		new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.delayReason))
				.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						L.out("yahoo: " + which);
						GetTaskInformationByTaskNumberAndFacilityID task = ((Cache) activity).getTask();
						String delayReason = arrayAdapter.getItem(which);
						L.out("delayReason: " + delayReason);
						String delayID = lookupTaskDelayType(delayReason);
						L.out("delayID: " + delayID);
						// task.setDelayType(delayID);
						// User.getUser().getValidateUser().setTaskStatus(TaskFragment.DELAYED);
						// L.out("User.getUser().getValidateUser().getTaskStatus: "
						// + User.getUser().getValidateUser().getTaskStatus());
						setTaskStatus(ActionViewFragment.DELAYED, null, false);
						// ((Cache) activity).updateStatusTitle();
						update();
						dialog.dismiss();
					}
				}).create().show();
	}

	private String lookupTaskDelayType(String key) {
		TaskDelayType[] taskDelayTypes = ((Cache) activity).getTaskDelayType();
		if (taskDelayTypes == null)
			return "No DelayTypes: " + key;
		for (int i = 0; i < taskDelayTypes.length; i++)
			if (taskDelayTypes[i].taskDelay.equals(key))
				return taskDelayTypes[i].taskDelayID;
		return "DelayType not found: " + key;
	}

	private void onCompleteClick(View v) {
		L.out("onCompleteClick: " + v);
		ValidateUser validateUser = User.getUser().getValidateUser();
		validateUser.setTaskNumber(null);
		setEmployeeStatus(LegacyBreakFragment.AVAILABLE, activity, false);
		setTaskStatus(ActionViewFragment.COMPLETED, LegacyBreakFragment.AVAILABLE, false);
		((Cache) activity).setTask(null);
		((Cache) activity).updateStatusTitle();
	}

	private void onCancellationClick(View v) {
		L.out("onCancellationClick: " + v);
	}

	@Override
	public String getTitle() {
		return "Action Home";
	}

	private void setTaskStatus(String status, String completeTo, boolean tickled) {
		L.out("setTaskStatus: " + status + " " + tickled + L.p());
		final String actionId = StaticFlow.INSTANCE.findActorStatusId(status);
		final StatusWrapper statusWrapper = UpdateController.INSTANCE.statusWrapper;
		L.out("old statusWrapper: " + statusWrapper);
		statusWrapper.currentStatus.taskStatus = status;
		// statusWrapper = UpdateController.INSTANCE.statusWrapper;
		// L.out("statusWrapper: " + statusWrapper);
		L.out("actionId: " + actionId);
		statusWrapper.currentStatus.actionStatusId = StaticFlow.INSTANCE.findActorStatusId(status);
		L.out("actionStatusId: " + statusWrapper.currentStatus.actionStatusId);
		L.out("new statusWrapper: " + statusWrapper);
		UpdateController.INSTANCE.callback(statusWrapper, UpdateController.STATUS_WRAPPER);
		// new Thread() {
		//
		// @Override
		// public void run() {
		// boolean result = Flow.getFlow().actionStatusUpdate(statusWrapper);
		// // L.out("result: " + result);
		// }
		// }.start();
	}

	// public void setTaskStatusold(String status, String completeTo, boolean
	// tickled) {
	// GetTaskInformationByTaskNumberAndFacilityID task = ((Cache)
	// activity).getTask();
	// if (task != null) {
	// String oldStatus = task.getTaskStatusBrief();
	// task.setTaskStatusBrief(status);
	// task.setTickled(tickled);
	// // MyToast.show("status: " + oldStatus + " to " + status);
	// L.out("status: " + oldStatus + " to " + status);
	// if (status != null && status.equals(oldStatus)) {
	// L.out("Ignoring status change since equal: " + status);
	// return;
	// }
	// // L.out("**** originally: ");
	// // getTaskInformationByTaskNumberAndFacilityID.printJson();
	// // L.out("**** new json: ");
	// if (status.equals(TaskFragment.COMPLETED)) {
	// L.out("completeTo: " + completeTo);
	// task.setCompleteTo(StatusType.lookUp(completeTo));
	// ValidateUser validateUser = User.getUser().getValidateUser();
	// validateUser.setTaskNumber(null);
	//
	// ((Cache) activity).setTask(null);
	// setEmployeeStatus(completeTo, activity, true);
	// // task = null;
	// } else if (status.equals(TaskFragment.ASSIGNED)) {
	// L.out("assigned: " + oldStatus);
	// task.setTaskStatusBrief(oldStatus);
	// task.setTickled(false);
	// }
	// task.setJson(task.getNewJson());
	// // getTaskInformationByTaskNumberAndFacilityID.printJson();s
	// // L.out(getTaskInformationByTaskNumberAndFacilityID + "");
	// // L.out("*** update the server here! " + status);
	//
	// updateDataModel(task, activity, false);
	// }
	// // if (getTaskStatus() != null &&
	// // getTaskStatus().equals(TaskFragment.COMPLETED)) {
	// // // deleteCurrentTask();
	// // stopTimer();
	// // task = null;
	// // // setEmployeeStatus(BreakActivity.AVAILABLE, false, this);
	// // }
	// // if (status.equals(TaskFragment.COMPLETED))
	// // ((Cache) activity).setTask(null);
	// // else {
	// // L.out("just resetting task!");
	// // ((Cache) activity).setTask(null);
	// // }
	//
	// if (status.equals(TaskFragment.COMPLETED) &&
	// completeTo.equals(BreakFragment.NOT_IN)) {
	// // setEmployeeStatus(completeTo, true);
	// // updateDataModel(task, false);
	// // ((Cache) getActivity()).setTask(null);
	// // already saved but make sure don't have task for login check
	// L.out("taskactivity called");
	// // User.getUser().setNeedLogout(true);
	// // TaskActivity.setEmployeeStatus(BreakActivity.NOT_IN, true,
	// // taskActivity);
	// Intent intent = new Intent().setClass(activity, FragLoginActivity.class);
	// activity.startActivity(intent);
	// }
	// }

	public void updateold() {
		L.out("update: " + getActivity());
		if (getActivity() == null)
			return;
		GetTaskInformationByTaskNumberAndFacilityID task = ((Cache) getActivity()).getTask();
		if (task == null) {
			MyToast.show("Task is null!");
			return;
		}

		Button completeButton = (Button) view.findViewById(R.id.actionCompleteButton);

		if (task.getTaskStatusBrief().equals(ActionViewFragment.DELAYED)) {
			((LinearLayout) view.findViewById(R.id.actionLayout)).setVisibility(View.INVISIBLE);
			((LinearLayout) view.findViewById(R.id.finishDelayLayout)).setVisibility(View.VISIBLE);
			if (!delaySwoosh.started) {
				startClock();
			}

		} else {
			delaySwoosh.setStarted(false);
			((LinearLayout) view.findViewById(R.id.actionLayout)).setVisibility(View.VISIBLE);
			((LinearLayout) view.findViewById(R.id.finishDelayLayout)).setVisibility(View.INVISIBLE);
			if (task.getTaskStatusBrief().equals(ActionViewFragment.ASSIGNED)) {
				completeButton.setText(START_TASK);
			} else {
				completeButton.setText(COMPLETE_TASK);
			}
		}
	}

	@Override
	public void update() {
		// L.out("update: " + getActivity());
		if (getActivity() == null)
			return;
		StatusWrapper statusWrapper = UpdateController.INSTANCE.statusWrapper;
		if (statusWrapper == null || statusWrapper.currentStatus.taskStatus == null) {
			MyToast.show("statusWrapper is null!");
			return;
		}

		Button completeButton = (Button) view.findViewById(R.id.actionCompleteButton);

		if (statusWrapper.currentStatus.taskStatus.equals(ActionViewFragment.DELAYED)) {
			((LinearLayout) view.findViewById(R.id.actionLayout)).setVisibility(View.INVISIBLE);
			((LinearLayout) view.findViewById(R.id.finishDelayLayout)).setVisibility(View.VISIBLE);
			if (!delaySwoosh.started) {
				startClock();
			}

		} else {
			delaySwoosh.setStarted(false);
			((LinearLayout) view.findViewById(R.id.actionLayout)).setVisibility(View.VISIBLE);
			((LinearLayout) view.findViewById(R.id.finishDelayLayout)).setVisibility(View.INVISIBLE);
			if (statusWrapper.currentStatus.taskStatus.equals(ActionViewFragment.ASSIGNED)) {
				completeButton.setText(START_TASK);
			} else {
				completeButton.setText(COMPLETE_TASK);
			}
		}
	}

	public void startClock() {
		// TODO Auto-generated method stub
		L.out("DELAY_TIME: " + DELAY_TIME);
		delaySwoosh.setTitle(DELAY_NAME);
		delaySwoosh.setTarget(DELAY_TIME);
		delaySwoosh.setStarted(true);
		delaySwoosh.setForecast(DELAY_TIME);
		delaySwoosh.initArrived();
		delaySwoosh.setBackground(LegacyBreakFragment.BACKGROUND);
		delaySwoosh.setWantSubLabel(false);
		Ticker.INSTANCE.register(delaySwoosh, activity);
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		UpdateController.INSTANCE.unRegisterCallback(this, UpdateController.STATUS_WRAPPER);
		Ticker.INSTANCE.unregister(delaySwoosh);
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		StatusWrapper statusWrapper = (StatusWrapper) gJon;
		L.out("statusWrapper: " + statusWrapper.getClass().getSimpleName());
		update();
	}

	@Override
	public boolean wantActions() {
		return true;
	}
}
