package com.ii.mobile.beacon.fragments;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.model.Network;
import com.ii.mobile.beacon.model.Sample;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.speed.SpeedTest;
import com.ii.mobile.surveyList.SurveyFragment;
import com.ii.mobile.util.L;

public class SampleFragment extends Fragment implements NamedFragment
{
	public static SampleFragment sampleFragment;
	RelativeLayout lout;
	Network selected;
	Activity activity;
	private static Sample sample = null;
	private View topLevel = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		selected = WifiListFragment.selected;
		super.onCreate(savedInstanceState);
		sample = new Sample();
		sample.location = "";
		// sample.adHoc = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();
		L.out("onCreateView: " + selected);
		sampleFragment = this;
		topLevel = inflater.inflate(R.layout.sample_fragment, container, false);
		final EditText editText = ((EditText) topLevel.findViewById(R.id.edit_location));
		// TextView.OnEditorActionListener inputListener = new
		// TextView.OnEditorActionListener() {
		//
		// @Override
		// public boolean onEditorAction(TextView editView, int actionId,
		// KeyEvent event) {
		// L.out("rehello: " + editView.getText());
		// // String temp = editView.getText().toString();
		// // temp = temp.replace("\"", "");
		// // sendMessage(temp);
		// // editView.setText("");
		// // InputMethodManager in = (InputMethodManager)
		// // getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		// //
		// in.hideSoftInputFromWindow(chatInputWindow.getApplicationWindowToken(),
		// // InputMethodManager.HIDE_NOT_ALWAYS);
		// // sample = new Sample();
		// // sample.adHoc = true;
		// sample.location = editView.getText().toString();
		// editText.setTextColor(Color.RED);
		// return false;
		// }
		// };
		// editText.setOnEditorActionListener(inputListener);
		return topLevel;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		activity = getActivity();
		update();
	}

	String getNetworkType(boolean state) {
		String type = "";
		if (state)
			type = "Secured";
		else
			type = "Not Secured";

		return type;
	}

	@Override
	public String getTitle() {
		return "Sample View";
	}

	@Override
	public void update() {
		if (sample == null || true)
			return;
		lout = (RelativeLayout) topLevel.findViewById(R.id.lout_networks);

		EditText editText = ((EditText) topLevel.findViewById(R.id.edit_location));
		// if (sample.adHoc)
		// editText.setTextColor(Color.RED);
		// else
		// editText.setTextColor(Color.BLACK);
		editText.setText(sample.location);

		((TextView) topLevel.findViewById(R.id.tv_password)).setText(sample.name);

		TextView speedView = ((TextView) topLevel.findViewById(R.id.tv_mac));
		if (sample.speed == -1)
			speedView.setText("Speed: " + "");
		else if (sample.testing)
			speedView.setTextColor(Color.RED);
		else {
			speedView.setTextColor(Color.BLACK);
			speedView.setText("Speed: " + sample.speed);
		}

		// speedView.setText("Speed: " + sample.speed);

		((TextView) topLevel.findViewById(R.id.tv_frequency)).setText("Strength: " + sample.strength);
		((TextView) topLevel.findViewById(R.id.tv_strength)).setText("Samples: " + sample.samples);
		((TextView) topLevel.findViewById(R.id.tv_throughput)).setText("Type: "
				+ WifiListFragment.getNetworkName(sample.type));
		TextView eventTextView = ((TextView) topLevel.findViewById(R.id.tv_event));
		((TextView) topLevel.findViewById(R.id.tv_event)).setText("Event: "
				+ sample.event);
		if (sample.event.equals(""))
			eventTextView.setTextColor(Color.BLACK);
		else
			eventTextView.setTextColor(Color.RED);
	}

	public void setSelection(Sample sample) {
		SampleFragment.sample = sample;
		update();
	}

	public void selectData() {
		if (sample == null) {
			sample = new Sample();

		}
		sample.getData(activity);
		sample.speed = 0;
		sample.failed = true;
		sample.dateStamp = new GregorianCalendar().getTimeInMillis();
		SurveyFragment.surveyFragment.update();
		new SpeedTest(sample);
		update();
	}

	public void selectSample() {
		selectData();
		EventOutputFragment.receiveMessage("Sample: ", sample.event);
		sample = SurveyFragment.surveyFragment.updateAndNext(sample);
		L.out("selectSample new sample: " + sample);
		// update();
	}

	@Override
	public boolean wantActions() {
		return false;
	}

	public static Sample getSample() {
		if (sample == null)
			sample = new Sample();
		return sample;
	}
}