package com.ii.mobile.legacy;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.TableRow;

import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.util.L;

public class CheckBoxField extends BaseField {

	public CheckBoxField(FragmentActivity fragmentActivity, Field field) {
		super(fragmentActivity, field);
	}

	@Override
	public CheckBox createValueView() {
		checkBox = new CheckBox(activity);

		TableRow.LayoutParams layoutParams = new
				TableRow.LayoutParams(
						TableRow.LayoutParams.FILL_PARENT,
						TableRow.LayoutParams.FILL_PARENT);

		// layoutParams.setMargins(0, 0, 10, 0);
		checkBox.setLayoutParams(layoutParams);
		// layoutParams = (LayoutParams) editText.getLayoutParams();
		// layoutParams.setMargins(20, 0, 40, 0);
		// checkBox.setHint(field.customHeader);
		checkBox.setTextSize(15);
		checkBox.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		onRestoreInstanceState();
		if (field.required.equals("1"))
			checkBox.setHintTextColor(Color.parseColor("#FFAAAA"));
		else
			checkBox.setHintTextColor(Color.parseColor("#AAAAAA"));

		checkBox.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					setValue();
					L.out("value set to: " + getValue());
				}
			}
		});

		return checkBox;
	}

	@Override
	public boolean validate() {
		// L.out("validate foo: " + foobar + " edit: " + editText);
		if (checkBox == null) {
			L.out("checkBox is null for " + field.toStringShort());
			return true;
		}
		titleView.setTextColor(Color.parseColor(OPTIONAL));
		setValue();

		return true;
	}

	@Override
	public ContentValues addValue(ContentValues contentValues) {
		String text = getValue();
		if (text != null && !text.equals("")) {
			// L.out("text: " + text);
			contentValues.put(field.controlName, text);
		}
		return contentValues;
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		if (field.required.equals("1")) {
			String text = checkBox.getText().toString();
			if (text == null || text.equals("")) {
				// L.out("text: " + text);
				contentValues.put(field.customHeader, "Not Entered!");
			}
		}
		return contentValues;
	}

	@Override
	public String getValue() {
		if (checkBox.isChecked())
			return "Yes";
		return "No";

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(field.customHeader, getValue());
	}

	public String getTaskValue() {
		// L.out("field: " + field);
		// L.out("task: " + task);
		String text = task.getNamedValue(field.customHeader);
		return text;
	}

	@Override
	void onRestoreInstanceState() {
		String text = getTaskValue();
		// L.out("text: " + text);
		setState(text);
	}

	private void setState(String text) {
		if (text != null && text.equals("Yes"))
			checkBox.setChecked(true);
		else
			checkBox.setChecked(false);
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		String text = outState.getString(field.customHeader);
		// L.out("text: " + text);
		setState(text);
		// checkBox.setText(outState.getString(field.customHeader));
	}
}
