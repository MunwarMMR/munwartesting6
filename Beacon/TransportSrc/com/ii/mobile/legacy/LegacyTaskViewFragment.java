package com.ii.mobile.legacy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ii.mobile.beacon.R;
import com.ii.mobile.cache.Cache;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.payload.StatusWrapper;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID.PickList;
import com.ii.mobile.util.L;

/**

 */
public class LegacyTaskViewFragment extends Fragment implements NamedFragment, SyncCallback {
	public final static String FRAGMENT_TAG = "TaskFragment";
	private FragmentActivity activity = null;
	private Cache cache = null;
	private View topLevelView = null;
	private TableLayout actionViewTable = null;

	public final static String NO_TASK = "No Task";
	public final static String UNASSIGNED = "Unassigned";
	public final static String ASSIGNED = "Assigned";
	public final static String ACTIVE = "Active";
	public final static String DELAYED = "Delayed";
	public final static String COMPLETED = "Completed";
	public final static String CANCELED = "Canceled";

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {

			return null;
		}
		activity = getActivity();

		this.cache = (Cache) activity;
		// cache.setTask(createTestTask());
		topLevelView = inflater.inflate(R.layout.transport_action_view, container, false);
		actionViewTable = (TableLayout) topLevelView.findViewById(R.id.actionViewTable);
		L.out("topLevelView: " + topLevelView);
		UpdateController.INSTANCE.registerCallback(this, UpdateController.STATUS_WRAPPER);
		update();
		return topLevelView;
	}

	@Override
	public void onResume() {
		super.onResume();
		update();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		UpdateController.INSTANCE.unRegisterCallback(this, UpdateController.STATUS_WRAPPER);
	}

	private void updateTable(GetActionStatus getActionStatus) {
		addRow("Patient:", "Kim Fairchild", null);
		addRow("dateOfBirth:", "10/12/56", null);
		addRow("Patient:", "Kim Fairchild", null);
		if (true)
			return;
		((TextView) topLevelView.findViewById(R.id.patientName)).setText(getActionStatus.getPatientName());
		((TextView) topLevelView.findViewById(R.id.dateOfBirth)).setText(getActionStatus.getPatientBirthDate());
		((TextView) topLevelView.findViewById(R.id.medicalRecordNumber)).setText(getActionStatus.getPatientMRN());
		TextView textView = (TextView) topLevelView.findViewById(R.id.isolation);
		// String isolationPatient = getActionStatus.getIsolationPatient();
		// if (isolationPatient != null && isolationPatient.equals("Yes"))
		// textView.setTextColor(Color.parseColor("#FF0000"));
		// else
		// textView.setTextColor(Color.parseColor("#000000"));
		// textView.setText(getActionStatus.getIsolationPatient());
		((TextView) topLevelView.findViewById(R.id.type)).setText(getActionStatus.getClassName());
		((TextView) topLevelView.findViewById(R.id.start)).setText(getActionStatus.getStartName());
		((TextView) topLevelView.findViewById(R.id.destination)).setText(getActionStatus.getDestinationName());
		((TextView) topLevelView.findViewById(R.id.mode)).setText(getActionStatus.getMode());
		((TextView) topLevelView.findViewById(R.id.equipment)).setText(getActionStatus.getEquipmentId());
		String notes = getActionStatus.getNotes();
		if (notes != null)
			notes = notes.toUpperCase();
		((TextView) topLevelView.findViewById(R.id.notes)).setText(notes);
	}

	private void addRow(String first, String second, String third) {
		TableRow tableRow = (TableRow) getActivity().findViewById(R.layout.transport_action_view);
		L.out("tableRow: " + tableRow);
		if (tableRow != null) {
			((TextView) tableRow.findViewById(R.id.first)).setText(first);
			((TextView) tableRow.findViewById(R.id.second)).setText(second);
			if (third != null)
				((TextView) tableRow.findViewById(R.id.third)).setText(third);
			actionViewTable.addView(tableRow);
		}
	}

	private void updateTableGood(GetActionStatus getActionStatus) {

		((TextView) topLevelView.findViewById(R.id.patientName)).setText(getActionStatus.getPatientName());
		((TextView) topLevelView.findViewById(R.id.dateOfBirth)).setText(getActionStatus.getPatientBirthDate());
		((TextView) topLevelView.findViewById(R.id.medicalRecordNumber)).setText(getActionStatus.getPatientMRN());
		TextView textView = (TextView) topLevelView.findViewById(R.id.isolation);
		// String isolationPatient = getActionStatus.getIsolationPatient();
		// if (isolationPatient != null && isolationPatient.equals("Yes"))
		// textView.setTextColor(Color.parseColor("#FF0000"));
		// else
		// textView.setTextColor(Color.parseColor("#000000"));
		// textView.setText(getActionStatus.getIsolationPatient());
		((TextView) topLevelView.findViewById(R.id.type)).setText(getActionStatus.getClassName());
		((TextView) topLevelView.findViewById(R.id.start)).setText(getActionStatus.getStartName());
		((TextView) topLevelView.findViewById(R.id.destination)).setText(getActionStatus.getDestinationName());
		((TextView) topLevelView.findViewById(R.id.mode)).setText(getActionStatus.getMode());
		((TextView) topLevelView.findViewById(R.id.equipment)).setText(getActionStatus.getEquipmentId());
		String notes = getActionStatus.getNotes();
		if (notes != null)
			notes = notes.toUpperCase();
		((TextView) topLevelView.findViewById(R.id.notes)).setText(notes);
	}

	@Override
	public void update() {
		if (topLevelView == null) {
			L.out("topLevelView is null");
			return;
		}
		StatusWrapper statusWrapper = UpdateController.INSTANCE.statusWrapper;
		// L.out("statusWrapper: " + statusWrapper);
		if (statusWrapper.currentStatus.actionId == null)
			updateNoAction();
		else {
			GetActionStatus getActionStatus = UpdateController.INSTANCE.getActionStatus;
			// L.out("TaskFragment update: " + getActionStatus + L.p());
			if (getActionStatus == null) {
				updateNoAction();
			} else
				updateHaveAction(getActionStatus);
		}
	}

	private void updateNoAction() {
		topLevelView.findViewById(R.id.notice).setVisibility(View.VISIBLE);
		topLevelView.findViewById(R.id.customScrollTable).setVisibility(View.GONE);
		topLevelView.findViewById(R.id.taskTable).setVisibility(View.GONE);
	}

	private void updateHaveAction(GetActionStatus getActionStatus) {
		updateTable(getActionStatus);
		topLevelView.findViewById(R.id.notice).setVisibility(View.GONE);
		topLevelView.findViewById(R.id.customScrollTable).setVisibility(View.VISIBLE);
		topLevelView.findViewById(R.id.taskTable).setVisibility(View.VISIBLE);
	}

	// public void updateold() {
	// L.out("TaskFragment update: " + cache);
	// if (cache == null)
	// return;
	// // L.out("update: " + cache.getTask());
	// View notice = view.findViewById(R.id.notice);
	// View scroll = view.findViewById(R.id.customScrollTable);
	// View extra = view.findViewById(R.id.taskTable);
	// if (cache.getTask() != null) {
	// notice.setVisibility(View.GONE);
	// scroll.setVisibility(View.VISIBLE);
	// extra.setVisibility(View.VISIBLE);
	// generateFieldView(cache.getTask());
	// } else {
	// notice.setVisibility(View.VISIBLE);
	// scroll.setVisibility(View.GONE);
	// extra.setVisibility(View.GONE);
	//
	// }
	// }

	@Override
	public String getTitle() {
		return "Action Home";
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("TaskFragment: ");
		update();
	}

	// @SuppressWarnings("unused")
	// private GetTaskInformationByTaskNumberAndFacilityID createTestTask() {
	//
	// GetTaskInformationByTaskNumberAndFacilityID task = new
	// GetTaskInformationByTaskNumberAndFacilityID();
	// // L.out("task: " + task);
	//
	// task.getNewJson();
	// // task.printJson();
	// task.setHirStartLocationNode("B-103a");
	// task.setHirDestLocationNode("Radiology");
	// task.setTaskNumber(new GregorianCalendar().getTimeInMillis() + "");
	// task.setTskTaskClass("2510");
	// task.setClassBrief("Old Transport");
	// task.setTaskStatusBrief(ASSIGNED);
	// task.setEmployeeID(User.getUser().getEmployeeID());
	// task.setFacilityID(User.getUser().getFacilityID());
	// task.setPatientName("Kim Fairchild");
	// task.setPatientMRN("000078127");
	// task.setIsolation("No");
	// task.setModeBrief("Ambulatory");
	// task.setEquipmentBrief("IV Pole");
	// String note =
	// "short isolation patient notify radiology prior to bringing patient to radiology";
	// task.setNotes(note.toUpperCase());
	// task.setTickled(false);
	//
	// L.out("**** new json: ");
	// task.setJson(null);
	// task.setJson(task.getNewJson());
	// // task.printJson();
	//
	// GetTaskInformationByTaskNumberAndFacilityID newTask =
	// GetTaskInformationByTaskNumberAndFacilityID.getGJon(task.getNewJson());
	// return newTask;
	// }

	// private void generateFieldView(GetActionStatus getActionStatus) {
	// L.out("actionStatus: " + getActionStatus);
	// if (getActionStatus == null)
	// return;

	// Field[] fields = getFields(getActionStatus);
	// SelectClassTypesByFacilityId classTypes =
	// UpdateController.INSTANCE.selectClassTypesByFacilityId;
	// updateTable(getActionStatus);
	//
	// if (fields == null) {
	// MyToast.show("Unable to find fields for taskClassID: " +
	// taskClassID);
	// return;
	// }
	// // printField(field);
	// List<DisplayField> baseDisplayFields =
	// DisplayField.getViews(activity, fields, task);
	// baseDisplayFields = updateCustomTable(task, baseDisplayFields);
	// TableLayout tableLayout = (TableLayout)
	// view.findViewById(R.id.taskTable);
	// // L.out("tableLayout: " + tableLayout);
	// tableLayout.removeAllViews();
	//
	// if (baseDisplayFields != null)
	// for (DisplayField baseDisplayField : baseDisplayFields) {
	// // L.out(count++ + ": " +
	// // baseDisplayField.field.toStringShort());
	// tableLayout.addView(baseDisplayField.getFieldView());
	// }
	// }

	// private Field[] getFields(GetActionStatus actionStatus) {
	// SelectClassTypesByFacilityId selectClassTypesByFacilityId =
	// UpdateController.INSTANCE.selectClassTypesByFacilityId;
	// Targets foo = actionStatus.getActionStatusInner.targets;
	// String functionalAreaType_id = foo.functionalAreaType_id;
	// String facility_id = foo.facility_id;
	// String actionType_id = foo.actionType_id;
	// String classType = foo.classType.name;
	// L.out("functionalAreaType_id: " + functionalAreaType_id);
	// L.out("facility_id: " + facility_id);
	// L.out("actionType_id: " + actionType_id);
	// L.out("classType: " + classType);
	// return null;
	// }

	// private void
	// generateFieldViewold(GetTaskInformationByTaskNumberAndFacilityID task) {
	// // L.out("task: " + task);
	// if (task == null)
	// return;
	//
	// String taskClassID = task.getTskTaskClass();
	// // L.out("taskClassID: " + taskClassID);
	// Field[] field = ((Cache) activity).getTaskField(taskClassID);
	// if (field == null) {
	// MyToast.show("Unable to find fields for taskClassID: " + taskClassID);
	// return;
	// }
	// // printField(field);
	// List<DisplayField> baseDisplayFields = DisplayField.getViews(activity,
	// field, task);
	// baseDisplayFields = updateCustomTable(task, baseDisplayFields);
	// TableLayout tableLayout = (TableLayout)
	// view.findViewById(R.id.taskTable);
	// // L.out("tableLayout: " + tableLayout);
	// tableLayout.removeAllViews();
	//
	// if (baseDisplayFields != null)
	// for (DisplayField baseDisplayField : baseDisplayFields) {
	// // L.out(count++ + ": " +
	// // baseDisplayField.field.toStringShort());
	// tableLayout.addView(baseDisplayField.getFieldView());
	// }
	// }

	// private void
	// generateFieldViewOld(GetTaskInformationByTaskNumberAndFacilityID task) {
	// // L.out("task: " + task);
	// if (task == null)
	// return;
	//
	// String taskClassID = task.getTskTaskClass();
	// // L.out("taskClassID: " + taskClassID);
	// Field[] field = ((Cache) activity).getTaskField(taskClassID);
	// if (field == null) {
	// MyToast.show("Unable to find fields for taskClassID: " + taskClassID);
	// return;
	// }
	// // printField(field);
	// List<DisplayField> baseDisplayFields = DisplayField.getViews(activity,
	// field, task);
	// baseDisplayFields = updateCustomTable(task, baseDisplayFields);
	// TableLayout tableLayout = (TableLayout)
	// view.findViewById(R.id.taskTable);
	// // L.out("tableLayout: " + tableLayout);
	// tableLayout.removeAllViews();
	//
	// if (baseDisplayFields != null)
	// for (DisplayField baseDisplayField : baseDisplayFields) {
	// // L.out(count++ + ": " +
	// // baseDisplayField.field.toStringShort());
	// tableLayout.addView(baseDisplayField.getFieldView());
	// }
	// }
	// private List<DisplayField>
	// updateCustomTable(GetTaskInformationByTaskNumberAndFacilityID task,
	// List<DisplayField> baseDisplayFields) {
	//
	// List<DisplayField> fields = new
	// ArrayList<DisplayField>(baseDisplayFields);
	//
	// ((TextView)
	// view.findViewById(R.id.patientName)).setText(task.getPatientName());
	// ((TextView)
	// view.findViewById(R.id.dateOfBirth)).setText(task.getPatientDOB());
	// ((TextView)
	// view.findViewById(R.id.medicalRecordNumber)).setText(task.getPatientMRN());
	// TextView textView = (TextView) view.findViewById(R.id.isolation);
	// String isolationPatient = task.getIsolationPatient();
	// if (isolationPatient != null && isolationPatient.equals("Yes"))
	// textView.setTextColor(Color.parseColor("#FF0000"));
	// else
	// textView.setTextColor(Color.parseColor("#000000"));
	// textView.setText(task.getIsolationPatient());
	// ((TextView) view.findViewById(R.id.type)).setText(task.getClassBrief());
	// ((TextView)
	// view.findViewById(R.id.start)).setText(lookUpRoomFromValue(cache,
	// task.getHirStartLocationNode()));
	// ((TextView)
	// view.findViewById(R.id.destination)).setText(lookUpRoomFromValue(cache,
	// task.getHirDestLocationNode()));
	// ((TextView) view.findViewById(R.id.mode)).setText(task.getModeBrief());
	// ((TextView)
	// view.findViewById(R.id.equipment)).setText(task.getEquipmentBrief());
	// String notes = task.getNotes();
	// if (notes != null)
	// notes = notes.toUpperCase();
	// ((TextView) view.findViewById(R.id.notes)).setText(notes);
	//
	// return removeFields(fields);
	// }
	//
	// private List<DisplayField> removeFields(List<DisplayField> fields) {
	// removeField("Patient Name", fields);
	// removeField("Class", fields);
	// removeField("Start", fields);
	// removeField("Destination", fields);
	// removeField("Mode", fields);
	// removeField("Equipment", fields);
	// removeField("Notes", fields);
	// removeField("Patient DOB", fields);
	// return fields;
	// }
	//
	// private void removeField(String string, List<DisplayField> fields) {
	// for (DisplayField baseDisplayField : fields) {
	// // L.out("baseDisplayField.getField(): " +
	// // baseDisplayField.getField());
	// // L.out("baseDisplayField.getField().header: " +
	// // baseDisplayField.getField().header);
	// if (baseDisplayField.header.equals(string)) {
	// fields.remove(baseDisplayField);
	// return;
	// }
	// }
	// L.out("Unable to find: " + string);
	// }
	//

	public static String lookUpRoomFromValue(Cache cache, String value) {
		String ROOMS_SOURCE = "hrcAjaxRoomsSelectByFacilityAsKeyValue";
		L.out("cache: " + cache);
		if (cache == null) {
			L.out("rooms not yet available");
			return value;
		}
		PickList[] picks = cache.getPickListGeography(ROOMS_SOURCE).pickList;
		// L.out("pick" + picks.length);
		// L.out("pick1" + picks[0].textPart + " " + picks[0].valuePart);
		for (int i = 0; i < picks.length; i++) {
			if (picks[i].valuePart.equals(value)) {
				return picks[i].textPart;
			}
		}
		L.out("Unable to find: " + value);
		return value;
	}

	//
	// public void startTimer(String timerName) {
	// L.out("Start Timer: " + timerName);
	// }
	//
	// public void stopTimer(String timerName) {
	// L.out("Stop Timer: " + timerName);
	// }
	//
	// public void setTickled(boolean flag) {
	// L.out("Tickled: " + flag);
	// }
	@Override
	public boolean wantActions() {
		return true;
	}
}
