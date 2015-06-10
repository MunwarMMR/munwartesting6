package com.ii.mobile.beacon.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ii.mobile.flow.db.FlowUploader;
import com.ii.mobile.flow.db.StaticFlowProvider.StaticFlowColumns;
import com.ii.mobile.util.L;

public abstract class AbstractDbAdapter {

	private String tableName = null;
	protected static DatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb;
	public final Context context;

	// public static final String TABLE_WIFI = "Wifi";
	//
	// private static List<String> tableNames = new ArrayList<String>();
	//
	// protected static final String TABLE_CREATE_WIFI =
	// "create table Wifi (id text not null, "
	// + "name text not null, strength text, throughput text);";

	// protected static final String TABLE_USERS = "users";
	public static final String TABLE_FLOW = "table_flow";

	private static List<String> tableNames = new ArrayList<String>();

	protected static final String TABLE_CREATE_FLOW = "create table table_flow (_id integer primary key autoincrement, "
			+ "flowMethod text not null , actorId text, facilityId text, actionId text, localActionNumber text, "
			+ "time_stamp text, update_time_stamp text, json blob not null);";

	public static final String KEY_ROWID = "_id";
	public static final String TIME_STAMP = "time_stamp";
	public static final String UPDATE_TIME_STAMP = "update_time_stamp";
	public static final String SERVER_ID = "SERVER_ID";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	protected FlowUploader flowUploader;

	public static FlowDbAdapter flowDbAdapter;

	public static final String KEY_SESSIONID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_STRENGTH = "strength";
	public static final String KEY_THROUGHPUT = "throughput";

	public static final String TICKLED = "tickled";

	// protected NetworkUploader networkUploader;
	// was 155
	protected static final String DATABASE_NAME = "beacon4.db";

	protected static int databaseVersion = 1;

	public AbstractDbAdapter(Context context) {
		this.context = context;
		open();
		// networkUploader = NetworkUploader.register(this, context);
	}

	synchronized private void open() throws SQLException {
		if (mDbHelper == null) {
			mDbHelper = new DatabaseHelper(this, context);
			mDb = mDbHelper.getWritableDatabase();
			mDbHelper.listTables();
		}
	}

	public SQLiteDatabase getWritableDatabase() {
		return getDB();
	}

	public void close() {
		mDbHelper.close();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
		tableNames.add(tableName);
	}

	public static SQLiteDatabase getDB() {
		return mDb;
	}

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

	public long update(ContentValues values, String where, boolean leaveTimeStamp) {
		L.out("update leaveTimeStamp: " + leaveTimeStamp);
		// values.remove(TICKLED);
		// L.out("values: " + values);
		L.out("where: " + where);
		// mDbHelper.listTables();
		// L.out("values: " + values);
		String json = values.getAsString(StaticFlowColumns.JSON);

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

	protected static class DatabaseHelper extends SQLiteOpenHelper {

		@SuppressWarnings("unused")
		private AbstractDbAdapter dbAdapter = null;

		DatabaseHelper(AbstractDbAdapter dbAdapter, Context context) {
			super(context, DATABASE_NAME, null, databaseVersion);
			this.dbAdapter = dbAdapter;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_CREATE_FLOW);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			System.out.println("Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			// db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATE_FLOW);
		}

		public ArrayList<Object> listTables() {
			ArrayList<Object> tableList = new ArrayList<Object>();
			String SQL_GET_ALL_TABLES = "SELECT name FROM " + "sqlite_master"
					+ " WHERE type='table' ORDER BY name";
			Cursor cursor = getDB().rawQuery(SQL_GET_ALL_TABLES, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				do {
					tableList.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
			cursor.close();
			System.out.println("tableList: " + tableList);
			return tableList;
		}
	}
}