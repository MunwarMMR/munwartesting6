package com.ii.mobile.flow;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.Event;
import com.ii.mobile.flow.types.GetActionHistory;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId;
import com.ii.mobile.flow.types.SelectLocations;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.payload.PlainString;
import com.ii.mobile.soap.ContentValues;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.util.L;

public class Flow extends BaseFlow {

	public static boolean wantCallBack = false;

	public static Flow getFlow() {
		if (flow == null) {
			flow = new Flow();
		}
		return (Flow) flow;
	}

	static final int MAX_TRY = 3;

	public boolean actorStatusUpdate(GetActorStatus getActorStatus) {
		String METHOD_NAME = FlowRestService.ACTOR_STATUS_UPDATE;

		String result = new FlowRestService().execute(METHOD_NAME, getActorStatus);
		// L.out("result: " + result);
		// String pretty = PrettyPrint.prettyPrint(result);
		if (result == null)
			return false;
		if (wantCallBack)
			UpdateController.INSTANCE.callback(new PlainString("Actor Status Update: " + result), UpdateController.PLAIN_STRING);
		JSONObject jSonObject = flowParser(result, METHOD_NAME);
		if (jSonObject == null) {
			L.out("JsonObject is null");
			return false;
		}
		// MyToast.show("actorStatusUpdate: " + jSonObject);
		return true;
	}

	// public boolean actionStatusUpdate(GetActorStatus getActorStatus) {
	// String METHOD_NAME = FlowRestService.ACTION_STATUS_UPDATE;
	// String actionStatusId = getActorStatus.getActionStatusId();
	//
	// getActorStatus.setActionStatusId(actionStatusId);
	// L.out("statusWrapper: " + actionStatusId);
	// String result = new FlowRestService().execute(METHOD_NAME,
	// getActorStatus);
	// // L.out("result: " + result);
	// // String pretty = PrettyPrint.prettyPrint(result);
	// if (wantCallBack)
	// UpdateController.INSTANCE.doCallback(new
	// PlainString("Action Status Update: " + result),
	// UpdateController.PLAIN_STRING);
	// JSONObject jSonObject = flowParser(result, METHOD_NAME);
	// if (jSonObject == null) {
	// L.out("JsonObject is null");
	// return false;
	// }
	//
	// return true;
	//
	// }

	public boolean actionStatusUpdate(GetActionStatus getActionStatus, Event event) {
		String METHOD_NAME = FlowRestService.ACTION_STATUS_UPDATE;
		// String actionStatusId = getActorStatus.getActionStatusId();
		//
		// getActorStatus.setActionStatusId(actionStatusId);
		L.out("getActionStatus: " + getActionStatus.getActionId() + " status: "
				+ getActionStatus.getActionStatusId());
		String result = new FlowRestService().execute(getActionStatus, event);
		// L.out("result: " + result);
		// String pretty = PrettyPrint.prettyPrint(result, true);
		if (result == null)
			return false;
		JSONObject jSonObject = flowParser(result, METHOD_NAME);
		if (jSonObject == null) {
			L.out("JsonObject is null");
			return false;
		}

		return true;

	}

	// public boolean actionStatusUpdate(GetActionStatus getActionStatus) {
	// String METHOD_NAME = FlowRestService.ACTION_STATUS_UPDATE;
	// // String actionStatusId = getActorStatus.getActionStatusId();
	// //
	// // getActorStatus.setActionStatusId(actionStatusId);
	// L.out("getActionStatus: " + getActionStatus.getActionId() + " status: "
	// + getActionStatus.getActionStatusId());
	// String result = new FlowRestService().execute(METHOD_NAME,
	// getActionStatus);
	// // L.out("result: " + result);
	// // String pretty = PrettyPrint.prettyPrint(result);
	// if (wantCallBack)
	// UpdateController.INSTANCE.callback(new
	// PlainString("Action Status Update: " + result),
	// UpdateController.PLAIN_STRING);
	// JSONObject jSonObject = flowParser(result, METHOD_NAME);
	// if (jSonObject == null) {
	// L.out("JsonObject is null");
	// return false;
	// }
	//
	// return true;
	//
	// }

