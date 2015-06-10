/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.beacon.database;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

import com.ii.mobile.flow.db.StaticFlowProvider.StaticFlowColumns;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.SecurityUtils;

public class FlowDbAdapter extends AbstractDbAdapter {

	public final static boolean WANT_SECURITY = true;
	public final static boolean WANT_SECURITY_DEBUG = false;
	public static FlowDbAdapter flowDbAdapter = null;

	public FlowDbAdapter(Context context) {
		super(context);
		setTableName(TABLE_FLOW);
		L.out("created: " + this);
		flowDbAdapter = this;
	}

	public static FlowDbAdapter getFlowDbAdapter() {
		return flowDbAdapter;
	}

	public long create(String flowMethod, String json, String employeeID, String facilityID, String actionId) {
		ContentValues values = new ContentValues();
		values.put(StaticFlowColumns.FLOW_METHOD, flowMethod);
		values.put(StaticFlowColumns.ACTOR_ID, employeeID);
		values.put(StaticFlowColumns.FACILITY_ID, facilityID);
		values.put(StaticFlowColumns.ACTION_ID, actionId);
		L.out("values: " + values);
		if (WANT_SECURITY) {
			out("before create json: " + json + " user: " + User.getUser());
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			values.put(StaticFlowColumns.JSON, json);
			out("after create json: " + json);
			if (WANT_SECURITY_DEBUG)
				out("test : " + SecurityUtils.decryptAES(User.getUser().getPassword(), json));
		}
		long result = getDB().insert(getTableName(), null, values);
		L.out("result: " + result);

		// showEvents(getTableName());
		return result;
	}

	public static void out(String output) {
		if (WANT_SECURITY_DEBUG)
			L.out(output);
	}

	public static String getWhere(String flowMethod, String employeeID, String facilityID, String actionId) {
		String temp = StaticFlowColumns.FLOW_METHOD + "='" + flowMethod + "'";
		if (facilityID != null)
			temp += " AND " + StaticFlowColumns.FACILITY_ID + "='" + facilityID + "'";
		if (employeeID != null)
			temp += " AND " + StaticFlowColumns.ACTOR_ID + "='" + employeeID + "'";
		if (actionId != null)
			temp += " AND " + StaticFlowColumns.ACTION_ID + "='" + actionId + "'";
		L.out("where: " + temp);
		return temp;
	}

	public List<GJon> parse(Uri uri, String actorId, String facilityId, String actionId) {
		L.out("parse uri: " + uri);
		// showEvents(getTableName());
		String where = null;

		String flowMethod = uri.getPathSegments().get(0);
		if (uri != null)
			where = getWhere(flowMethod, actorId, facilityId, actionId);

		Cursor cursor = getDB().query(getTableName(), null, where, null, null, null, null);
		L.out("cursor: " + cursor.getCount());

		List<GJon> flowList = null;
		if (cursor.getCount() != 0) {
			flowList = getFromDatabase(cursor, actorId, facilityId, actionId);
			if (flowList != null)
				return flowList;
			L.out("failed to decrypt employeeID: " + actorId);
		}
		return null;
		// return getFromServer(cursor, uri, actorId, facilityID, actionId);
	}

	private List<GJon> getFromDatabase(Cursor cursor, String actorId, String facilityId, String actionId) {

		List<GJon> flowList = new ArrayList<GJon>();
		cursor.moveToFirst();
		do {
			String json = cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.JSON));
			if (WANT_SECURITY) {
				out("parse json: " + json);
				json = SecurityUtils.decryptAES(User.getUser().getPassword(), json);
				out("uncompressed: " + json);
				if (json == null) {
					L.out("cannot decrypt with: " + User.getUser().getPassword());
					return null;
				}
			}

