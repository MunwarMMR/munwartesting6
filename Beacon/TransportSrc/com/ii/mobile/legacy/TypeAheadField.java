package com.ii.mobile.legacy;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.ii.mobile.beacon.R;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.util.L;

public class TypeAheadField extends PickListField implements OnItemClickListener {

	private boolean edited = false;

	public TypeAheadField(FragmentActivity fragmentActivity, Field field) {
		super(fragmentActivity, field);
	}

	@Override
	public View createValueView() {
		String value = getValue();
		// L.out("value: " + value);
		// autoComplete = new AutoComplete(activity.getApplicationContext());
		autoComplete.setAdapter(setSuggestionSource(getPickListArray()));
		onRestoreInstanceState();
		autoComplete.setOnItemClickListener(this);
		autoComplete.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		return autoComplete;
	}

	public ArrayAdapter<String> setSuggestionSource(String[] options) {
		// L.out("from: " + hints.length);
		return new ArrayAdapter<String>(activity.getApplicationContext(),
				R.layout.list_item, options);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		edited = true;
		setValue();
		validateAll();
	}

	@Override
	public boolean validate() {
		// L.out("TypeAhead: " + edited + " required: " + field.required);
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

	@Override
	public String getValue() {
		if (autoComplete == null) {
			// L.out("autoComplete text: " + autoComplete);
			return "";
		}
		String text = autoComplete.getText().toString();
		// L.out("getValue spinner text for header: " + field.header + " " +
		// text);

		String temp = lookUpFromText(field.pickListSource, text);
		// L.out("temp: " + temp);
		return temp;
	}

	@Override
	public ContentValues addValue(ContentValues contentValues) {
		String text = autoComplete.getText().toString();

		if (text != null && !text.equals("")) {
			contentValues.put(field.controlName, text);
		}
		return contentValues;
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		String text = autoComplete.getText().toString();
		if (field.required.equals("1") && text.equals("")) {
			contentValues.put(field.customHeader, "Not selected from dropdown list!");
		}
		return contentValues;
	}

	@Override
	protected void setSideEffect(GetTaskInformationByTaskNumberAndFacilityID task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		String text = autoComplete.getText().toString();
		outState.putString(field.header, text);
		L.out("text: " + text);
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		String value = outState.getString(field.header);

		L.out("value: " + value);
		autoComplete.setText(value);
		if (value.length() > 0)
			edited = true;
		// int index = findSelectedValue(value);
		// if (index != -1)
		// spinner.setSelection(index, true);
		// else
		// L.out("*** ERROR can't find spinner selection: " + value);
	}

	@Override
	public String getTaskValue() {
		// L.out("field: " + field);
		String text = task.getNamedValue(field.header);
		if (text == null)
			text = "";
		return text;
	}

	@Override
	void onRestoreInstanceState() {
		String value = getTaskValue();
		L.out("value: " + value);
		String temp = lookUpFromValue(field.pickListSource, value);
		L.out("temp: " + temp);
		autoComplete.setText(temp);
		if (value.length() > 0)
			edited = true;
	}

	@Override
	int findSelectedValue(String key) {
		// L.out("spinner: " + key + " " + spinner);
		if (spinner == null)
			return -1;
		// L.out("spinner: " + key + " " + spinner.getCount());
		for (int i = 0; i < spinner.getCount(); i++) {
			// L.out("spinner.: " + spinner.getAdapter().getItem(i));
			if (spinner.getAdapter().getItem(i).equals(key))
				return i;
		}
		return -1;
	}

}
