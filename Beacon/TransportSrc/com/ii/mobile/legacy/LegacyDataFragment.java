package com.ii.mobile.legacy;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.payload.StatusWrapper;
import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.SoapDbAdapter;
import com.ii.mobile.soap.StaticSoap;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.task.TaskSoap.TaskSoapColumns;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class LegacyDataFragment extends Fragment {

	public void updateDataModel(GetTaskInformationByTaskNumberAndFacilityID task,
			FragmentActivity fragmentActivity, boolean create) {

		// L.out("updateDataModel: " + task);
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
		Intent intent = fragmentActivity.getIntent();
		intent.setData(Uri.parse("content://" + StaticSoap.AUTHORITY + "/" + soapMethod));
		fragmentActivity.getContentResolver().update(intent.getData(), values,
				SoapDbAdapter.getWhere(soapMethod, employeeID, facilityID, taskNumber), selectionArgs);
	}

	public void setEmployeeStatus(String status, FragmentActivity fragmentActivity, boolean tickled) {
		L.out("setEmployeeStatus: " + status + " " + tickled);
		final String statusId = StaticFlow.INSTANCE.findActorStatusId(status);
		StatusWrapper statusWrapper = UpdateController.INSTANCE.statusWrapper;
		L.out("statusWrapper: " + statusWrapper);
		statusWrapper.currentStatus.employeeStatus = status;
		statusWrapper = UpdateController.INSTANCE.statusWrapper;
		L.out("statusWrapper: " + statusWrapper);
		statusWrapper.currentStatus.actorStatusId = statusId;
		L.out("statusId: " + statusId);
		final StatusWrapper wrapper = statusWrapper;
		// new Thread() {
		// @Override
		// public void run() {
		// boolean foo = Flow.getFlow().actorStatusUpdate(wrapper);
		// L.out("foo: " + foo);
		// }
		// }.start();
		UpdateController.INSTANCE.callback(statusWrapper, UpdateController.STATUS_WRAPPER);
	}

	public void setEmployeeStatusOld(String status, FragmentActivity fragmentActivity, boolean tickled) {
		L.out("setEmployeeStatus: " + status + " " + tickled);
		ValidateUser validateUser = User.getUser().getValidateUser();
		// L.out("before: " + validateUser);
		String oldStatus = validateUser.getEmployeeStatus();
		validateUser.setEmployeeStatus(status);
		// MyToast.show("setEmployeeStatus: " + oldStatus + " to " + status);
		L.out("status: " + oldStatus + " to " + status + " tickled: " + tickled);
		validateUser.setTickled(tickled);
		validateUser.setJson(validateUser.getNewJson());
		// L.out("after: " + validateUser);
		// L.out("**** employee status after: ");
		// validateUser.printJson();
		User.getUser().setValidateUser(ValidateUser.getGJon(validateUser.getJson()));
		// L.out("*** update the updateEmployeeDataModel here! " + status);
		updateEmployeeDataModel(fragmentActivity);
	}

	private void updateEmployeeDataModel(FragmentActivity fragmentActivity) {
		// L.out("update the updateEmployeeDataModel here: "
		// + User.getUser().getValidateUser().getEmployeeStatus());
		String facilityID = User.getUser().getFacilityID();
		String employeeID = User.getUser().getEmployeeID();
		String taskNumber = null;
		String soapMethod = ParsingSoap.VALIDATE_USER;

		ContentValues values = new ContentValues();
		values.put(TaskSoapColumns.JSON, User.getUser().getValidateUser().getJson());
		values.put(TaskSoapColumns.FACILITY_ID, User.getUser().getPassword());
		values.put(TaskSoapColumns.EMPLOYEE_ID, User.getUser().getUsername());
		values.put(TaskSoapColumns.TASK_NUMBER, User.getUser().getPlatform());
		values.put(TaskSoapColumns.SOAP_METHOD, soapMethod);

		String[] selectionArgs = new String[] { employeeID, facilityID, taskNumber };
		Intent intent = fragmentActivity.getIntent();
		intent.setData(Uri.parse("content://" + StaticSoap.AUTHORITY + "/" + soapMethod));
		fragmentActivity.getContentResolver().update(intent.getData(), values,
				SoapDbAdapter.getWhere(soapMethod, employeeID, facilityID, taskNumber), selectionArgs);
	}
}
