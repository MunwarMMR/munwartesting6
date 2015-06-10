package com.ii.mobile.beacon.actions;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ii.mobile.actionButtons.ActionFragment;
import com.ii.mobile.beacon.Alert;
import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.StateChange;
import com.ii.mobile.beacon.fragments.SampleFragment;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

/**
 
 */
public class SampleActionFragment extends ActionFragment implements NamedFragment, SyncCallback {
	public final static String FRAGMENT_TAG = "SampleActionFragment";

	private View view = null;

	public static boolean running = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.out("onCreate");
	}

	private void addLongClick() {
		// L.out("addedLongClick: " + addedLongClick);
		// MyToast.show("Long click is enabled");
		view.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				// L.out("long view: " + view);
				Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(200);
				return true;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		visible = true;
		if (container == null) {

			return null;
		}
		this.activity = getActivity();
		view = inflater.inflate(R.layout.beacon_actions, container, false);
		addLongClick();

		Button dataButton = (Button)
				view.findViewById(R.id.dataButton);
		dataButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SampleFragment.sampleFragment.selectData();
			}
		});

		Button sampleButton = (Button)
				view.findViewById(R.id.sampleButton);
		sampleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// SampleFragment.sampleFragment.selectSample();
				// SampleFragment.sampleFragment.sample.event = "Sample Saved";
				String message = StateChange.AD_HOC;
				if (StateChange.begin)
					message = StateChange.START_TO_ADHOC;
				if(!TransportActivity.transportActivity.isFinishing())
				new Alert().createDialog(SampleFragment.getSample().location, message, TransportActivity.transportActivity);
				StateChange.begin = false;
			}
		});
		//
		// Button testButton = (Button)
		// view.findViewById(R.id.testButton);
		// testButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (SurveyFragment.surveyFragment != null) {
		// SurveyFragment.surveyFragment.test();
		// }
		// }
		// });
		Button emailButton = (Button)
				view.findViewById(R.id.deleteSampleButton);
		emailButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!TransportActivity.transportActivity.isFinishing())
				new Alert().createDialog(SampleFragment.getSample().location, StateChange.COMPLETE, TransportActivity.transportActivity);

			}
		});

		update();
		L.out("created SampleActionFragment view ");
		// UpdateController.INSTANCE.registerCallback(this,
		// FlowRestService.GET_ACTOR_STATUS);
		return view;
	}

	@Override
	public String getTitle() {
		return "Not Used";
	}

	@Override
	public void update() {
		L.out("SampleActionFragment update: " + visible);
	}

	@Override
	public void onPause() {
		L.out("onPause");
		super.onPause();
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	@Override
	public void onResume() {
		super.onResume();
		L.out("onResume");
		update();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		update();
	}

}
