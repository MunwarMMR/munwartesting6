package com.ii.mobile.legacy;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.ii.mobile.selfAction.DateButton;
import com.ii.mobile.util.L;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment
		implements DatePickerDialog.OnDateSetListener {
	DateButton dateButton = null;
	private final Calendar calendar;
	private final DatePickerField datePickerField;

	public DatePickerFragment(DatePickerField datePickerField, DateButton dateButton, Calendar calendar) {
		super();
		this.dateButton = dateButton;
		this.calendar = calendar;
		this.datePickerField = datePickerField;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		L.out("created: " + year + " " + month + " " + day);
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		// L.out("*** ERRROR (not really) picked: " + year + " " + month + " " +
		// day);
		dateButton.update(year, month, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.YEAR, year);
		datePickerField.update(year, month, day);
		datePickerField.edited = true;
		datePickerField.validateAll();
	}

	public void update(int year, int month, int day) {
		L.out("************************************** update: " + year + " "
				+ month + " " + day);
		if (year == 0)
			return;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.YEAR, year);

	}
}