	// public void signOn(AzureToken azureToken) {
	// int tryCount = 0;
	// boolean loop = true;
	// while (loop) {
	// new FlowRestService().login(azureToken.userName,
	// azureToken.authenticationToken);
	// // L.out("Login: " + Login.INSTANCE.toString());
	// UpdateController.INSTANCE.callback(new PlainString("Login: "
	// + Login.INSTANCE.toString().replaceAll("\n", "<br/>").replaceAll(" ",
	// "&nbsp;")), UpdateController.PLAIN_STRING);
	// tryCount += 1;
	//
	// if (Login.cookie != null || tryCount == MAX_TRY)
	// loop = false;
	// }
	// // if (Login.INSTANCE.cookie != null)
	// // new UnitTest().unitTest();
	// }

	// public boolean signOnWithPassword(String userName, String password) {
	// int tryCount = 0;
	// boolean loop = true;
	// while (loop) {
	// // new FlowRestService().loginWithPassword(userName, password);
	// // L.out("Login: " + Login.INSTANCE.toString());
	//
	// tryCount += 1;
	// // if (Login.cookie != null || tryCount == MAX_TRY)
	// loop = false;
	// }
	// // if (Login.cookie != null)
	// Login.userName = userName;
	// // if (Login.cookie == null)
	// // return false;
	// return setStatusToAvailable();
	// }

	public boolean signOnWithPassword(String userName, String password) {
		int tryCount = 0;
		boolean loop = true;
		while (loop) {
			new FlowRestService().loginWithPassword(userName, password);
			// L.out("Login: " + Login.INSTANCE.toString());

			tryCount += 1;
			// if (Login.cookie != null || tryCount == MAX_TRY)
			loop = false;
		}
		if (Login.cookie != null)
			Login.userName = userName;
		if (Login.cookie == null)
			return false;
		return setStatusToAvailable();
	}

