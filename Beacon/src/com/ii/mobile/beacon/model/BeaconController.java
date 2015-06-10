package com.ii.mobile.beacon.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;

import com.ii.mobile.flow.FlowBinder;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActionHistory;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId;
import com.ii.mobile.flow.types.SelectLocations;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.home.UserWatcher;
import com.ii.mobile.payload.StatusWrapper;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.util.L;

public enum BeaconController implements SyncCallback {
	INSTANCE;

	public static void receiveEvent() {

	}

	public static final String BLACK = "<font color=#000000>";
	public static final String END = "</font>";

	private final List<Callback> callbacks = new ArrayList<Callback>();

	public ValidateUser validateUser = null;

	public int outputCounter = 0;
	public int inputCounter = 0;

	public Activity activity = null;

	public static StatusWrapper statusWrapper = new StatusWrapper();
	public static BeaconSample beaconSample = null;
	public static String beaconSampleName = "Default Sample";
	public static GetActorStatus getActorStatus = null;
	public static GetActionStatus getActionStatus = null;
	public static GetActionHistory getActionHistory = null;
	public static SelectClassTypesByFacilityId selectClassTypesByFacilityId = null;
	public static SelectLocations selectLocations = null;

	public static Hashtable<String, GetActionStatus> getActionStatusHashtable = new Hashtable<String, GetActionStatus>();

	public void clearStaticLoad() {
		getActorStatus = null;
		selectClassTypesByFacilityId = null;
		selectLocations = null;
		getActionStatus = null;
		getActionHistory = null;
	}

	public synchronized boolean staticLoad(Activity activity) {
		boolean result = staticSurveysLoad(activity);
		UpdateController.INSTANCE.callback(getActorStatus, FlowRestService.GET_ACTOR_STATUS);
		return result;
	}

	public static boolean staticSurveysLoad(Activity activity) {
		if (BeaconController.beaconSample == null) {
			L.out("test: "+FlowBinder.getGJon(FlowRestService.BEACON_SAMPLE, activity));
			BeaconController.beaconSample = (BeaconSample) FlowBinder.getGJon(FlowRestService.BEACON_SAMPLE, activity);
			if (BeaconController.beaconSample == null) {
				// String facilityId =
				// BeaconController.getActorStatus.getActorStatusInner.targets.facilityId;
				// L.out("facilityId: " + facilityId);
				BeaconController.beaconSample = new BeaconSample();
			}
			// L.out("selectLocations: " + selectLocations);
			// if (BeaconController.selectLocations == null)
			// return false;
		}
		return true;
	}

