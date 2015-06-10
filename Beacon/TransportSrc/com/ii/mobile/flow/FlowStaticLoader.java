package com.ii.mobile.flow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.util.L;

public class FlowStaticLoader {

	Activity activity;

	//
	// class StaticState {
	// String methodName;
	// Cursor cursor = null;
	// String facilityID = null;
	//
	// public StaticState(String methodName) {
	// this.methodName = methodName;
	// }
	//
	// @Override
	// public String toString() {
	// return "StaticState: " + methodName + " cursor: " + cursor + " " +
	// facilityID;
	// }
	// }

	public FlowStaticLoader(Activity activity) {
		L.out("FlowStaticLoader: " + activity);
		this.activity = activity;

	}

	public synchronized void execute() {
		L.out("execute: " + activity);
		new DownloadFlowStaticTask().execute();
	}

	class DownloadFlowStaticTask extends AsyncTask<Void, Integer, Long> {
		private final long LOADING = 0;
		private final long ERROR = 1;
		private final long LOADED_ALREADY = 2;
		private ProgressDialog progressDialog = null;

		@Override
		protected Long doInBackground(Void... arg0) {
			Thread.currentThread().setName("StaticLoaderThread");
			L.out("doInBackground: ");
			boolean success = UpdateController.INSTANCE.staticLoad();
			L.out("success: " + success);
			if (!success)
				return ERROR;
			return LOADED_ALREADY;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {
			L.out("onPreExecute: " + activity);
			progressDialog = new ProgressDialog(activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("One-time load of flow content ...");
			progressDialog.setCancelable(true);
			progressDialog.show();
			L.out("done onPreExecute: " + activity);
		}

		@Override
		protected void onPostExecute(Long l) {
			L.out("onPostExecute: " + l);
			GetActorStatus getActorStatus = UpdateController.getActorStatus;
			if (getActorStatus == null) {
				MyToast.show("Failed to load getActorStatus", Toast.LENGTH_LONG);
				activity.finish();
				return;
			}
			// if (UpdateController.INSTANCE.statusWrapper == null) {
			// MyToast.show("Failed to load statusWrapper", Toast.LENGTH_LONG);
			// activity.finish();
			// return;
			// }
			if (l == LOADING) {
				MyToast.show("... Loaded static content  ");
			}
			if (l == ERROR) {
				String temp = "Failed loading Static Content!"
						+ "\nYou are welcome to try again"
						+ "\n(just press Enter)."
						+ "\nOr you may wait until"
						+ "\nyou have better WI-FI."
						+ "\n";
				MyToast.show(temp, Toast.LENGTH_LONG);
				MyToast.show(temp, Toast.LENGTH_LONG);
				activity.finish();
				return;
			}
			if (l == LOADED_ALREADY) {
				L.out("updating");
				// UpdateController.INSTANCE.statusWrapper =
				// getActorStatus.inject();
				// L.out("getActorStatus: " + UpdateController.getActorStatus);
				UpdateController.INSTANCE.callback(UpdateController.getActorStatus, FlowRestService.GET_ACTOR_STATUS);
			}
			if (progressDialog != null) {
				try {
					progressDialog.dismiss();
				} catch (Exception e) {
					L.out("dismissed exception: " + e);
				}
			}
		}
	}
}
