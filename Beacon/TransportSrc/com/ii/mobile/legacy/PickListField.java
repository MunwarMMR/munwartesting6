package com.ii.mobile.legacy;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableRow;

import com.ii.mobile.beacon.R;
import com.ii.mobile.cache.Cache;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID.Geography;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID.PickList;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.util.L;

public class PickListField extends BaseField implements AdapterView.OnItemSelectedListener {

	static String[] items = { "error1", "error2",
			"error3"
	};

	public PickListField(Activity activity, Field field) {
		super(activity, field);
		// L.out("field: " + field);
	}

	@Override
	public View createValueView() {
		spinner = new Spinner(activity);
		String value = getValue();
		// L.out("value: " + value);
		String[] pickList = getPickListArray();
		if (pickList == null)
			pickList = items;
		ArrayAdapter<Object> arrayAdapter =
				new ArrayAdapter<Object>(activity, R.layout.spinner_item, pickList);

		arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
		spinner.setAdapter(arrayAdapter);
		spinner.setPrompt(field.customHeader);
		onRestoreInstanceState();
		spinner.setPadding(0, 0, 0, 10);

		TableRow.LayoutParams layoutParams = new
				TableRow.LayoutParams(
						TableRow.LayoutParams.FILL_PARENT,
						TableRow.LayoutParams.FILL_PARENT);
		spinner.setLayoutParams(layoutParams);
		spinner.setOnItemSelectedListener(this);
		return spinner;
	}

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String pick = (String) spinner.getSelectedItem();
		// L.out("pick: " + pick + " " + field);
		// L.out("lastPick: " + lastPick);
		if (field.required.equals("1")) {
			if (lastPick != null && lastPick != pick)
				validateAll();
			lastPick = pick;
		}
		setValue();
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

	protected String[] getPickListArray() {
		Geography geography = ((Cache) activity).getPickListGeography(field.pickListSource);
		if (geography == null)
			return null;

		PickList[] pickList = geography.pickList;
		String[] strings = new String[pickList.length];
		for (int i = 0; i < pickList.length; i++) {
			strings[i] = pickList[i].textPart;
		}
		return strings;
	}

	@Override
	public String getValue() {
		if (spinner == null) {
			// L.out("spinner text: " + spinner);
			return "";
		}
		String text = (String) spinner.getSelectedItem();
		// L.out("getValue spinner text for header: " + field.header + " " +
		// text);
		return text;
	}

	@Override
	public boolean validate() {
		if (spinner == null) {
			L.out("spinner text: " + spinner);
			return false;
		}
		String text = (String) spinner.getSelectedItem();
		// L.out("spinner text: " + text + " " + field.required);

		if (field.required.equals("1")) {
			if (text.equals("")) {
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
	public ContentValues addValue(ContentValues contentValues) {
		if (spinner == null) {
			// L.out("addValue spinner text: " + spinner);
			return contentValues;
		}
		String text = (String) spinner.getSelectedItem();
		if (text != null && !text.equals("")) {
			contentValues.put(field.controlName, text);
		}
		return contentValues;
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		if (field.required.equals("1")) {
			String text = (String) spinner.getSelectedItem();

			if (text == null || text.equals("")) {
				// L.out("text: " + text);
				contentValues.put(field.customHeader, "Not Selected!");
			}
		}
		return contentValues;
	}

	@Override
	protected void setSideEffect(GetTaskInformationByTaskNumberAndFacilityID task) {
		Geography geography = ((Cache) activity).getPickListGeography(field.pickListSource);
		if (geography == null)
			return;
		String textPart = getValue();
		if (textPart == null)
			return;
		PickList[] pickList = geography.pickList;
		String valuePart = lookUp(pickList, textPart);
		// L.out("sideEffect text header: " + field.header + " text: " +
		// textPart + " " + valuePart);
		task.setSideEffect(field.header, valuePart);
	}

	private String lookUp(PickList[] pickList, String textPart) {
		for (int i = 0; i < pickList.length; i++) {
			if (pickList[i].textPart.equals(textPart))
				return pickList[i].valuePart;
		}
		return null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(field.header, getValue());
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		String value = outState.getString(field.header);
		int index = findSelectedValue(value);
		if (index != -1)
			spinner.setSelection(index, true);
		else
			L.out("*** ERROR can't find spinner selection: " + value);
	}

	String getTaskValue() {
		String temp = task.getNamedValue(field.header);
		// L.out("temp: " + temp);
		return temp;
	}

	@Override
	void onRestoreInstanceState() {
		String value = getTaskValue();
		int index = findSelectedValue(value);
		if (index != -1)
			spinner.setSelection(index, true);
		else
			L.out("*** ERROR can't find spinner selection: " + value);
	}

	int findSelectedValue(String key) {
		// L.out("spinner: " + key + " " + spinner);
		if (spinner == null)
			return -1;
		L.out("spinner: " + key + " " + spinner.getCount());
		for (int i = 0; i < spinner.getCount(); i++) {
			// L.out("spinner.: " + spinner.getAdapter().getItem(i));
			if (spinner.getAdapter().getItem(i).equals(key))
				return i;
		}
		return -1;
	}

}
