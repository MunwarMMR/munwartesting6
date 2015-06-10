package com.ii.mobile.flow.types;

import com.ii.mobile.flow.FlowBinder;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.util.L;

public class GetActionHistory extends GetActionStatus {
	public void addTarget(Targets target) {
		L.out("adding target: " + target.actionNumber + " " + target.actionStatus.selfTask);
		if (target.actionStatus.selfTask)
			return;
		if (alreadyHave(target))
			return;

		add(target);
		FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTION_HISTORY, this);
		UpdateController.INSTANCE.doCallback(this, FlowRestService.GET_ACTION_HISTORY);
	}

	static public GetActionHistory getGJon(String json) {
		// L.out("GetActionStatus: " + "");
		// PrettyPrint.prettyPrint(json, false);
		// GetActionStatus getActionStatus = (GetActionStatus)
		// getJSonObject(json, GetActionStatus.class);
		json = json.replace("\"isolation\":{},", "");
		GetActionHistory getActionHistory = (GetActionHistory) getJSonObjectArray(json, GetActionHistory.class);
		// getActionStatus.json = null;
		if (getActionHistory == null)
			return null;
		getActionHistory.getActionStatusInner.init();
		// L.out("output: " + getActionStatus.getNewJson());
		return getActionHistory;
	}

	private final int MAX_ACTIONS = 25;

	private void add(Targets target) {
		L.out("targets: " + target.actionNumber);
		Targets[] targets = getTargets();
		int length = targets.length + 1;
		if (length > MAX_ACTIONS)
			length = MAX_ACTIONS;
		Targets[] newTargets = new Targets[length];
		for (int i = 1; i < length; i++) {
			newTargets[i] = targets[i - 1];
		}
		newTargets[0] = target;
		getActionStatusInner.targets = newTargets;
	}

	private boolean alreadyHave(Targets target) {
		Targets[] targets = getTargets();
		L.out("targets: " + targets.length);
		for (int i = 0; i < targets.length; i++) {
			Targets historyTarget = targets[i];
			// L.out(i + " targets: " + historyTarget.actionNumber + " " +
			// target.actionNumber);
			if (historyTarget.actionNumber != null && historyTarget.actionNumber.equals(target.actionNumber)) {
				return true;
			}
		}
		L.out("didn't already have: " + target.actionNumber);
		return false;
	}

	public void reverseTargets() {
		// L.out("original");
		// printTargets();
		Targets[] targets = getTargets();
		Targets[] newTargets = new Targets[targets.length];
		for (int i = 0; i < targets.length; i++) {
			newTargets[i] = targets[targets.length - i - 1];
		}
		getActionStatusInner.targets = newTargets;
		// L.out("reversed");
		// printTargets();
	}

	public void printTargets() {
		Targets[] targets = getTargets();

		for (int i = 0; i < targets.length; i++) {
			Targets historyTarget = targets[i];
			L.out(i + " targets: " + historyTarget.actionNumber + " " + historyTarget.localActionId);
		}
	}

	public void addAction(GetActionStatus getActionStatus) {
		if (alreadyHave(getActionStatus.getTarget()))
			return;
		if (!replaceTarget(getActionStatus))
			add(getActionStatus.getTarget());
		FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTION_HISTORY, this);
		UpdateController.INSTANCE.doCallback(this, FlowRestService.GET_ACTION_HISTORY);
	}

	private boolean replaceTarget(GetActionStatus getActionStatus) {
		Targets newTarget = getActionStatus.getTarget();
		// L.out("getActionStatus.getLocalActionId(): " +
		// getActionStatus.getLocalActionId());
		Targets[] targets = getTargets();
		L.out("targets: " + targets.length);
		for (int i = 0; i < targets.length; i++) {
			Targets historyTarget = targets[i];
			// L.out(i + " targets: " + historyTarget.localActionId + " " +
			// newTarget.localActionId);
			if (historyTarget.localActionId != null
					&& historyTarget.localActionId.equals(newTarget.localActionId)) {
				L.out("replaced: " + newTarget.localActionId);
				targets[i] = newTarget;
				getActionStatusInner.targets = targets;
				// printTargets();
				return true;
			}
		}
		return false;
	}
}