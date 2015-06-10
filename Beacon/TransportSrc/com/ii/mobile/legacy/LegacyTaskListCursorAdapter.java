/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.legacy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ii.mobile.beacon.R;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListRecentTasksByEmployeeID.EmployeeRecentTasksList;
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class LegacyTaskListCursorAdapter extends ArrayAdapter<EmployeeRecentTasksList> {

	private EmployeeRecentTasksList[] employeeRecentTasksList;
	private final FragmentActivity fragmentActivity;
	private final LegacyTaskListFragment taskListFragment;
	private final Vibrator vibrator;

	static class ViewHolder {
		// the views
		protected TextView topView = null;
		@SuppressWarnings("unused")
		private ImageView colorShapeView;
		private TextView view1;
		private TextView view2;
		@SuppressWarnings("unused")
		private ImageView historyShapeView;
		@SuppressWarnings("unused")
		private TextView view3;
		private TextView view4;
		private TextView view5;

		// private TextView view6;
		// private TextView view7;

		private ViewHolder cacheViews(View view) {
			// topView = (TextView) view.findViewById(R.id.topView);
			view1 = (TextView) view.findViewById(R.id.view1);
			view2 = (TextView) view.findViewById(R.id.view2);
			view3 = (TextView) view.findViewById(R.id.view3);
			view4 = (TextView) view.findViewById(R.id.view4);
			view5 = (TextView) view.findViewById(R.id.view5);
			// view6 = (TextView) view.findViewById(R.id.view6);
			// view7 = (TextView) view.findViewById(R.id.view7);
			historyShapeView = (ImageView) view.findViewById(R.id.historyShape);
			colorShapeView = (ImageView) view.findViewById(R.id.colorShape);
			// L.out("viewHolder: " + this);
			// L.out("first staff: " + staff);

			return this;
		}
	}

	public LegacyTaskListCursorAdapter(FragmentActivity fragmentActivity,
			LegacyTaskListFragment taskListFragment,
			int resourceId, EmployeeRecentTasksList[] list) {
		super(fragmentActivity, resourceId, list);
		this.employeeRecentTasksList = list;
		this.fragmentActivity = fragmentActivity;
		this.taskListFragment = taskListFragment;
		vibrator = (Vibrator) (fragmentActivity.getSystemService(Context.VIBRATOR_SERVICE));
	}

	@Override
	public EmployeeRecentTasksList getItem(int i) {
		if (employeeRecentTasksList == null || i < 0 || i > employeeRecentTasksList.length - 1) {
			L.out("Error in getItem: " + i + " " + employeeRecentTasksList);
			return null;
		}
		return employeeRecentTasksList[i];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		final EmployeeRecentTasksList task = getItem(position);
		// L.out("position: " + position + " task: " + task);
		LayoutInflater mInflater = (LayoutInflater) fragmentActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.action_item, null);
			holder = new ViewHolder();
			holder.cacheViews(convertView);
			convertView.setTag(holder);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					vibrator.vibrate(200);
					ListView parent = (ListView) (v.getParent());
					int pos = parent.getPositionForView(v);

					// MyToast.show("Click-" + pos);
					EmployeeRecentTasksList task = getItem(pos);
					GetTaskInformationByTaskNumberAndFacilityID realTask = LegacyTaskLoader.getTask(task.getTaskNumber());
					if (realTask != null)
						taskListFragment.updateSelfTaskFragment(realTask);
					else
						MyToast.show("Task not yet available.\nTry again when not red.");
				}

			});
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.view1.setText(task.getTaskNumber());
		if (LegacyTaskLoader.getTask(task.getTaskNumber()) != null)
			holder.view1.setTextColor(Color.BLACK);
		else
			holder.view1.setTextColor(Color.RED);
		holder.view2.setText(task.getTaskClass());
		holder.view4.setText(task.getStartLocation());
		holder.view5.setText(task.getDestinationLocation());
		GetTaskInformationByTaskNumberAndFacilityID realTask = LegacyTaskLoader.getTask(task.getTaskNumber());
		if (realTask != null)
			holder.view3.setText(realTask.getPatientName());
		else
			holder.view3.setText("");

		return convertView;
	}

	public void setTaskList(EmployeeRecentTasksList[]
			employeeRecentTasksList) {
		this.employeeRecentTasksList = employeeRecentTasksList;
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		L.out("notifyDataSetChanged!");
		L.out("size of list: " + employeeRecentTasksList.length);
	}

}
