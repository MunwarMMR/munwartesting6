package com.ii.mobile.legacy;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableRow;

import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.util.L;

public class TextField extends BaseField {

	public Editable lastString;
	public String foobar = "no way!";
	private int numLines = 1;

	public TextField(FragmentActivity fragmentActivity, Field field) {
		super(fragmentActivity, field);
	}

	@Override
	public EditText createValueView() {
		editText = new EditText(activity);
		editText.setLines(numLines);
		TableRow.LayoutParams layoutParams = new
				TableRow.LayoutParams(
						TableRow.LayoutParams.FILL_PARENT,
						TableRow.LayoutParams.FILL_PARENT);

		// layoutParams.setMargins(0, 0, 10, 0);
		editText.setLayoutParams(layoutParams);
		// layoutParams = (LayoutParams) editText.getLayoutParams();
		// layoutParams.setMargins(20, 0, 40, 0);
		editText.setHint(field.customHeader);
		editText.setTextSize(15);
		editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		onRestoreInstanceState();
		if (field.required != null && field.required.equals("1"))
			editText.setHintTextColor(Color.parseColor("#FFAAAA"));
		else
			editText.setHintTextColor(Color.parseColor("#AAAAAA"));
		editText.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				// validate();
				String newText = editText.getText().toString();
				// L.out("lastString: " + lastString);
				// L.out("newText: " + newText);
				if (field.required != null && field.required.equals("1")) {
					if (!lastString.equals(newText))
						validateAll();
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				lastString = editText.getText();
				// L.out("lastString: " + lastString);
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// intentionally left blank
			}

		});
		// L.out("edittext: " + editText + " " + field.toString());
		// L.out("getTaskValue(): " + getTaskValue());
		// editText.setText(getTaskValue());
		// validateAll();
		return editText;
	}

	@Override
	public boolean validate() {
		// L.out("validate foo: " + foobar + " edit: " + editText);
		if (editText == null) {
			L.out("editText is null for " + field.toStringShort());
			return true;
		}
		setValue();
		Editable foo = editText.getText();
		String text = foo.toString();
		// L.out("validate textField: #" + text + "#" + text.length());
		if (titleView == null) {
			L.out("titleView is null");
			return true;
		}
		if (field.required.equals("1")) {
			if (text != null && text.equals("")) {
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
		String text = editText.getText().toString();
		if (text != null && !text.equals("")) {
			// L.out("text: " + text);
			contentValues.put(field.controlName, text);
		}
		return contentValues;
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		if (field.required.equals("1")) {
			String text = editText.getText().toString();
			if (text == null || text.equals("")) {
				// L.out("text: " + text);
				contentValues.put(field.customHeader, "Not Entered!");
			}
		}
		return contentValues;
	}

	@Override
	public String getValue() {
		String text = editText.getText().toString();
		return text;
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
		editText.setText(text);
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		String text = outState.getString(field.customHeader);
		// L.out("text: " + text);
		editText.setText(outState.getString(field.customHeader));
	}

	public void setNumLines(int numLines) {
		this.numLines = numLines;

	}
}