	public void doCallback(final GJon gJon, final String payloadName) {
		if (activity == null) {
			L.out("ERROR - no Activity set for: " + payloadName + L.p());
			return;
		}
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				callbacks(gJon, payloadName);
			}
		});
	}

	private void callbacks(GJon gJon, String payloadName) {
		// L.out("doCallback: " + payloadName + "\n" + gJon);a
		boolean foundCallBack = false;
		if (gJon instanceof GetActorStatus) {
			GetActorStatus getNewActorStatus = (GetActorStatus) gJon;
			// if (!getNewActorStatus.isDifferent(getActorStatus)) {
			// // L.out("ERROR - should of checked already Not different");
			// // return;
			// }
			// L.out("getActorStatus: " + getActorStatus);
			getActorStatus = getNewActorStatus;
		}
		L.out("getActorStatus: " + getActorStatus);
		for (int i = 0; i < callbacks.size(); i++) {
			Callback callback = callbacks.get(i);
			if (callback.callbackName.equals(payloadName)) {
				// L.out("found callbackName: " + payloadName + " " +
				// callback.syncCallBack.getClass());
				callback.syncCallBack.callback(gJon, payloadName);
				foundCallBack = true;
			}
		}
		if (!foundCallBack)
			L.out("ERROR doCallback did not find callbackName: " + payloadName);
	}

	public void registerCallback(SyncCallback syncCallback, String callBackName) {
		// L.out("registerCallback: " + callBackName);
		if (notInCallBackList(syncCallback, callBackName))
			callbacks.add(new Callback(syncCallback, callBackName));
	}

	private boolean notInCallBackList(SyncCallback syncCallback, String callBackName) {
		for (int i = 0; i < callbacks.size(); i++) {
			Callback callback = callbacks.get(i);
			if (callback.syncCallBack == syncCallback
					&& callback.callbackName.equals(callBackName)) {
				// L.out("found callbackName: " + callBackName);
				return false;
			}
		}
		return true;
	}

	public void unRegisterCallback(SyncCallback syncCallback, String callBackName) {
		// L.out("unRegisterCallback: " + callBackName);
		for (int i = 0; i < callbacks.size(); i++) {
			Callback callback = callbacks.get(i);
			if (callback.syncCallBack == syncCallback
					&& callback.callbackName.equals(callBackName)) {
				// L.out("found callbackName: " + callBackName);
				callbacks.remove(i);
			}
		}
		// L.out("ERROR unRegisterCallback did not find callbackName: "
		// + callBackName + " for " + syncCallback.getClass().getSimpleName());
	}

	class Callback {
		String callbackName = null;
		SyncCallback syncCallBack = null;

		Callback(SyncCallback syncCallback, String callBackName) {
			this.callbackName = callBackName;
			this.syncCallBack = syncCallback;
		}
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		if (gJon == null)
			return;
		if (gJon instanceof GetActionStatus) {
			callbackGetActionStatus((GetActionStatus) gJon, payloadName);
			return;
		}

		if (gJon instanceof GetActorStatus) {
			callbackGetActorStatus((GetActorStatus) gJon, payloadName);
			return;
		}

		doCallback(getActorStatus, FlowRestService.GET_ACTOR_STATUS);

		// L.out("actionSelect: " + actionSelect);
	}

	private void callbackGetActionStatus(GetActionStatus gJon, String payloadName) {
		L.out("action called: " + L.p());
	}

	private void callbackGetActorStatus(GetActorStatus getActorStatus, String payloadName) {
		String actionId = getActorStatus.getActionId();
		// L.out("getActorStatus: " + getActorStatus);
		L.out("callbackGetActorStatus actionId: " + getActorStatus.getActionId());
		if (actionId != null) {
			getActionStatus = getActionStatusHashtable.get(actionId);
			if (getActionStatus == null) {
				// MyToast.show("ERROR: unable to find the actionId: " +
				// actionId);
				return;
			}
		} else {
			getActionStatus = null;
		}
		if (getActorStatus.getActorStatusId().equals(StaticFlow.ACTOR_NOT_IN)) {
			MyToast.show("Logged out by Dispatch!");
			UserWatcher.INSTANCE.stop();
			activity.finish();
		} else
			doCallback(getActorStatus, FlowRestService.GET_ACTOR_STATUS);
	}

	public static void putActionStatus(GetActionStatus getActionStatus) {
		L.out("putActionHashtable: " + getActionStatus.getActionId());
		if (getActionStatusHashtable.get(getActionStatus.getActionId()) != null) {
			L.out("getActionStatus.getActionId() already in table: " + getActionStatus.getActionId());
			return;
		}
		// if (getActionStatus.getLocalActionId() == null)
		// getActionStatus.setLocalActionId(getActionStatus.getActionId());
		getActionStatusHashtable.put(getActionStatus.getActionId(), getActionStatus);
		if (getActionHistory != null)
			getActionHistory.addAction(getActionStatus);
	}

	public static void removeActionStatus(String actionId) {
		L.out("removeActionStatus: " + actionId);
		L.out("getActionStatusHashtable: ");
		getActionStatusHashtable.remove(actionId);
	}

	public static GetActionStatus getActionStatus(String actionId) {
		L.out("getActionHashtable: " + actionId);
		return getActionStatusHashtable.get(actionId);
	}
}
