package com.ii.mobile.legacy;

import java.util.Hashtable;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.ii.mobile.cache.Cache;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListRecentTasksByEmployeeID.EmployeeRecentTasksList;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class LegacyTaskLoader {

	private final Activity activity;
	private final ListAdapter listAdapter;
	private static Thread currentThread = null;

	private static boolean loadedTasks = false;
	private static String lastEmployeeID = "";

	private static final Hashtable<String, GetTaskInformationByTaskNumberAndFacilityID> taskHashtable =
			new Hashtable<String, GetTaskInformationByTaskNumberAndFacilityID>();

	public LegacyTaskLoader(FragmentActivity activity, ListAdapter listAdapter) {
		this.activity = activity;
		this.listAdapter = listAdapter;
		String employeeID = User.getUser().getValidateUser().getEmployeeID();
		if (currentThread == null || !employeeID.equals(lastEmployeeID)) {
			loadedTasks = true;
			if (currentThread != null) {
				// L.out("stopping: " + currentThread);
				// if (currentThread.isAlive())
				// currentThread.stop();
				currentThread = null;
			}

			new LoadAllTasks().execute();
		}
	}

	class UpdateListView extends AsyncTask<Void, Integer, Long> {
		@Override
		protected void onPostExecute(Long l) {
			((ArrayAdapter<?>) listAdapter).notifyDataSetChanged();
		}

		@Override
		protected Long doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	class LoadAllTasks extends AsyncTask<Void, Integer, Long> {
		private static final int SLEEP_TIME = 2000;
		private static final int TASK_SLEEP_TIME = 50;
		private static final int MAX_ATTEMPTS = 1;
		private final long LOADING = 0;
		private final long ERROR = 1;
		private final long LOADED_ALREADY = 2;

		@Override
		protected Long doInBackground(Void... arg0) {
			// L.out("starting: " + currentThread);
			Thread.currentThread().setName("LoadAllTasks");
			currentThread = Thread.currentThread();
			long count = LOADING;
			EmployeeRecentTasksList[] recentTasks = getRecentTasks();
			loadTasks(recentTasks);
			// MyToast.show("...Loaded static data");
			currentThread = null;
			return count;
		}

		private void loadTasks(EmployeeRecentTasksList[] recentTasks) {
			int attempt = 0;
			// L.out("loadTasks: " + recentTasks.length);
			boolean loaded = true;
			int counter = 0;
			while (true) {
				for (EmployeeRecentTasksList recentTask : recentTasks) {
					String taskNumber = recentTask.getTaskNumber();
					// L.out(counter + " - taskNumber: " + taskNumber);
					counter += 1;
					if (taskHashtable.get(taskNumber) == null) {
						GetTaskInformationByTaskNumberAndFacilityID task = ((Cache) activity).getTask(taskNumber);
						if (task == null) {
							loaded = false;
							// L.out("failed on task: " + taskNumber);
						} else {
							// L.out(counter + " loaded task: " +
							// task.getTaskNumber());
							taskHashtable.put(task.getTaskNumber(), task);
							new UpdateListView().execute();
						}
					}
					L.sleep(TASK_SLEEP_TIME);
				}
				// L.out("getRecentTasks: " + attempt + " " + " loaded: " +
				// loaded + " tasks: "
				// + recentTasks.length);
				attempt += 1;
				L.sleep(SLEEP_TIME);
				if (loaded || attempt == MAX_ATTEMPTS)
					return;
			}
		}

		private EmployeeRecentTasksList[] getRecentTasks() {
			int attempt = 0;
			while (true) {
				EmployeeRecentTasksList[] recentTasks = ((Cache) activity).getEmployeeRecentTasksList();
				if (recentTasks != null)
					return recentTasks;
				// L.out("getRecentTasks: " + attempt);
				attempt += 1;
				L.sleep(SLEEP_TIME);
			}
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
			if (l == LOADING) {
				// MyToast.show("... Loaded static content  ");
			}
			if (l == ERROR) {
				MyToast.show("...Failed loading Static Content",
						Toast.LENGTH_SHORT);
			}
			if (l == LOADED_ALREADY) {
				// MyToast.show("already loaded");
			}

		}
	}

	public static GetTaskInformationByTaskNumberAndFacilityID getTask(String taskNumber) {
		// L.out("taskNumber: " + taskNumber);
		return taskHashtable.get(taskNumber);
	}

	public static void addTask(GetTaskInformationByTaskNumberAndFacilityID task) {
		taskHashtable.put(task.getTaskNumber(), task);
	}
}
