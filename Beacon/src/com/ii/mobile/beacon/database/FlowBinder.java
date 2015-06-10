package com.ii.mobile.beacon.database;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.ii.mobile.beacon.model.BeaconController;
import com.ii.mobile.beacon.model.BeaconSample;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.db.StaticFlowProvider;
import com.ii.mobile.flow.db.StaticFlowProvider.StaticFlowColumns;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public enum FlowBinder {
	INSTANCE;

	public static Activity activity;

	synchronized public static GJon getGJon(String methodName, Activity activity) {
		L.out("methodName: " + methodName);
		if (methodName.equals(FlowRestService.BEACON_SAMPLE)) {
			// String userName = Login.userName;
			String beaconSampleName = BeaconController.beaconSampleName;
			String json = getJSon(activity, methodName, beaconSampleName, null, null);
			if (json == null)
				return null;
			GJon gJon = BeaconSample.getGJon(json);
			if (gJon == null)
				L.out("ERROR: getGJon failed to bind json: " + json);
			// L.out("BeaconSample: " + gJon);
			return gJon;
		}

		L.out("ERROR: getGJon cannot find methodName: " + methodName);
		return null;
	}

	public static String getJSon(Activity activity, String methodName, String employeeId,
			String facilityId, String actionId) {
		L.out("methodName: " + methodName);
		// User user = User.getUser();
		// L.out("user: " + user);
		// String facilityId = user.getFacilityID();
		// String employeeId = user.getEmployeeID();

		Intent intent = activity.getIntent();
		String[] selectionArgs = new String[] { employeeId, facilityId, actionId };
		intent.setData(Uri.parse("content://" + StaticFlowProvider.AUTHORITY + "/" +
				methodName));
		Cursor cursor = activity.managedQuery(activity.getIntent().getData(), null, null, selectionArgs, null);
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				int index = cursor.getColumnIndex(StaticFlowColumns.JSON);
				// L.out("index: " + index + " " + cursor.getClass());
				String json = cursor.getString(cursor.getColumnIndex(StaticFlowColumns.JSON));
				if (json != null) {
					// L.out("json: " + json.length());
				}
				else
					L.out("ERROR: Json is null for methodName: " + methodName);
				return json;

			} while (cursor.moveToNext());
		}
		return null;
	}

	public static String[] getSurveys(Activity activity) {

		// User user = User.getUser();
		// L.out("user: " + user);
		// String facilityId = user.getFacilityID();
		// String employeeId = user.getEmployeeID();
		String methodName = FlowRestService.BEACON_SAMPLE;
		Intent intent = activity.getIntent();
		List<String> surveys = new ArrayList<String>();
		// String[] selectionArgs = new String[] { null, null, null };
		intent.setData(Uri.parse("content://" + StaticFlowProvider.AUTHORITY + "/" +
				methodName));
		// Cursor cursor = activity.managedQuery(activity.getIntent().getData(),
		// null, null, selectionArgs, null);
		FlowDbAdapter flowDbAdapter = FlowDbAdapter.getFlowDbAdapter();
		if (FlowDbAdapter.getDB() == null) {
			L.out("creating flowDbAdapter!");

			flowDbAdapter = new FlowDbAdapter(activity);
		}
		L.out("creating getDB: " + FlowDbAdapter.getDB());
		String where = FlowDbAdapter.getWhere(methodName, null, null, null);
		Cursor cursor = FlowDbAdapter.getDB().query(flowDbAdapter.getTableName(), null, where, null, null, null, null);
		L.out("cursor: " + cursor);
		if (cursor != null && cursor.getCount() > 0) {
			L.out("cursor.getCount: " + cursor.getCount());
			cursor.moveToFirst();
			do {
				// int index = cursor.getColumnIndex(StaticFlowColumns.JSON);
				// L.out("index: " + index + " " + cursor.getClass());
				String actorId = cursor.getString(cursor.getColumnIndex(StaticFlowColumns.ACTOR_ID));
				L.out("actorId: " + actorId);
				surveys.add(actorId);
			} while (cursor.moveToNext());
		}
		// L.out("surveys.toArray(): " + surveys.toArray().length);
		return toStringArray(surveys.toArray());

	}

	private static String[] toStringArray(Object[] objects) {
		String[] temp = new String[objects.length];
		for (int i = 0; i < objects.length; i++)
			temp[i] = (String) objects[i];
		return temp;
	}

	public static void updateLocalDatabase(String methodName, GJon gJon) {
		updateLocalDatabase(methodName, gJon, null);
	}

	private static final String DEFAULT_FACILITY_ID = "defaultFacilityId";
	private static final String DEFAULT_ACTOR_ID = "defaultActorId";
	private static final String DEFAULT_ACTION_ID = "defaultActionId";

	public static void updateLocalDatabase(String methodName, GJon gJon, String localActionNumber) {
		L.out("methodName: " + methodName);

		String facilityId = DEFAULT_FACILITY_ID;
		String actorId = DEFAULT_ACTOR_ID;
		String actionId = DEFAULT_ACTION_ID;
		if (localActionNumber != null)
			actionId = localActionNumber;

		if (!methodName.equals(FlowRestService.GET_ACTION_STATUS)) {
			actionId = null;
			actorId = BeaconController.beaconSampleName;
			gJon.tickled = GJon.TRUE_STRING;
		}

		ContentValues values = new ContentValues();
		values.put(StaticFlowColumns.JSON, gJon.getNewJson());
		values.put(StaticFlowColumns.FACILITY_ID, facilityId);
		values.put(StaticFlowColumns.ACTOR_ID, actorId);
		values.put(StaticFlowColumns.ACTION_ID, actionId);
		values.put(StaticFlowColumns.FLOW_METHOD, methodName);
		if (gJon.tickled != null && gJon.tickled.equals(GJon.FALSE_STRING))
			values.put(AbstractDbAdapter.TICKLED, GJon.FALSE_STRING);
		// if (localActionNumber != null)
		// values.put(StaticFlowColumns.LOCAL_ACTION_NUMBER, localActionNumber);
		String[] selectionArgs = new String[] { actorId, facilityId, localActionNumber };
		Intent intent = activity.getIntent();
		intent.setData(Uri.parse("content://" + StaticFlowProvider.AUTHORITY + "/" + methodName));
		activity.getContentResolver().update(intent.getData(), values, FlowDbAdapter.getWhere(methodName, actorId, facilityId, actionId), selectionArgs);
	}

	// public static void deleteLocalDatabase(String methodName) {
	// L.out("methodName: " + methodName);
	//
	// GetActorStatus getActorStatus = UpdateController.getActorStatus;
	// if (getActorStatus == null) {
	// L.out("ERROR: Unable to delete, getActorStatus is null for methodName: "
	// + methodName);
	// return;
	// }
	//
	// String facilityId =
	// getActorStatus.getActorStatusInner.targets.facilityId;
	// String actorId = getActorStatus.getActorStatusInner.targets.actor_id;
	//
	// String[] selectionArgs = new String[] { actorId, null, methodName };
	// Intent intent = activity.getIntent();
	// intent.setData(Uri.parse("content://" + StaticFlowProvider.AUTHORITY +
	// "/" + methodName));
	// activity.getContentResolver().delete(intent.getData(),
	// FlowDbAdapter.getWhere(methodName, actorId, facilityId, null),
	// selectionArgs);
	// }
}
