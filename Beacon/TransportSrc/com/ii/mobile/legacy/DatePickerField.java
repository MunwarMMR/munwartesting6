package com.ii.mobile.legacy;

import java.util.Calendar;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.ii.mobile.beacon.R;
import com.ii.mobile.selfAction.DateButton;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.util.L;

public class DatePickerField extends BaseField implements OnItemClickListener {

	// Button button = null;
	DateButton dateButton;

	public DatePickerField(Activity activity, Field field) {
		super(activity, field);
	}

	@Override
	public View createValueView() {
		dateButton = new DateButton(activity);
		// L.out("before createValueView: " + year + " " + month + " " + day);
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.YEAR, year);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		// L.out("createValueView: " + year + " " + month + " " + day);
		dateButton.update(year, month, day);
		final DatePickerField datePickerField = this;
		newFragment = new DatePickerFragment(datePickerField, dateButton, calendar);
		dateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				newFragment.show(getCache().getSupportFragmentManager(), "datePicker");
			}
		});
		return dateButton;
	}

	public ArrayAdapter<String> setSuggestionSource(String[] hints) {
		// L.out("from: " + hints.length);
		return new ArrayAdapter<String>(activity.getApplicationContext(),
				R.layout.list_item, hints);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		edited = true;
		// L.out("onItemClick: " + arg1 + " " + arg2 + " " + arg3);
		setValue();
		validateAll();
	}

	@Override
	public boolean validate() {
		// L.out("DOB: " + edited);
		if (titleView == null) {
			L.out("titleView is null");
			return true;
		}
		if (field.required.equals("1")) {
			if (!edited) {
				titleView.setTextColor(Color.parseColor(REQUIRED));
				return false;
			}
			else
				titleView.setTextColor(Color.parseColor(REQUIRED_PRESENT));

		} else
			titleView.setTextColor(Color.parseColor(OPTIONAL));

		return true;
	}

	public void update(int year, int month, int day) {
		// L.out("************************************** update: " + year + " "
		// + month + " " + day);
		if (year == 0)
			return;
		this.year = year;
		this.month = month;
		this.day = day;

	}

	@Override
	public ContentValues addValue(ContentValues contentValues) {
		String text = "" + year + " " + (month + 1) + " " + day;
		if (edited && text != null && !text.equals("")) {
			contentValues.put(field.controlName, text);
		}
		return contentValues;
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		if (field.required.equals("1") && !edited) {
			contentValues.put(field.controlName, "Date not changed from default!");
		}
		return contentValues;
	}

	@Override
	public String getValue() {
		// String text = "" + year + " " + (month + 1) + " " + day;
		String text = (month + 1) + "/" + day + "/" + year;
		if (edited && text != null && !text.equals("")) {
			return text;
		}
		return "";
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(field.header + "month", month + "");
		outState.putString(field.header + "day", day + "");
		outState.putString(field.header + "year", year + "");
		L.out("*******************888 onSaveInstanceState: " + year + " " + month + " " + day + " "
				+ field.header);
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		month = (int) L.getLong(outState.getString(field.header + "month"));
		day = (int) L.getLong(outState.getString(field.header + "day"));
		year = (int) L.getLong(outState.getString(field.header + "year"));
		L.out("*******************888 onRestoreInstanceState: " + year + " " + month + " " + day);
		newFragment.update(year, month, day);
		dateButton.update(year, month, day);

	}

	@Override
	void onRestoreInstanceState() {
		L.out("ignoring datePicker! on restore!");

	}
}
