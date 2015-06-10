package com.ii.mobile.beacon.actions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ii.mobile.actionButtons.ActionFragment;
import com.ii.mobile.beacon.Alert;
import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.database.FlowBinder;
import com.ii.mobile.beacon.model.BeaconController;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.surveyList.SurveyFragment;
import com.ii.mobile.util.L;

/**

 */
public class SampleNameActionFragment extends ActionFragment implements NamedFragment, SyncCallback {
	public final static String FRAGMENT_TAG = "SampleNameActionFragment";

	private View view = null;
	public static boolean running = false;
	private boolean paused;

	private Spinner spin;

	public static SampleNameActionFragment sampleNameActionFragment = null;

	public static int currentPosition = 0;

	public static String[] actionNames;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.out("onCreate");
		// final Bundle args = new Bundle();
		// args.putString("TAG", FRAGMENT_TAG);
		// this.setArguments(args);
		sampleNameActionFragment = this;

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

				// if (Ticker.INSTANCE.demoMode)
				// MyToast.show("Demo Mode Ended");
				// else
				// MyToast.show("Demo Mode Started");
				// Ticker.INSTANCE.demoMode = !Ticker.INSTANCE.demoMode;
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
		view = inflater.inflate(R.layout.beacon_name_actions, container, false);
		addLongClick();

		// Button dataButton = (Button)
		// view.findViewById(R.id.dataButton);
		// dataButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// SampleFragment.sampleFragment.selectData();
		// }
		// });

		spin = (Spinner) view.findViewById(R.id.nameSpinner);

		// Button sampleButton = (Button)
		// view.findViewById(R.id.sampleButton);
		// sampleButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// SampleFragment.sampleFragment.selectSample();
		// }
		// });

		// Button testButton = (Button)
		// view.findViewById(R.id.cancelButton);
		// testButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// if (SurveyFragment.surveyFragment != null) {
		// SurveyFragment.surveyFragment.test();
		// }
		// }
		// });

		L.out("created SampleActionFragment view ");
		// UpdateController.INSTANCE.registerCallback(this,
		// FlowRestService.GET_ACTOR_STATUS);
		createSpinner();

		final EditText editText = ((EditText) view.findViewById(R.id.edit_location));
		TextView.OnEditorActionListener inputListener = new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView editView, int actionId, KeyEvent event) {
				L.out("rehello: " + editView.getText());
				editText.setTextColor(Color.RED);
				hide_keyboard(getActivity(), editView);
				if (editView.getText().length() > 1)
					addToSpinner(editView.getText().toString());
				editView.setText("");
				return false;
			}

		};
		editText.setOnEditorActionListener(inputListener);
		update();
		return view;
	}

	public void addToSpinner(String text) {
		String[] newActionNames = new String[actionNames.length + 1];
		for (int i = 0; i < actionNames.length; i++)
			newActionNames[i + 1] = actionNames[i];
		newActionNames[0] = text;
		actionNames = newActionNames;
		createSpinner();

	}

	public void addToSpinner() {
		// TODO Auto-generated method stub

	}

	public static void hide_keyboard(Context context, View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
		paused = true;
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	@Override
	public void onResume() {
		super.onResume();
		L.out("onResume");
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		paused = false;
		update();
		running = true;
		Alert.resume(getActivity());

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void createSpinner() {
		// MyToast.show("try createSpinner: ");
		if (activity == null)
			return;
		// MyToast.show("did try createSpinner: ");
		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// MyToast.show("beep: " + position);
				setNameSelection(actionNames[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		if (actionNames == null) {
			actionNames = FlowBinder.getSurveys(getActivity());
			if (actionNames == null || actionNames.length == 0) {
				actionNames = new String[1];
				actionNames[0] = "default";
			}
		}
		L.out("createSpinner: " + actionNames.length);
		// for (int i = 0; i < actionNames.length; i++)
		// L.out("className: " + actionNames[i]);

		ArrayAdapter<String> arrayAdapter =
				new ArrayAdapter<String>(activity, R.layout.transport_blue_spinner, actionNames);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spin.setAdapter(arrayAdapter);
		setSpinner(currentPosition);
	}

	protected void setNameSelection(String string) {
		getActivity().setTitle("  Facility: " + string);
		BeaconController.beaconSampleName = string;
		// BeaconController.beaconSample = (BeaconSample)
		// FlowBinder.getGJon(FlowRestService.BEACON_SAMPLE, getActivity());
		BeaconController.beaconSample = null;
		BeaconController.staticSurveysLoad(SurveyFragment.surveyFragment.getActivity());
		if (SurveyFragment.surveyFragment.getListAdapter() != null) {
			SurveyFragment.getSampleList();
			((ArrayAdapter<?>) SurveyFragment.surveyFragment.getListAdapter()).notifyDataSetChanged();
		}
	}

	protected void setSpinner(int position) {
		if (spin == null) {
			L.out("spinner is null for: " + position);
			return;
		}
		L.out("position: " + position + " " + actionNames.length);
		if (actionNames.length > position) {
			L.out("ERROR: changed spinner size: " + actionNames.length + " position: " + position);
			position = 0;
		}
		currentPosition = position;
		// setSpinnerSelectionWithoutCallingListener(spin, position);
		spin.setSelection(position);
	}

	private void setSpinnerSelectionWithoutCallingListener(final Spinner spinner, final int selection) {
		final OnItemSelectedListener onItemSelectedListener = spinner.getOnItemSelectedListener();
		spinner.setOnItemSelectedListener(null);
		spinner.post(new Runnable() {

			@Override
			public void run() {
				L.out("selection: " + selection);
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
	public void callback(GJon gJon, String payloadName) {
		update();
	}

}
