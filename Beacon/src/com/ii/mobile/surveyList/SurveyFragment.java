package com.ii.mobile.surveyList;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.model.BeaconController;
import com.ii.mobile.beacon.model.BeaconSample;
import com.ii.mobile.beacon.model.Sample;
import com.ii.mobile.flow.FlowBinder;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.selfAction.SelfActionFragment;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

/**

 */
public class SurveyFragment extends ListFragment implements NamedFragment, SyncCallback {

	public static SurveyFragment surveyFragment;

	private LinearLayout ll;

	private Activity activity;
	public static Sample[] samples;
	private boolean haveListAdapter = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.out("create view");
		ll = (LinearLayout) inflater.inflate(R.layout.transport_list_actions, container, false);
		surveyFragment = this;
		return ll;
	}

	boolean runningTest = false;

	public void test() {
		if (runningTest) {
			runningTest = false;
			BeaconController.beaconSample = null;
			update();
			// if (getListAdapter() != null)
			// ((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
		} else {
			runningTest = true;
			samples = getSampleList();
			for (int i = 0; i < samples.length; i++) {
				Sample sample = samples[i];
				sample.random(getActivity());
				if (getListAdapter() != null)
					((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
				// L.sleep(1000);
			}
		}
	}

	@Override
	public String getTitle() {
		return "Survey List";
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
		// UpdateController.INSTANCE.registerCallback(this,
		// FlowRestService.GET_ACTION_HISTORY);
		// Sample sample = new Sample();
		// sample.location = "xyz100";
		// generateName(new Sample(), sample);
		// sample.location = "32xyz100";
		// generateName(new Sample(), sample);
		// sample.location = "xyz";
		// generateName(new Sample(), sample);
	}

	private void setListAdapter() {
		if (haveListAdapter)
			return;
		samples = getSampleList();
		if (samples != null)
			L.out("items: " + samples.length);
		if (samples != null) {
			haveListAdapter = true;
			L.out("created listAdapter");
			setListAdapter(new SurveyCursorAdapter((FragmentActivity)
					activity, this, R.layout.transport_list_actions, samples));
			getListView().setDivider(null);
			getListView().setDividerHeight(0);
			getListView().setTextFilterEnabled(true);
			// getListView().setSelector(R.drawable.atest);
			// getListView().setOnItemLongClickListener(new
			// OnItemLongClickListener() {
			//
			// @Override
			// public boolean onItemLongClick(AdapterView<?> parent, View view,
			// int position, long id) {
			// MyToast.show("longclick ddd");
			// return true;
			// }
			// });
			getListView().setFastScrollEnabled(true);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// UpdateController.INSTANCE.unRegisterCallback(this,
		// FlowRestService.GET_ACTION_HISTORY);
	}

	public static Sample[] getSampleList() {
		BeaconSample beaconSample = BeaconController.beaconSample;
		if (beaconSample == null) {
			beaconSample = new BeaconSample();
			BeaconController.beaconSample = beaconSample;
		}

		Sample[] newSamples = new Sample[beaconSample.beaconSampleInner.samples.size()];
		int i = 0;
		for (Sample sample : beaconSample.beaconSampleInner.samples) {
			newSamples[i] = sample;
			i += 1;
		}
		samples = newSamples;
		return newSamples;
	}

	@Override
	public void onResume() {
		super.onResume();

		// if (getActivity() == null) {
		// L.out("Unable to get activity for actionPager!");
		// return;
		// }
		// GetActionStatus getActionHistory = UpdateController.getActionHistory;
		// if (getActionHistory == null)
		// return;
		// L.out("actionSelect: " + getActionHistory.getTargets().length);
		// View view = getActivity().findViewById(R.id.actionPager);
		// if (view == null) {
		// L.out("Unable to get view for actionPager!");
		// return;
		// }
		// view.setVisibility(View.VISIBLE);
		update();
	}

	public void updateSelfActionFragment(GetActionStatus getActionStatus) {
		// Bundle bundle = newTask.getAllNamedValues();
		// SelfTaskFragment selfTaskFragment = (SelfTaskFragment)
		// getActivity().getSupportFragmentManager().findFragmentByTag(SelfTaskFragment.FRAGMENT_TAG);
		SelfActionFragment selfActionFragment = SelfActionFragment.selfActionFragment;
		if (selfActionFragment != null) {
			L.out("selfActionFragment not found");
			selfActionFragment.setAction(getActionStatus);
			// selfTaskFragment.updateView(newTask.getTskTaskClass(),
			// newTask.getClassBrief(), bundle, ((Cache) getActivity()));
			// selfTaskFragment.setTask(newTask, bundle);
		} else {
			L.out("selfActionFragment not found");
		}
	}

	@Override
	public void update() {
		// L.out("obsolete update: " + L.p());
		setListAdapter();
		if (getListAdapter() != null)
			((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		// MyToast.show("callback: " + payloadName);
		setListAdapter();
		if (getListAdapter() != null)
			((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public boolean wantActions() {
		return true;
	}

	public Sample updateAndNext(Sample sample) {
		L.out("currentsample: " + sample + " samples.length: " + samples.length);
		Sample newSample = null;

		Sample[] newSamples = new Sample[samples.length + 1];
		for (int i = 0; i < samples.length; i++)
			newSamples[i + 1] = samples[i];
		newSamples[0] = sample;
		samples = newSamples;
		updateSamples();
		newSample = new Sample();
		newSample.location = generateName(newSample, sample);

		L.out("newSample: " + newSample);

		update();
		return newSample;
	}

	private void updateSamples() {
		List<Sample> sampleList = BeaconController.beaconSample.beaconSampleInner.samples;
		sampleList.clear();
		for (int i = 0; i < samples.length; i++)
			sampleList.add(samples[i]);
		FlowBinder.updateLocalDatabase(FlowRestService.BEACON_SAMPLE, BeaconController.beaconSample);
	}

	public static String generateName(Sample newSample, Sample sample) {
		String location = sample.location;
		L.out("old location: " + location);

		String stringPart = location.replaceAll("[0-9]*$", "");
		L.out("stringPart: " + stringPart);

		String numberPart = location.replaceAll(stringPart, "");
		L.out("numberPart: " + numberPart);

		int num = 0;
		try {
			num = Integer.parseInt(numberPart);
		} catch (Exception e) {
		}
		String temp;
		// if (num == -1)
		// temp = location + num;
		temp = stringPart + (num + 1);
		L.out("new location: " + temp);
		return temp;
	}
}