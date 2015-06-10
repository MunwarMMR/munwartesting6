package com.ii.mobile.paging;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.actions.SampleActionFragment;
import com.ii.mobile.beacon.actions.SampleNameActionFragment;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class ActionFragmentController extends FragmentController implements SyncCallback {

	public ActionFragmentController(Activity activity) {
		super(activity);
		L.out("create ActionFragmentController");
	}

	public void update() {

	}

	@Override
	protected void initialize() {
		currentFragment = Fragment.instantiate(fragmentActivity, SampleActionFragment.class.getName());
		fragments.add(currentFragment);
		currentFragment = Fragment.instantiate(fragmentActivity, SampleNameActionFragment.class.getName());
		fragments.add(currentFragment);

		// fragments.add(Fragment.instantiate(fragmentActivity,
		// StatsFragment.class.getName()));

		sliderAdapter = new SliderAdapter(fragmentActivity.getSupportFragmentManager(), fragments);
		ViewPager pager = (ViewPager) fragmentActivity.findViewById(R.id.actionPager);

		pager.setAdapter(sliderAdapter);
	}

	// public void startTask() {
	// if (timerFragment == null) {
	// L.out("ERROR: timerFragment is null! shouldn't happen");
	// return;
	// }
	// timerFragment.startTask();
	// }

	@Override
	public void setPosition(int position) {
		L.out("setPosition: " + position);
		NamedFragment namedFragment = (NamedFragment) fragments.get(position);
		L.out("setPosition : " + namedFragment.getTitle());
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		// L.out("callback: " + gJon.getClass().getSimpleName());
		update();
	}
}
