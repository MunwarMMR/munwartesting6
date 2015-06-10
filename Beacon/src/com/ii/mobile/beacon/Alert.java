package com.ii.mobile.beacon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ii.mobile.beacon.actions.SampleNameActionFragment;
import com.ii.mobile.beacon.actions.SendMail;
import com.ii.mobile.beacon.database.FlowBinder;
import com.ii.mobile.beacon.fragments.SampleFragment;
import com.ii.mobile.beacon.model.BeaconController;
import com.ii.mobile.surveyList.SurveyFragment;
import com.ii.mobile.tab.AudioPlayer;
import com.ii.mobile.util.L;

public class Alert {
	public static Dialog dialog = null;
	private Spinner spin;
	private Activity activity;

	private static String cacheLocation = null;
	private static String cacheMessage = null;
	private static boolean cacheBegin = false;

	public static void resume(Activity activity) {
		// MyToast.show("delayed starting: " + cacheMessage);
		if (cacheLocation != null) {
			// MyToast.show("delayed starting");
			StateChange.lastBegin = cacheBegin;
			new Alert().createDialog(cacheLocation, cacheMessage, activity);
		}
	}

	public void createDialog(final String location, final String message, final Activity activity) {
		// MyToast.show("waiting for transport to start before");
		if (!SampleNameActionFragment.running) {
			// MyToast.show("waiting for transport to start");
			cacheLocation = location;
			cacheMessage = message;
			cacheBegin = StateChange.lastBegin;
			return;
		}
		this.activity = activity;
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {

				AudioPlayer.INSTANCE.playSound(AudioPlayer.ERROR);
				L.out("dialog: " + dialog);
				if (dialog != null && dialog.isShowing()) {
					dialog.setTitle(message);
					SampleFragment.getSample().event = message;
					// MyToast.show("showing");
					// dialog.hide();
					return;
				}

				dialog = new Dialog(activity);
				dialog.setContentView(R.layout.dialog);

				SampleFragment.getSample().event = message;
				// L.out("alert: " + SampleFragment.getSample());
				final EditText editText = ((EditText) dialog.findViewById(R.id.edit_location));
				final EditText editFacilityText = ((EditText) dialog.findViewById(R.id.edit_facility));

				TextView.OnEditorActionListener inputListener = new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView editView, int actionId, KeyEvent event) {
						L.out("rehello: " + editView.getText());
						// String temp = editView.getText().toString();
						// temp = temp.replace("\"", "");
						// sendMessage(temp);s
						// editView.setText("");
						// InputMethodManager in = (InputMethodManager)
						// activity.getSystemService(Context.INPUT_METHOD_SERVICE);
						// in.hideSoftInputFromWindow(activity.getApplicationWindowToken(),
						// InputMethodManager.HIDE_NOT_ALWAYS);
						SampleFragment.getSample().location = editView.getText().toString();
						// SampleFragment.sampleFragment.sample.adHoc = true;
						// SampleFragment.sample.location =
						// editView.getText().toString();
						editText.setTextColor(Color.RED);
						return false;
					}
				};
				editText.setOnEditorActionListener(inputListener);

				if (location != null)
					((EditText) dialog.findViewById(R.id.edit_location)).setText(location);
				dialog.setTitle(message);

