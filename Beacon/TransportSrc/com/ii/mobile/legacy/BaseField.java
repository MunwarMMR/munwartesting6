package com.ii.mobile.legacy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ii.mobile.beacon.R;
import com.ii.mobile.actionView.PickField;
import com.ii.mobile.cache.Cache;
import com.ii.mobile.selfAction.AutoComplete;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.util.L;

abstract public class BaseField extends PickField {

	public static final String TEXT = "Text";
	public static final String TYPE_AHEAD = "TypeAhead";
	public static final String PICK_LIST = "PickList";
	public static final String DATE_TIME_SPLIT = "DateTimeSplit";
	public static final String PATIENT_DOB = "TxtPatientDOB";
	public static final String DROP_MODE_ENTRY = "DrpModeEntry";
	public static final String CHECK_BOX = "CheckBox";

	protected static final String REQUIRED = "#CC0000";
	protected static final String REQUIRED_PRESENT = "#CC9999";
	protected static final String OPTIONAL = "#000000";
	protected static final String VALID = "#00AA00";
	protected static final String HAVE_TASK = "#FFA500";

	private static ArrayList<BaseField> baseFields;
	private static View topLevelView = null;
	private static boolean inited = false;
	public static boolean validated = false;

	protected String lastPick = null;

	// amazing that need to put here. Not initialize in super and then
	// initialized after to default! Since call createFieldLayout here, the
	// side-effects go away!
	protected EditText editText = null;
	protected int month = 0;
	protected int day = 1;
	protected int year = 1970;
	protected Spinner spinner;
	protected CheckBox checkBox;
	protected DatePickerFragment newFragment = null;
	protected AutoComplete autoComplete;
	boolean edited = false;

	protected Activity activity;

	// private final TableRow fieldLayout;
	public final Field field;
	protected TextView titleView;
	protected static GetTaskInformationByTaskNumberAndFacilityID task = null;

	public BaseField(Activity activity, Field field) {
		super(activity);
		this.activity = activity;
		this.field = field;
		// fieldLayout = createFieldLayout();
		// validate();
	}

	public static void setTask(GetTaskInformationByTaskNumberAndFacilityID task) {
		BaseField.task = task;
		// L.out("task: " + task);
	}

	public void validateAll() {
		L.out("inited: " + inited);
		// if (!inited) {
		// return;
		// }
		// int count = 0;

		if (baseFields == null)
			return;
		validated = true;
		L.out("baseFieldList: " + baseFields.size());
		for (BaseField baseField : baseFields) {
			boolean valid = baseField.validate();
			// L.out((count++) + ": valid: " + valid + " BaseField: " +
			// baseField.getClass() + " "
			// + baseField.field.toStringShort());
			if (!valid)
				validated = false;
		}
		updateSubmitButton();
		L.out("valididate valid: " + validated + " " + baseFields.size());
	}

	private void updateSubmitButton() {

		Button button = (Button) topLevelView.findViewById(R.id.submitButton);
		// L.out("have task: " + getCache().getTask());
		if (getCache().getTask() != null) {
			button.setTextColor(Color.parseColor(HAVE_TASK));
			return;
		}

		if (validated)
			button.setTextColor(Color.parseColor(VALID));
		else
			button.setTextColor(Color.parseColor(REQUIRED));
	}

	public static void setFields(View topLevelView, ArrayList<BaseField> baseFields) {
		BaseField.baseFields = baseFields;
		BaseField.topLevelView = topLevelView;

	}

	public static List<BaseField> getBaseFieldList() {
		return baseFields;
	}

	public abstract View createValueView();

	public abstract ContentValues addValue(ContentValues contentValues);

	public abstract ContentValues addFailValue(ContentValues contentValues);

	public abstract boolean validate();

	// public View getFieldView() {
	// return fieldLayout;
	// }

	@Override
	public String toString() {
		return "base field: " + field.customHeader + " controlType: " + field.controlType;
	}

	public void setValue() {
		// L.out("setNamedValue: " + this + " " + getValue());
		task.setNamedValue(field.customHeader, getValue());
		setSideEffect(task);
	}

	protected void setSideEffect(GetTaskInformationByTaskNumberAndFacilityID task) {
	}

	public Field getField() {
		return field;
	}

	abstract public String getValue();

	public abstract void onSaveInstanceState(Bundle outState);

	public abstract void onRestoreInstanceState(Bundle outState);

	abstract void onRestoreInstanceState();

	public Cache getCache() {
		if (activity == null || !(activity instanceof Cache)) {
			L.out("****** ERROR TransportActivity: " + activity);
			return null;
		}
		return (Cache) activity;
	}

	public void setTitleView(TextView titleView) {
		this.titleView = titleView;
	}

	public void setSlotView(TextView slotView) {
		titleView = slotView;

	}

}
