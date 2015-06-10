/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.beacon.database;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ii.mobile.soap.cursor.SoapCursor;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class FlowProvider extends ContentProvider {

	private FlowDbAdapter flowDbAdapter;

	@Override
	public boolean onCreate() {
		L.out("onCreate");
		flowDbAdapter = new FlowDbAdapter(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// String soapMethod = uri.getLastPathSegment();
		// L.out("soapMethod: " + soapMethod);
		String employeeId = null;
		String facilityId = null;
		String actionId = null;
		if (selectionArgs != null && selectionArgs.length > 0)
			employeeId = selectionArgs[0];
		if (selectionArgs != null && selectionArgs.length > 1)
			facilityId = selectionArgs[1];
		if (selectionArgs != null && selectionArgs.length > 2)
			actionId = selectionArgs[2];

		List<GJon> gJonList = flowDbAdapter.parse(uri, employeeId, facilityId, actionId);
		// L.out("gJonList: " + gJonList);
		if (gJonList == null)
			return null;
		// L.out("gJonList: " + gJonList);

		Cursor c = new SoapCursor(gJonList);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		L.out("uri: " + uri);
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		L.out("insert");
		throw new UnsupportedOperationException("insert Not supported yet.");
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] arg3) {
		L.out(" update uri: " + uri + " where: " + where);
		flowDbAdapter.update(values, where);
		return 1;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