				((Button) dialog.findViewById(R.id.okButton)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						dialog = null;
						hide_keyboard(activity, editText);
						hide_keyboard(activity, editFacilityText);
						// if (editFacilityText.length() > 0) {
						// if (editFacilityText.getText().length() > 0)
						// addToSpinner(editFacilityText.getText().toString());
						// editFacilityText.setText("");
						// }
						SampleFragment.getSample().location = editText.getText().toString();
						String event = SampleFragment.getSample().event;
						SampleFragment.sampleFragment.selectSample();
						L.out("SampleFragment.getSample(): " + SampleFragment.getSample().event);
						if (event.equals(StateChange.COMPLETE))
							SendMail.sendMail(activity);
						dialog = null;
					}
				});
				((Button) dialog.findViewById(R.id.cancelButton)).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						dialog = null;
					}
				});
				L.out("lastbegin: " + StateChange.lastBegin);
				L.out("begin: " + StateChange.begin);
				String event = SampleFragment.getSample().event;
				if (event == null)
					event = "";
				if (!StateChange.lastBegin || event.equals(StateChange.COMPLETE))
					dialog.findViewById(R.id.listItemLayout).setVisibility(View.GONE);
				else {
					spin = (Spinner) dialog.findViewById(R.id.nameSpinner);
					createSpinner(activity);
				}

				TextView.OnEditorActionListener inputFacilityListener = new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView editView, int actionId, KeyEvent event) {
						L.out("rehello: " + editView.getText() + " " + actionId);
						editFacilityText.setTextColor(Color.RED);
						// SampleNameActionFragment.currentPosition = actionId;
						if (editFacilityText.getText().length() > 0)
							addToSpinner(editView.getText().toString());
						editFacilityText.setText("");
						return false;
					}

				};
				editFacilityText.setOnEditorActionListener(inputFacilityListener);
				resize(activity, dialog);
				dialog.show();
				StateChange.begin = false;
			}
		});
	}

	private void addToSpinner(String text) {
		String[] newActionNames = new String[SampleNameActionFragment.actionNames.length + 1];
		for (int i = 0; i < SampleNameActionFragment.actionNames.length; i++)
			newActionNames[i + 1] = SampleNameActionFragment.actionNames[i];
		newActionNames[0] = text;
		SampleNameActionFragment.actionNames = newActionNames;
		createSpinner(activity);

		SampleNameActionFragment sampleNameActionFragment = SampleNameActionFragment.sampleNameActionFragment;
		// SampleNameActionFragment sampleNameActionFragment =
		// (SampleNameActionFragment)
		// activity.getSupportFragmentManager().findFragmentByTag(SampleNameActionFragment.FRAGMENT_TAG);
		L.out("sampleNameActionFragment: " + sampleNameActionFragment);
		if (sampleNameActionFragment != null)
			sampleNameActionFragment.createSpinner();
	}

	public static void hide_keyboard(Context context, View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	private void createSpinner(final Activity activity) {
		// MyToast.show("try createSpinner: ");
		if (activity == null)
			return;
		// MyToast.show("did try createSpinner: ");
		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// MyToast.show("beep: " + position);
				setNameSelection(SampleNameActionFragment.actionNames[position], activity);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		if (SampleNameActionFragment.actionNames == null) {
			SampleNameActionFragment.actionNames = FlowBinder.getSurveys(activity);
			if (SampleNameActionFragment.actionNames == null
					|| SampleNameActionFragment.actionNames.length == 0) {
				SampleNameActionFragment.actionNames = new String[1];
				SampleNameActionFragment.actionNames[0] = "default";
			}
		}
		L.out("createSpinner: " + SampleNameActionFragment.actionNames.length);
		// for (int i = 0; i < actionNames.length; i++)
		// L.out("className: " + actionNames[i]);

		ArrayAdapter<String> arrayAdapter =
				new ArrayAdapter<String>(activity, R.layout.transport_blue_spinner, SampleNameActionFragment.actionNames);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spin.setAdapter(arrayAdapter);
		setSpinner(SampleNameActionFragment.currentPosition);
	}

	protected void setSpinner(int position) {
		if (spin == null) {
			L.out("spinner is null for: " + position);
			return;
		}
		L.out("position: " + position + " " + SampleNameActionFragment.actionNames.length);
		if (SampleNameActionFragment.actionNames.length > position) {
			L.out("ERROR: changed spinner size: " + SampleNameActionFragment.actionNames.length
					+ " position: " + position);
			position = 0;
		}
		SampleNameActionFragment.currentPosition = position;
		// setSpinnerSelectionWithoutCallingListener(spin, position);
		spin.setSelection(position);
	}

	protected void setNameSelection(String string, Activity activity) {
		activity.setTitle("  Facility: " + string);
		BeaconController.beaconSampleName = string;
		// BeaconController.beaconSample = (BeaconSample)
		// FlowBinder.getGJon(FlowRestService.BEACON_SAMPLE, getActivity());
		BeaconController.beaconSample = null;
		BeaconController.staticSurveysLoad(activity);
		if (SurveyFragment.surveyFragment.getListAdapter() != null) {
			SurveyFragment.getSampleList();
			((ArrayAdapter<?>) SurveyFragment.surveyFragment.getListAdapter()).notifyDataSetChanged();
		}
	}

	public void resize(Activity activity, Dialog dialog) {
		Rect displayRectangle = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		// int width =
		// getResources().getDimensionPixelSize(R.dimen.popup_width);
		// int height =
		// getResources().getDimensionPixelSize(R.dimen.popup_height);

		int width = ((displayRectangle.width()));
		int height = ((displayRectangle.height()));
		L.out("real width: " + width);
		L.out("real height: " + height);
		if (width == 0)
			width = 1100;
		if (height == 0)
			height = 2000;
		width = ((int) (width * 0.85f));

		if (StateChange.lastBegin)
			height = ((int) (height * 0.60f));
		else
			height = ((int) (height * 0.40f));
		L.out("width: " + width);
		L.out("height: " + height);
		dialog.getWindow().setLayout(width, height);
	}

}