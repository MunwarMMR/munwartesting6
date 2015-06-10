package com.ii.mobile.legacy;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.ii.mobile.beacon.R;
import com.ii.mobile.actionView.ActionViewFragment;
import com.ii.mobile.cache.Cache;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.SoapDbAdapter;
import com.ii.mobile.soap.StaticSoap;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListRecentTasksByEmployeeID.EmployeeRecentTasksList;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.task.TaskSoap.TaskSoapColumns;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

/**

 */
public class LegacyTaskListFragment extends ListFragment implements NamedFragment {

	private LinearLayout ll;
	EmployeeRecentTasksList[] employeeRecentTasksList;
	public static LegacyTaskListFragment taskListFragment = null;
	private Activity activity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.out("create view");

		ll = (LinearLayout) inflater.inflate(R.layout.frag_list_task, container, false);

		return ll;
	}

	@Override
	public String getTitle() {
		return "Action History";
	}

	// @Override
	// public void onListItemClick(ListView l, View v, int position, long id) {
	// super.onListItemClick(l, v, position, id);
	// MyToast.show("click: " + position);
	// }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		L.out("activity view");
		activity = getActivity();
		employeeRecentTasksList = getTaskList();
		if (employeeRecentTasksList != null)
			setListAdapter(new LegacyTaskListCursorAdapter((FragmentActivity) activity, this, R.layout.frag_list_task, employeeRecentTasksList));

		getListView().setDivider(null);
		getListView().setDividerHeight(0);

		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyToast.show("longclick");

				return true;
			}
		});

		getListView().setFastScrollEnabled(true);
		new LegacyTaskLoader((FragmentActivity) activity, getListAdapter());
		taskListFragment = this;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (getActivity() == null) {
			L.out("Unable to get activity for actionPager!");
			return;
		}
		View view = getActivity().findViewById(R.id.actionPager);
		if (view == null) {
			L.out("Unable to get view for actionPager!");
			return;
		}
		view.setVisibility(View.VISIBLE);
	}

	public GetTaskInformationByTaskNumberAndFacilityID getTask(String taskNumber) {
		L.out("taskNumber: " + taskNumber);
		ValidateUser validateUser = User.getUser().getValidateUser();
		String json = null;
		GetTaskInformationByTaskNumberAndFacilityID task = null;

		json = LoginActivity.getJSon(ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID, activity, taskNumber);
		if (json != null) {
			task = GetTaskInformationByTaskNumberAndFacilityID.getGJon(json);
			task.setMobileUserName(validateUser.getMobileUserName());
			task.setTickled(true);
			// validateUser.setTaskNumber(task.getTaskNumber());
			if (task != null)
				L.out("task.getTaskNumber(): " + task.getTaskNumber());
			else
				L.out("task is null: " + taskNumber);
			return task;
		}
		L.out("*** ERROR failed taskNumber: " + taskNumber);
		return null;
	}

	public void updateSelfTaskFragment(GetTaskInformationByTaskNumberAndFacilityID newTask) {
		Bundle bundle = newTask.getAllNamedValues();
		// SelfTaskFragment selfTaskFragment = (SelfTaskFragment)
		// getActivity().getSupportFragmentManager().findFragmentByTag(SelfTaskFragment.FRAGMENT_TAG);
		LegacySelfTaskFragment selfTaskFragment = LegacySelfTaskFragment.legacySelfTaskFragment;
		if (selfTaskFragment != null) {
			// selfTaskFragment.updateView(newTask.getTskTaskClass(),
			// newTask.getClassBrief(), bundle, ((Cache) getActivity()));
			selfTaskFragment.setTask(newTask, bundle);
		} else {
			L.out("selftaskFragment not found");
		}
	}

	// private String findClassIDByName(TaskClass[] taskClass, String className)
	// {
	// for (int i = 0; i < taskClass.length; i++) {
	// if (className.equals(taskClass[i].brief))
	// return taskClass[i].taskClassID;
	// }
	// L.out("className:" + className);
	// return null;
	// }

	private synchronized EmployeeRecentTasksList[] getTaskList() {
		return ((Cache) activity).getEmployeeRecentTasksList();
	}

	public void updateDataModel(GetTaskInformationByTaskNumberAndFacilityID task, boolean create) {

		L.out("updateDataModel: " + task);
		if (task == null)
			return;
		String facilityID = User.getUser().getFacilityID();
		String employeeID = User.getUser().getEmployeeID();
		String taskNumber = task.getTaskNumber();
		String soapMethod = ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID;

		ContentValues values = new ContentValues();
		values.put(TaskSoapColumns.JSON, task.getJson());
		values.put(TaskSoapColumns.FACILITY_ID, facilityID);
		values.put(TaskSoapColumns.EMPLOYEE_ID, employeeID);
		values.put(TaskSoapColumns.TASK_NUMBER, taskNumber);
		values.put(TaskSoapColumns.SOAP_METHOD, soapMethod);
		if (create)
			values.put(TaskSoapColumns.LOCAL_TASK_NUMBER, taskNumber);
		// long localTaskNumber = L.getLong(taskNumber);
		String[] selectionArgs = new String[] { employeeID, facilityID, taskNumber };
		Intent intent = activity.getIntent();
		intent.setData(Uri.parse("content://" + StaticSoap.AUTHORITY + "/" + soapMethod));
		activity.getContentResolver().update(intent.getData(), values,
				SoapDbAdapter.getWhere(soapMethod, employeeID, facilityID, taskNumber), selectionArgs);
	}

	@Override
	public void update() {
		((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
	}

	public void addTask(GetTaskInformationByTaskNumberAndFacilityID newTask) {
		L.out("task: " + newTask);
		if (employeeRecentTasksList == null) {
			L.out("employeeRecentTasksList is null");
			return;
		}
		if (newTask == null) {
			L.out("task is null");
			return;
		}
		EmployeeRecentTasksList newRecentTask = new EmployeeRecentTasksList();
		newRecentTask.startLocation = ActionViewFragment.lookUpRoomFromValue((Cache) getActivity(), newTask.getHirStartLocationNode());
		newRecentTask.destinationLocation = ActionViewFragment.lookUpRoomFromValue((Cache) getActivity(), newTask.getHirDestLocationNode());
		newRecentTask.taskNumber = newTask.getTaskNumber();
		newRecentTask.taskClass = newTask.getClassBrief();

		LegacyTaskLoader.addTask(newTask);
		employeeRecentTasksList = ((Cache) activity).addRecentTask(newRecentTask);
		((LegacyTaskListCursorAdapter) getListAdapter()).setTaskList(employeeRecentTasksList);
	}

	@Override
	public boolean wantActions() {
		return true;
	}
}