	private boolean setStatusToAvailable() {
		GetActorStatus getActorStatus = getActorStatus("foo");
		if (getActorStatus == null) {
			L.out("ERROR: could not get actorStatus!");
			return false;
		}

		L.out("checking if not in: " + getActorStatus);
		L.out("getActorStatus.getActorStatusId(): " + getActorStatus.getActorStatusId());
		if (getActorStatus.getActorStatusId() != null
				&& getActorStatus.getActorStatusId().equals(StaticFlow.ACTOR_NOT_IN)) {
			getActorStatus.setActorStatusId(StaticFlow.ACTOR_AVAILABLE);
			// setting ourself, should be false.
			getActorStatus.tickled = GJon.TRUE_STRING;
			UpdateController.getActorStatus = getActorStatus;
			FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTOR_STATUS, getActorStatus);
			L.out("setting to available");
			actorStatusUpdate(getActorStatus);
		}
		// will get after static load
		// UpdateController.INSTANCE.callback(getActorStatus,
		// FlowRestService.GET_ACTOR_STATUS);
		return true;
	}

	public boolean signOff() {
		GetActorStatus getActorStatus = getActorStatus("foo");
		if (getActorStatus == null) {
			L.out("ERROR: could not get actorStatus!");
			return false;
		}
		L.out("checking if signOff");
		if (getActorStatus.getActorStatusId() != null
				&& getActorStatus.getActorStatusId().equals(StaticFlow.ACTOR_AVAILABLE)) {
			getActorStatus.setActorStatusId(StaticFlow.ACTOR_NOT_IN);
			L.out("setting to available");
			boolean result = actorStatusUpdate(getActorStatus);
			if (result)
				return result;
		} else {
			MyToast.show("Unable to logout with an Action or on Break!");
		}
		return false;
		// StatusWrapper statusWrapper = getActorStatus.inject();
		// if (statusWrapper.currentStatus.actorStatusId != null
		// &&
		// statusWrapper.currentStatus.actorStatusId.equals(StaticFlow.ACTOR_AVAILABLE))
		// {
		// statusWrapper.currentStatus.actorStatusId = StaticFlow.ACTOR_NOT_IN;
		// L.out("setting to Not In");
		// actorStatusUpdate(statusWrapper);
		// } else {
		// MyToast.show("Unable to logout with an Action or on Break!");
		// }
	}

	public ValidateUser validateUserLogin(String username, String pin) {
		// String METHOD_NAME = "JSON_MobileValidateUser";
		String METHOD_NAME = "MobileValidateUser";
		ContentValues contentValues = new ContentValues();
		contentValues.put("UserName", username);
		contentValues.put("PIN", pin);

		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);

		if (jSonObject == null)
			return null;
		return ValidateUser.getGJon(jSonObject.toString());
	}

	public boolean sendMessage(String to, String message) {
		String METHOD_NAME = FlowRestService.SEND_MESSAGE;
		String result = new FlowRestService().execute(METHOD_NAME, to, message);
		L.out("result: " + result);
		if (result != null)
			return true;
		return false;
	}

	public String createAction(GetActionStatus getActionStatus) {
		String METHOD_NAME = FlowRestService.CREATE_ACTION;
		L.out("creating action");
		String result = new FlowRestService().execute(METHOD_NAME, getActionStatus, true);
		// L.out("result: " + result);

		JSONObject jSonObject = flowParser(result, FlowRestService.GET_ACTION_STATUS);
		// L.out("jSonObject: " + jSonObject);

		if (jSonObject == null) {
			L.out("JsonObject is null");
			return null;
		}
		// GetActionStatus actionStatus =
		// GetActionStatus.getGJon(jSonObject.toString());
		return jSonObject.toString();
	}

	// call for the tickler
	public GetActorStatus getStatus() {
		String METHOD_NAME = FlowRestService.GET_ACTOR_STATUS;

		String result = new FlowRestService().execute(METHOD_NAME, null, null);
		// L.out("result: " + result);

		JSONObject jSonObject = flowParser(result, METHOD_NAME);

		if (jSonObject == null) {
			L.out("JsonObject is null");
			return null;
		}
		GetActorStatus getActorStatus = GetActorStatus.getGJon(jSonObject.toString());
		// L.out("getFlowStatus: " + getActorStatus);
		if (getActorStatus == null
				|| !getActorStatus.isValidated()
				|| getActorStatus.getActorId() == null) {
			L.out("ERROR: getActorStatus is null!");
			return null;

		}

		return getActorStatus;
	}

	public GetActorStatus getActorStatus(String employeeID) {
		String METHOD_NAME = FlowRestService.GET_ACTOR_STATUS;

		String result = new FlowRestService().execute(METHOD_NAME, null, null);
		// L.out("result: " + result);

		JSONObject jSonObject = flowParser(result, METHOD_NAME);

		if (jSonObject == null) {
			L.out("JsonObject is null");
			return null;
		}
		GetActorStatus getActorStatus = GetActorStatus.getGJon(jSonObject.toString());
		// L.out("getFlowStatus: " + getActorStatus);
		if (getActorStatus == null
				|| !getActorStatus.isValidated()
				|| getActorStatus.getActorId() == null) {
			L.out("ERROR: getActorStatus is null!");
			return null;

		}

		GetActorStatus getNewActorStatus = getActorStatus;
		if (false && getNewActorStatus.isDifferent(UpdateController.getActorStatus)) {
			L.out("is different");
			getActorStatus.tickled = GJon.TRUE_STRING;
			UpdateController.getActorStatus = getActorStatus;
			FlowBinder.updateLocalDatabase(METHOD_NAME, getActorStatus);
		}

		return getActorStatus;
	}

	public SelectLocations selectLocations(String facilityId) {
		String METHOD_NAME = FlowRestService.SELECT_LOCATIONS;
		String result = new FlowRestService().execute(METHOD_NAME, facilityId, null);
		// String result = Sample.INSTANCE.sample;
		JSONObject jSonObject = flowParser(result, METHOD_NAME);
		if (jSonObject == null) {
			L.out("JsonObject is null");
			return null;
		}

		// L.out("selectLocations: " + result);
		SelectLocations selectLocations = SelectLocations.getGJon(jSonObject.toString());
		if (wantCallBack && selectLocations != null) {
			selectLocations.json = null;
			String temp = selectLocations.toString().replaceAll("\n", "<br>");
			temp = temp.replaceAll(" ", "&nbsp;");
			UpdateController.INSTANCE.callback(new PlainString("selectLocations: \n"
					+ temp), UpdateController.PLAIN_STRING);
		}
		if (selectLocations != null)
			FlowBinder.updateLocalDatabase(METHOD_NAME, selectLocations);
		else
			L.out("ERROR: selectLocations is null!");
		return selectLocations;
	}

	public GetActionStatus getActionStatus(String actionId) {
		String METHOD_NAME = FlowRestService.GET_ACTION_STATUS;
		String result = new FlowRestService().execute(METHOD_NAME, actionId, null);
		// L.out("result: " + result);

		// String pretty = PrettyPrint.prettyPrint(result, true);
		if (wantCallBack)
			UpdateController.INSTANCE.callback(new PlainString("Action Status: " + result), UpdateController.PLAIN_STRING);
		JSONObject jSonObject = flowParser(result, METHOD_NAME);
		if (jSonObject == null) {
			L.out("JsonObject is null");
			return null;
		}
		// PrettyPrint.prettyPrint(jSonObject.toString(), true);
		GetActionStatus getActionStatus = GetActionStatus.getGJon(jSonObject.toString());
		// L.out("getActionStatus: " + getActionStatus);
		if (getActionStatus == null) {
			L.out("getActionStatus is null");
			return null;
		}

		if (wantCallBack) {
			String temp = getActionStatus.toString().replaceAll("\n", "<br>");
			temp = temp.replaceAll(" ", "&nbsp;");
			UpdateController.INSTANCE.callback(new PlainString("getActionStatus: \n"
					+ temp), UpdateController.PLAIN_STRING);
		}
		// L.out("finished");a
		return getActionStatus;
	}

	public GetActionHistory getActionHistory(GetActorStatus getActorStatus) {
		String METHOD_NAME = FlowRestService.GET_ACTION_HISTORY;
		String result = new FlowRestService().execute(METHOD_NAME, getActorStatus);
		L.out("getActionHistory: ");
		// PrettyPrint.prettyPrint(result, true);

		JSONObject jSonObject = flowParser(result, FlowRestService.GET_ACTION_STATUS);
		// L.out("jSonObject: " + jSonObject);
		if (jSonObject == null) {
			L.out("JsonObject is null");
			return null;
		}
		// PrettyPrint.prettyPrint(jSonObject.toString(), true);
		GetActionHistory getActionStatusHistory = GetActionHistory.getGJon(jSonObject.toString());
		// L.out("getActionStatus: " + getActionStatus);
		if (getActionStatusHistory == null) {
			L.out("getActionStatusHistory is null");
			return null;
		}
		getActionStatusHistory.reverseTargets();
		// if (wantCallBack) {
		// String temp = getActionStatus.toString().replaceAll("\n", "<br>");
		// temp = temp.replaceAll(" ", "&nbsp;");
		// UpdateController.INSTANCE.doCallback(new
		// PlainString("getActionStatus: \n"
		// + temp), UpdateController.PLAIN_STRING);
		// }
		FlowBinder.updateLocalDatabase(METHOD_NAME, getActionStatusHistory);
		return getActionStatusHistory;
	}

	public SelectClassTypesByFacilityId selectClassTypes(String facilityId) {
		String METHOD_NAME = FlowRestService.SELECT_CLASS_TYPES_BY_FACILITY_ID;
		String result = new FlowRestService().execute(METHOD_NAME, facilityId, null);
		// String result = Sample.INSTANCE.sample;
		JSONObject jSonObject = flowParser(result, METHOD_NAME);
		// if (jSonObject != null)
		// L.out("jSonObject: " + jSonObject.toString());
		if (jSonObject == null) {
			L.out("JsonObject is null");
			return null;
		}

		SelectClassTypesByFacilityId selectClassTypesByFacilityId = SelectClassTypesByFacilityId.getGJon(jSonObject.toString());
		// L.out("SelectClassTypesByFacilityId: " +
		// selectClassTypesByFacilityId);
		if (selectClassTypesByFacilityId != null)
			FlowBinder.updateLocalDatabase(METHOD_NAME, selectClassTypesByFacilityId);
		else
			L.out("ERROR: selectClassTypesByFacilityId is null!");
		return selectClassTypesByFacilityId;
	}

	private JSONObject flowParser(String result, String methodName) {
		try {
			JSONArray foo = parseJSONArray(result);
			// L.out("foo: " + foo);
			JSONObject bar = (JSONObject) foo.get(0);
			// L.out("bar: " + bar.getClass().getSimpleName() + " - " + bar);
			JSONObject top = new JSONObject();
			// L.out("top: " + top.toString());
			top.accumulate(methodName + "Inner", bar);
			// L.out("top: " + top.toString());
			return top;
		} catch (Exception e) {
			L.out("*** ERROR parsing: " + e + "\nresult: " + result + " " + methodName);
		}
		return null;
	}

	public static JSONArray parseJSONArray(String temp) {
		// L.out("temp: " + temp);
		JSONArray JSONArray = null;
		try {
			JSONArray = new JSONArray(temp);

		} catch (Exception e) {
			L.out("*** ERROR parsing: " + temp);
		}
		return JSONArray;
	}

}
