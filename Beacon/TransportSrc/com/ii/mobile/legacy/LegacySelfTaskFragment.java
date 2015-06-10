package com.ii.mobile.legacy;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ii.mobile.beacon.R;
import com.ii.mobile.actionButtons.DataFragment;
import com.ii.mobile.actionView.ActionViewFragment;
import com.ii.mobile.cache.Cache;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID.TaskClass;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.tab.BreakActivity;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class LegacySelfTaskFragment extends DataFragment implements NamedFragment, OnItemClickListener,
		OnItemSelectedListener {
	public final static String FRAGMENT_TAG = "selfTaskFragment";

	GetTaskInformationByTaskNumberAndFacilityID selfTask;
	// private boolean resetting = false;

	private final Bundle outState = null;
	private FragmentActivity activity;
	private static int currentPosition = 0;
	private Vibrator vibrator;
	public static LegacySelfTaskFragment legacySelfTaskFragment = null;
	private static View topLevelView = null;
	// private static ArrayList<BaseField> baseFields;
	protected static boolean isVisible = false;

	private static final String NOTES = "Notes";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// L.out("created: " + getActivity());
		vibrator = (Vibrator) (getActivity().getSystemService(Context.VIBRATOR_SERVICE));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = getActivity();
		// L.out("attached!" + getActivity());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// L.out("onDetach!" + getActivity());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// L.out("onDestroy!" + getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		// L.out("onPause");
		isVisible = false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		Spinner spin = (Spinner) topLevelView.findViewById(R.id.spinner);
		// L.out("SAVING: " + spin.getSelectedItemPosition());
		outState.putString("TaskClass", spin.getSelectedItemPosition() + "");
		// List<BaseField> baseFields = BaseField.getBaseFieldList();
		for (BaseField baseField : BaseField.getBaseFieldList()) {
			baseField.onSaveInstanceState(outState);
			// L.out("contentValue: " + contentValues.size());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// L.out("onResume");
		isVisible = true;
	}

	public void onRestoreInstanceState(Bundle outState) {
		// Spinner spin = (Spinner) getActivity().findViewById(R.id.spinner);
		if (outState == null)
			return;
		int itemPosition = (int) L.getLong(outState.getString("TaskClass"));
		// L.out("itemPosition: " + itemPosition);
		setSpinner(itemPosition);
		// resetting = true;
		// spin.setSelection(itemPosition);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		// L.out("onCreateView: " + container + " " + bundle);
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}
		topLevelView = inflater.inflate(R.layout.frag_self_task, container, false);
		Spinner spin = (Spinner) topLevelView.findViewById(R.id.spinner);
		spin.setOnItemSelectedListener(this);

		Button submitButton = (Button) topLevelView.findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submitButtonClick(v);
			}
		});

		// get the classes of self-tasks
		TaskClass[] taskClasses = ((Cache) activity).getTaskClasses();

		ArrayAdapter<String> arrayAdapter =
				new ArrayAdapter<String>(activity, R.layout.list_item, getTypesOfClasses(taskClasses));

		arrayAdapter.setDropDownViewResource(R.layout.list_item);
		spin.setAdapter(arrayAdapter);
		legacySelfTaskFragment = this;

		// onRestoreInstanceState(bundle);
		setSpinner(currentPosition);

		// updateView(((Cache) activity).getTaskClasses()[currentPosition],
		// null);

		View topLevel = topLevelView.findViewById(R.id.topLevel);
		topLevel.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				vibrator.vibrate(400);
				String classBrief = selfTask.getClassBrief();
				String taskClassID = selfTask.getTskTaskClass();
				selfTask = initTask(taskClassID, classBrief);
				updateView(taskClassID, classBrief, null);
				return false;
			}
		});
		return topLevelView;
	}

	private String[] getTypesOfClasses(TaskClass[] taskClass) {
		String[] items = new String[taskClass.length];
		for (int i = 0; i < taskClass.length; i++) {
			items[i] = taskClass[i].brief;
			// L.out(i + " item: " + items[i]);
		}
		return items;
	}

	private int getTaskClassByID(String classID) {

		TaskClass[] taskClasses = ((Cache) activity).getTaskClasses();
		// L.out("classID: " + classID + " length: " + taskClasses.length);
		for (int i = 0; i < taskClasses.length; i++) {
			L.out("taskClasses[i].taskClassID: " + taskClasses[i].taskClassID);
			if (taskClasses[i].taskClassID.equals(classID))
				return i;
		}
		return -1;
	}

	public static boolean isReallyVisible() {
		return isVisible;
	}

	protected void setSpinner(int position) {
		// L.out("position: " + position);
		Spinner spinner = (Spinner) topLevelView.findViewById(R.id.spinner);
		currentPosition = position;
		// setSpinnerSelectionWithoutCallingListener(spinner, position);
		spinner.setSelection(position);
	}

	private void setSpinnerSelectionWithoutCallingListener(final Spinner spinner, final int selection) {
		final OnItemSelectedListener onItemSelectedListener = spinner.getOnItemSelectedListener();
		spinner.setOnItemSelectedListener(null);
		spinner.post(new Runnable() {

			@Override
			public void run() {
				spinner.setSelection(selection);
				spinner.post(new Runnable() {

					@Override
					public void run() {
						spinner.setOnItemSelectedListener(onItemSelectedListener);
					}
				});
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// Intent intent = new Intent().setClass(this,
		// AdmissionsTaskActivity.class);
		// startActivity(intent);
		// L.out("onItemCLICK????");
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		TextView view = (TextView) arg1;
		// L.out("position: " + position + " " + currentPosition);
		if (currentPosition != position || true) {
			if (view != null) {
				// L.out("selected: " + view.getText() + " " + position + " " +
				// arg3);
			}
			// if (position != lastPosition)
			// String taskClassID = ((Cache)
			// activity).getTaskClasses()[position].taskClassID;
			// String brief = ((Cache)
			// activity).getTaskClasses()[position].brief;
			TaskClass taskClass = getCache().getTaskClasses()[position];
			// GetTaskInformationByTaskNumberAndFacilityID newTask =
			// GetTaskInformationByTaskNumberAndFacilityID.getGJon(task.getJson());
			if (selfTask == null)
				selfTask = initTask(taskClass.taskClassID, taskClass.brief);
			selfTask.setTskTaskClass(taskClass.taskClassID);
			selfTask.setClassBrief(taskClass.brief);
			// task = newTask;
			updateView(taskClass, null);
			currentPosition = position;
		}
	}

	public void setTask(GetTaskInformationByTaskNumberAndFacilityID task, Bundle bundle) {
		this.selfTask = GetTaskInformationByTaskNumberAndFacilityID.getGJon(task.getJson());
		reverseEndPoints(this.selfTask);
		// L.out("task: " + task);
		setSpinner(getTaskClassByID(task.getTskTaskClass()));
		// updateView(task.getTskTaskClass(), task.getClassBrief(), null);
		// onSaveInstanceState(bundle);
		// boolean isDemoVersion =
		// activity.getResources().getBoolean(R.bool.isDemoVersion);
		// if (!isDemoVersion)
		getCache().getPageFragmentController().setPosition(TransportActivity.ACTION_VIEW_PAGE);
	}

	private void reverseEndPoints(GetTaskInformationByTaskNumberAndFacilityID realTask) {
		String temp = realTask.getHirDestLocationNode();
		realTask.setHirDestLocationNode(realTask.getHirStartLocationNode());
		realTask.setHirStartLocationNode(temp);
	}

	private void updateView(TaskClass taskClassSelected, Bundle existingBundle) {
		// L.out("taskClassSelected: " + taskClassSelected);
		updateView(taskClassSelected.taskClassID, taskClassSelected.brief, existingBundle);
	}

	public void updateView(String taskClassID, String brief, Bundle existingBundle) {
		// L.out("taskClassID: " + taskClassID);
		// L.out("brief: " + brief);
		// L.out("brief: " + brief);
		// L.out("existingBundle: " + existingBundle);
		Field[] fields = getCache().getTaskField(taskClassID);
		// printField(field);

		BaseField.setTask(selfTask);
		ArrayList<BaseField> baseFields = getViews(fields);

		LinearLayout linearLayout = (LinearLayout) topLevelView.findViewById(R.id.topLevel);
		linearLayout.removeAllViews();
		// int count = 0;
		for (BaseField baseField : baseFields) {
			// L.out(count++ + ": " + baseField.field.toStringShort());
			// linearLayout.addView(baseField.getFieldView());
			linearLayout.addView(getSlotValueView(baseField));
		}
		// List<BaseField> baseFields = BaseField.getBaseFieldList();
		// L.out("task: " + selfTask);
		// L.out("test baseFields: " + baseFields.size());
		Bundle bundle = outState;
		if (existingBundle != null)
			bundle = existingBundle;
		if (outState != null) {
			String position = outState.getString("TaskClass");
			if (position != null) {
				int itemPosition = (int) L.getLong(position);
				L.out("test itemPosition: " + itemPosition);
			}
			for (BaseField baseField : baseFields) {
				baseField.onRestoreInstanceState(bundle);
			}
		}
		if (baseFields.size() > 0) {
			baseFields.get(0).validateAll();
		}
		// resetting = false;
	}

	public ArrayList<BaseField> getViews(Field[] fields) {

		ArrayList<BaseField> baseFields = new ArrayList<BaseField>();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			L.out(i + " field: " + field.customHeader + " controlType: " + field.controlType);
			String controlType = field.controlType;
			String controlName = field.controlName;
			// L.out("controlType: " + controlType);
			if (field.customHeader == null) {
				// L.out("customHeader is null! " + field.toString());
				field.customHeader = "Persist";
			}
			if (field.customHeader.equals("Persist")) {
			}
			else if (controlType.equals(BaseField.PICK_LIST) || controlType.equals(BaseField.DROP_MODE_ENTRY)) {
				baseFields.add(new PickListField(activity, field));
				// notice different test. Use a better widget than text for DOB
			} else if (controlName.equals(BaseField.PATIENT_DOB)) {
				baseFields.add(new DatePickerField(activity, field));
			} else if (controlType.equals(BaseField.TEXT)) {
				baseFields.add(new TextField(activity, field));
			} else if (controlType.equals(BaseField.TYPE_AHEAD)) {
				baseFields.add(new TypeAheadField(activity, field));
			} else if (controlType.equals(BaseField.DATE_TIME_SPLIT)) {
				// L.out("date time ignored: " + field);
			} else if (controlType.equals(BaseField.CHECK_BOX)) {
				baseFields.add(new CheckBoxField(activity, field));
			} else {
				L.out("*** ERROR in controlType: " + controlType);
			}
		}

		BaseField.setFields(topLevelView, baseFields);
		if (baseFields.size() > 1) {
			addNote(baseFields);
			// L.out("now have: " + baseFields.size());
			baseFields.get(0).validateAll();
		}

		return baseFields;
	}

	private void addNote(ArrayList<BaseField> baseFields) {
		for (BaseField baseField : baseFields) {
			if (baseField.field.customHeader.equals(NOTES))
				return;
		}
		// L.out("No Notes: " + baseFields.size());
		Field field = new Field();
		field.customHeader = NOTES;
		field.required = "";
		field.controlName = "Text";
		field.header = NOTES;
		TextField textField = new TextField(activity, field);
		textField.setNumLines(5);
		baseFields.add(textField);
	}

	// private void addNote(ArrayList<BaseField> baseFields) {
	// for (BaseField baseField:baseFields) {
	// if (baseField.field.header.equals(object))
	// }
	//
	// }

	private View getSlotValueView(BaseField baseField) {
		// L.out("activity: " + activity);
		LinearLayout linearLayout = new LinearLayout(activity);
		TextView slotView = getSlot(baseField);
		baseField.setSlotView(slotView);
		linearLayout.addView(slotView);
		linearLayout.addView(getValue(baseField));
		return linearLayout;
	}

	private TextView getSlot(BaseField baseField) {
		TextView viewSlot = new TextView(activity);
		LayoutParams layoutParams =
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.FILL_PARENT, 6.0f);

		viewSlot.setLayoutParams(layoutParams);
		viewSlot.setTextSize(15);
		viewSlot.setGravity(Gravity.CENTER_VERTICAL);
		// viewSlot.setTextColor(getColor(baseField));
		viewSlot.setText(baseField.getField().header);
		baseField.setTitleView(viewSlot);
		baseField.validate();
		return viewSlot;
	}

	private View getValue(BaseField baseField) {
		View view = baseField.createValueView();
		LayoutParams layoutParams =
				new TableRow.LayoutParams(
						TableRow.LayoutParams.FILL_PARENT,
						TableRow.LayoutParams.FILL_PARENT, 4.0f);
		view.setLayoutParams(layoutParams);

		return view;
	}

	// private int getColor(BaseField baseField) {
	// if (baseField.getField().required.equals("1")) {
	// L.out("baseField.getValue(): " + baseField.getValue());
	// if (baseField.getValue().equals("")) {
	// return Color.parseColor(BaseField.REQUIRED);
	// }
	// else
	// return Color.parseColor(BaseField.REQUIRED_PRESENT);
	// } else
	// return Color.parseColor(BaseField.OPTIONAL);
	// }

	private GetTaskInformationByTaskNumberAndFacilityID initTask(String taskClassID, String classBrief) {
		GetTaskInformationByTaskNumberAndFacilityID task = new GetTaskInformationByTaskNumberAndFacilityID();
		User user = User.getUser();
		ValidateUser validateUser = user.getValidateUser();
		task.setEmployeeID(validateUser.getEmployeeID());
		task.setFacilityID(validateUser.getFacilityID());
		task.setTskTaskClass(taskClassID);
		// task.setPatientName("fred");
		task.setClassBrief(classBrief);
		task.setRequestorName(user.getUsername());
		task.setRequestorPhone("123456789");
		// L.out("task: " + task);
		return task;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// L.out("nothing: " + arg0);
	}

	// public GetEmployeeAndTaskStatusByEmployeeID getTask() {
	// GetEmployeeAndTaskStatusByEmployeeID task = new
	// GetEmployeeAndTaskStatusByEmployeeID();
	// List<BaseField> baseFields = BaseField.getBaseFieldList();
	// for (BaseField baseField : baseFields) {
	// baseField.setValue(task);
	// }
	// return task;
	// }

	public void submitButtonClick(View view) {
		if (getCache().getTask() != null) {
			MyToast.show("Unable to self task\nwhen you have a task!");
			return;
		}
		List<BaseField> baseFields = BaseField.getBaseFieldList();

		for (BaseField baseField : baseFields) {
			// side-effects
			baseField.setValue();
			// L.out("contentValue: " + contentValues.size());
		}
		// createRecord(task);
		// L.out("submit task: " + selfTask);
		ContentValues contentValues = new ContentValues();
		if (BaseField.validated) {

			for (BaseField baseField : baseFields) {
				baseField.addValue(contentValues);
				// L.out("contentValue: " + contentValues.size());
			}
			String temp = "Created Task: ";
			Set<Entry<String, Object>> valueSet = contentValues.valueSet();
			// L.out("valuseSet: " + valueSet.size());
			Iterator<Entry<String, Object>> iter = valueSet.iterator();
			while (iter.hasNext()) {
				Entry<?, ?> entry = iter.next();
				temp += "\n" + entry.getKey() + ": " + entry.getValue();
				// L.out("Entry: " + entry.getKey() + " " + entry.getValue());
			}
			MyToast.show(temp, Toast.LENGTH_LONG);
			selfTask(selfTask);
			// finish();
		}
		else {
			for (BaseField baseField : baseFields) {
				baseField.addFailValue(contentValues);
			}
			String temp = "Invalid Task:";
			Set<Entry<String, Object>> valueSet = contentValues.valueSet();
			// L.out("valuseSet: " + valueSet.size());
			Iterator<Entry<String, Object>> iter = valueSet.iterator();
			while (iter.hasNext()) {
				Entry<?, ?> entry = iter.next();
				temp += "\n" + entry.getKey() + ": " + entry.getValue();
				// L.out("Entry: " + entry.getKey() + " " + entry.getValue());
			}
			MyToast.show(temp, Toast.LENGTH_LONG);
		}
	}

	protected Cache getCache() {
		return ((Cache) activity);
	}

	private void selfTask(GetTaskInformationByTaskNumberAndFacilityID task) {
		if (((Cache) activity).getTask() != null) {
			MyToast.show("ERROR - Already have task!");
			return;
		}
		task.setJson(null);
		task.setTaskStatusBrief(ActionViewFragment.ASSIGNED);
		task.setEmployeeID(User.getUser().getEmployeeID());
		task.setFacilityID(User.getUser().getFacilityID());
		task.setTaskNumber(new GregorianCalendar().getTimeInMillis() + "");
		task.setTickled(false);
		GetTaskInformationByTaskNumberAndFacilityID newTask = GetTaskInformationByTaskNumberAndFacilityID.getGJon(task.getNewJson());
		newTask = GetTaskInformationByTaskNumberAndFacilityID.getGJon(task.getNewJson());
		newTask.printJson();
		// L.out("newTask: " + newTask);
		// stopTimer();
		User.getUser().getValidateUser().setTaskNumber(newTask.getTaskNumber());
		setEmployeeStatus(BreakActivity.ASSIGNED, true);
		// updateDataModel(task, activity, true);
		// startAssignedTimer();
		((Cache) activity).setTask(task);
	}

	@Override
	public String getTitle() {
		return "Self Action";
	}

	@Override
	public void update() {
		// L.out("selfTask update");
	}

	@Override
	public boolean wantActions() {
		return true;
	}
}

class Arg {
	String type = null;
	String value = null;

	Arg(String title, String value) {
		this.type = title;
		this.value = value;
	}
}
