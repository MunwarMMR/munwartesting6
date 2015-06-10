package com.ii.mobile.oldcontroller;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;

import com.ii.mobile.cache.Cache;
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.StaticSoap;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.tab.BreakActivity;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class FragTaskController extends BaseController {

	private static FragTaskController fragTaskController = null;

	// private GetTaskInformationByTaskNumberAndFacilityID task = null;
	// private final TaskFragment taskFragment;

	// private final FragmentActivity activity;

	public static FragTaskController getFragTaskController(Cache cache) {
		if (fragTaskController != null)
			return fragTaskController;
		fragTaskController = new FragTaskController(cache);
		return fragTaskController;
	}

	public FragTaskController(Cache cache) {
		super(cache);
		// this.taskFragment = (TaskFragment) fragment;
		// this.activity = fragment.getActivity();
		// if (checkForExistingTask() != null) {
		// // taskFragment.startTimer(Cache.ASSIGNED_TIMER);
		// // taskFragment.startTimer(Cache.ACTIVE_TIMER);
		// }

		Uri uri = Uri.parse("content://" + StaticSoap.AUTHORITY + "/" +
				ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID);
		L.out("uri: " + uri);
		activity.getApplicationContext().getContentResolver().registerContentObserver(uri, true, new MyContentObserver());
	}

	// private GetTaskInformationByTaskNumberAndFacilityID
	// checkForExistingTask() {
	// ValidateUser validateUser = User.getUser().getValidateUser();
	// if (validateUser != null)
	// if (validateUser.getTaskNumber() != null) {
	// GetTaskInformationByTaskNumberAndFacilityID task = createExistingTask();
	// return task;
	// }
	// return null;
	// }
	//
	// private GetTaskInformationByTaskNumberAndFacilityID createExistingTask()
	// {
	// ValidateUser validateUser = User.getUser().getValidateUser();
	// String json = null;
	// GetTaskInformationByTaskNumberAndFacilityID task = null;
	// if (validateUser.getTaskNumber() != null &&
	// validateUser.getTaskNumber().length() > 1)
	// json =
	// LoginActivity.getJSon(ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID,
	// activity, validateUser.getTaskNumber());
	// if (json != null) {
	// task = GetTaskInformationByTaskNumberAndFacilityID.getGJon(json);
	// task.setMobileUserName(validateUser.getMobileUserName());
	// task.setTickled(true);
	// validateUser.setTaskNumber(task.getTaskNumber());
	// L.out("task.getTaskNumber(): " + task.getTaskNumber());
	// return task;
	// }
	// return null;
	// }

	private class MyContentObserver extends ContentObserver {

		public MyContentObserver() {
			super(null);
		}

		// @Override
		public void onChangeold(boolean selfChange) {
			super.onChange(selfChange);
			L.out("*** Received change: " + selfChange);
			if (selfChange)
				return;
			ValidateUser validateUser = User.getUser().getValidateUser();
			if (validateUser.getEmployeeStatus().equals(BreakActivity.NOT_IN)) {
				new PerformLogout().execute();
			}
			GetTaskInformationByTaskNumberAndFacilityID task = cache.getTask();

			if (task != null) {
				if (!task.getTaskNumber().equals(validateUser.getTaskNumber())) {
					L.out("*** ERROR have taskNumber and getting new one: " + task.getTaskNumber()
							+ " and new:" + validateUser.getTaskNumber());
					if (validateUser.getTaskNumber() == null
							&& validateUser.getEmployeeStatus().equals(BreakActivity.AVAILABLE)) {
						if (task != null && L.getLong(task.getTaskNumber()) == 0) {
							L.out("*** TASK WAS CANCELLED!");
							task = null;
						}
					}
				}
			}
			// new UpdateView().execute(checkForExistingTask());
		}

	}

	// need this for UI thread
	class UpdateView extends AsyncTask<GetTaskInformationByTaskNumberAndFacilityID, Long, Long> {

		private GetTaskInformationByTaskNumberAndFacilityID task;

		@Override
		protected Long doInBackground(GetTaskInformationByTaskNumberAndFacilityID... params) {
			task = params[0];
			// L.out("task: " + task);
			return 0l;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Long l) {
			// if (task != null)
			cache.setTask(task);
			// L.out("TaskListFragment.taskListFragment: " +
			// ActionHistoryFragment.taskListFragment);
			// if (ActionHistoryFragment.taskListFragment != null) {
			// ActionHistoryFragment.taskListFragment.addTask(task);
			// }
		}
	}

	class PerformLogout extends AsyncTask<Integer, Integer, Long> {

		@Override
		protected Long doInBackground(Integer... params) {
			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Long l) {
			ValidateUser validateUser = User.getUser().getValidateUser();
			validateUser.setTaskNumber(null);
			Intent intent = new Intent().setClass(activity, LoginActivity.loginActivity.getClass());
			activity.startActivity(intent);
		}
	}
}
