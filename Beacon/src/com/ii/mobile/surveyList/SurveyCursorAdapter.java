/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.surveyList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.StateChange;
import com.ii.mobile.beacon.fragments.SampleFragment;
import com.ii.mobile.beacon.fragments.WifiListFragment;
import com.ii.mobile.beacon.model.Sample;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.util.L;

public class SurveyCursorAdapter extends ArrayAdapter<Sample> {

	private final FragmentActivity fragmentActivity;

	private final Vibrator vibrator;

	public int lastPick = 0;

	// private final Sample[] items;

	private final SurveyFragment surveyFragment;

	// private Targets[] items = null;

	static class ViewHolder {
		// the views
		protected TextView topView = null;
		@SuppressWarnings("unused")
		private ImageView colorShapeView;
		private TextView view1;
		private TextView view2;
		@SuppressWarnings("unused")
		private ImageView historyShapeView;

		private TextView view3;
		private TextView view4;
		private TextView view5;
		private TextView view6;
		private RelativeLayout topLevel;

		// private TextView view7;

		private ViewHolder cacheViews(View view) {
			// topView = (TextView) view.findViewById(R.id.topView);
			view1 = (TextView) view.findViewById(R.id.view1);
			view2 = (TextView) view.findViewById(R.id.view2);
			view3 = (TextView) view.findViewById(R.id.view3);
			view4 = (TextView) view.findViewById(R.id.view4);
			view5 = (TextView) view.findViewById(R.id.view5);
			view6 = (TextView) view.findViewById(R.id.view6);
			topLevel = (RelativeLayout) view.findViewById(R.id.listItemLayout);
			// view7 = (TextView) view.findViewById(R.id.view7);
			historyShapeView = (ImageView) view.findViewById(R.id.historyShape);
			colorShapeView = (ImageView) view.findViewById(R.id.colorShape);
			// L.out("viewHolder: " + this);
			// L.out("first staff: " + staff);
			return this;
		}
	}

	public SurveyCursorAdapter(FragmentActivity fragmentActivity,
			SurveyFragment surveyFragment,
			int resourceId, Sample[] items) {
		super(fragmentActivity, resourceId, items);
		// this.items = items;
		this.surveyFragment = surveyFragment;
		this.fragmentActivity = fragmentActivity;
		vibrator = (Vibrator) (fragmentActivity.getSystemService(Context.VIBRATOR_SERVICE));
	}

	@Override
	public int getCount() {
		Sample[] samples = surveyFragment.samples;
		return samples.length;
	}

	@Override
	public Sample getItem(int i) {
		Sample[] samples = surveyFragment.samples;
		return samples[i];
	}

	private String trim(String temp, int length) {
		if (temp == null)
			return "";
		if (temp.length() < length)
			return temp;
		return temp.substring(0, length - 1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		final Sample sample = getItem(position);
		// L.out("position: " + position);
		// L.out("target: " + target);
		// createSampleData(target);
		LayoutInflater mInflater = (LayoutInflater) fragmentActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.beacon_item, null);
			holder = new ViewHolder();
			holder.cacheViews(convertView);
			convertView.setTag(holder);

			convertView.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					vibrator.vibrate(200);

					ListView parent = (ListView) (v.getParent());
					int pos = parent.getPositionForView(v);

					Sample sample = getItem(pos);
					// L.out("target: " + target);
					String inspection = getInspection(sample);
					MyToast.show(inspection);
					MyToast.show(inspection);
					return true;
				}

				private String getInspection(Sample sample) {
					return "Sample: \n" + sample.toString();
				}
			});

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					vibrator.vibrate(200);

					ListView parent = (ListView) (v.getParent());
					int pos = parent.getPositionForView(v);
					lastPick = pos;
					L.out("lastPick: " + lastPick);
					v.setSelected(true);
					// MyToast.show("Click-" + pos);
					Sample sample = getItem(pos);
					L.out("sample: " + sample);
					SampleFragment.sampleFragment.setSelection(sample);
					TransportActivity.pageFragmentController.setPosition(1);
				}
			});
		} else
			holder = (ViewHolder) convertView.getTag();

		// 1 - top left, 2 - top middle, 3 - top left, 4 - bottom left, 5 -
		// bottom middle, 6 - bottom right

		String location = "0";
		if (sample.location != null && !sample.location.equals(""))
			location = sample.location;
		holder.view1.setText(trim(location, 20));
		if (sample.event.equals(StateChange.AD_HOC))
			holder.view1.setTextColor(Color.RED);
		else
			holder.view1.setTextColor(Color.BLACK);

		String name = "Name";
		if (sample.name != null && (!sample.name.equals("")
				|| !sample.name.equals(" ")))
			name = sample.name;
		holder.view2.setText(name);

		if (sample.speed == -1)
			holder.view3.setText("Speed");
		else if (sample.testing)
			holder.view3.setTextColor(Color.RED);
		else {
			holder.view3.setTextColor(Color.BLACK);
			holder.view3.setText(sample.speed / 1000. + " kB/s");
		}
		// String speed = "Speed";
		//
		// if (sample.speed == 0)
		// holder.view3.setTextColor(Color.RED);
		// else
		// holder.view3.setTextColor(Color.BLACK);
		//
		// if (sample.speed != -1)
		// speed = sample.speed + "";
		// holder.view3.setText(speed);

		// if (sample.failed)
		// holder.view2.setTextColor(Color.RED);
		// else
		// holder.view2.setTextColor(Color.BLACK);

		String networkName = "";
		if (sample.strength != -1)
			networkName = sample.strength + "";
		holder.view4.setText(trim(networkName, 20));
		holder.view5.setText(WifiListFragment.getNetworkName(sample.type));

		String event = sample.event;
		holder.view6.setText(event);

		String backgroundColor = "#FFFFFF";
		if (sample.failed)
			backgroundColor = "#DCA2CD";
		else if (sample.samples > 0)
			backgroundColor = "#BCED91";
		holder.topLevel.setBackgroundColor(Color.parseColor(backgroundColor));
		return convertView;
	}

	public Sample getNextPick() {
		while (true) {
			Sample[] samples = SurveyFragment.samples;
			lastPick += 1;
			L.out("lastPick: " + lastPick);
			if (lastPick > samples.length - 1) {
				return null;
			}
			Sample sample = samples[lastPick];
			L.out("sample: " + sample);
			L.out("test: " + (sample.samples == 0));

			if (sample.samples == 0)
				return sample;
		}
	}

	@Override
	public void notifyDataSetChanged() {
		L.out("notifyDataSetChanged!");
		super.notifyDataSetChanged();
	}

}