			GJon gJon = null;
			gJon = new GJon(actorId, facilityId, actionId, json);
			flowList.add(gJon);
			// return only one
			return flowList;
		} while (cursor.moveToNext());
	}

	// private List<GJon> getFromServer(Cursor cursor, Uri uri, String actorId,
	// String facilityId,
	// String actionId) {
	// String flowMethod = uri.getPathSegments().get(0);
	// if (FlowUploader.isConnectedToInternet()) {
	// // MyToast.show("Loading static flow...");
	// List<GJon> flowList = new ParsingSoap().build(uri, actorId, facilityId,
	// actionId);
	//
	// if (flowList == null)
	// return null;
	// if (flowList != null) {
	// // L.out("SoapList: " + soapList.size());
	// // put in db
	// for (GJon gjon : flowList) {
	// // need the employeeID and facilityID
	//
	// if (gjon == null) {
	// MyToast.show("Failed to load JSON for: " + flowMethod +
	// " and facilityID: "
	// + facilityId);
	// } else {
	// String json = gjon.getJson();
	// String password = User.getUser().getPassword();
	// L.out("password: " + password);
	//
	// create(flowMethod, json, actorId, facilityId, actionId);
	// }
	// }
	// // MyToast.show("...Loaded " + L.getPlural(flowList.size(),
	// // "Flow"));
	// // showEvents(getTableName());
	// // L.out("flowList: " + flowList);
	// return flowList;
	// }
	// }
	// // failure - no network!
	// // Toast.makeText(context, "No network available for downloading Soap: "
	// // + soapMethod, Toast.LENGTH_SHORT);
	// L.out("no network available for downloading: " + flowMethod);
	// return null;
	// }

	/**
	 * Return a Cursor positioned at the route that matches the given rowId
	 * 
	 * @param rowId
	 *            id of route to retrieve
	 * @return Cursor positioned to matching route, if found
	 * @throws SQLException
	 *             if route could not be found/retrieved
	 */
	@Override
	public Cursor fetch(long rowId) throws SQLException {
		L.out("rowId: " + rowId);
		Cursor mCursor =
				getDB().query(getTableName(), null, null, null, null, null,
						StaticFlowColumns.DEFAULT_SORT_ORDER);
		L.out("mCursor: " + mCursor);
		if (mCursor != null) {
			L.out("rows: " + mCursor.getCount());
		}
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	@Override
	public long update(ContentValues values, String where, boolean leaveTimeStamp) {
		L.out("update leaveTimeStamp: " + leaveTimeStamp);
		// values.remove(TICKLED);
		// L.out("values: " + values);
		L.out("where: " + where);
		// mDbHelper.listTables();
		// L.out("values: " + values);
		String json = values.getAsString(StaticFlowColumns.JSON);
		if (WANT_SECURITY && json != null) {
			// out("before update json: " + json);
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			values.put(StaticFlowColumns.JSON, json);
			// out("after update compressed: " + json);
			// test
			// String test =
			// SecurityUtils.decryptAES(User.getUser().getPassword(), json);
			// out("uncompressed : " + test);
		}
		long updated = getDB().update(getTableName(), values, where, null);
		L.out("updated result: " + updated);
		// not in table
		if (updated == 0) {
			long result = getDB().insert(getTableName(), null, values);
			L.out("insert result: " + result);
		}
		// showEvents(getTableName());

		return updated;
	}

	public long update(ContentValues values, String where) {
		// L.out("update values: " + values);
		String timeStamp = null;
		// boolean tickled = values.getAsString(TICKLED) != null
		// && values.getAsString(TICKLED).equals(GJon.TRUE_STRING);
		if (values.getAsString(TICKLED) != null && values.getAsString(TICKLED).equals(GJon.FALSE_STRING))
			timeStamp = new GregorianCalendar().getTimeInMillis() + "";
		// values.put(TIME_STAMP, timeStamp);
		values.remove(TICKLED);
		// L.out("values: " + values);
		L.out("where: " + where);
		// mDbHelper.listTables();
		// L.out("values: " + values);
		String json = values.getAsString(StaticFlowColumns.JSON);
		if (WANT_SECURITY && json != null) {
			// out("before update json: " + json);
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			values.put(StaticFlowColumns.JSON, json);
			// out("after update compressed: " + json);
			// test
			// String test =
			// SecurityUtils.decryptAES(User.getUser().getPassword(), json);
			// out("uncompressed : " + test);
		}
		long updated = getDB().update(getTableName(), values, where, null);
		L.out("updated result: " + updated);
		// not in table
		if (updated == 0) {
			long result = getDB().insert(getTableName(), null, values);
			L.out("insert result: " + result);
		}
		// showEvents(getTableName());
		// MyToast.show("timeStamp: " + timeStamp);

		return updated;
	}

}
