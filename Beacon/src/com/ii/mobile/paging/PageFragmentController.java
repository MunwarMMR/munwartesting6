package com.ii.mobile.paging;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.StrengthReciever;
import com.ii.mobile.beacon.fragments.EventOutputFragment;
import com.ii.mobile.beacon.fragments.WifiListFragment;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.fragments.RadioFragment;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.surveyList.SurveyFragment;
import com.ii.mobile.util.L;

public class PageFragmentController extends FragmentController {
	private int focusedPage = TransportActivity.ACTION_VIEW_PAGE;
	private TransportActivity transportActivity = null;

	public PageFragmentController(Activity activity) {
		super(activity);
		this.transportActivity = (TransportActivity) activity;
		// MyToast.make(activity);
	}

	@Override
	protected void initialize() {

		Fragment fragment = Fragment.instantiate(fragmentActivity, EventOutputFragment.class.getName());
		fragments.add(fragment);

		currentFragment = Fragment.instantiate(fragmentActivity, WifiListFragment.class.getName());
		fragments.add(currentFragment);
		fragments.add(Fragment.instantiate(fragmentActivity, SurveyFragment.class.getName()));

		sliderAdapter = new SliderAdapter(fragmentActivity.getSupportFragmentManager(), fragments);

		ViewPager pager = (ViewPager) fragmentActivity.findViewById(R.id.viewpager);
		pager.setAdapter(sliderAdapter);

		pager.setOnPageChangeListener(new MyPageChangeListener());
		L.out("focusedPage: " + focusedPage);
		// int position = focusedPage;
		// pager.setCurrentItem(position);
		// setRadioPosition(position);
		fragmentActivity.registerReceiver(new StrengthReciever(),
				new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
		// fragmentActivity.startService(new Intent(fragmentActivity,
		// StrengthReciever.class));
	}

	@Override
	public void setPosition(int position) {
		ViewPager pager = (ViewPager) fragmentActivity.findViewById(R.id.viewpager);
		pager.setCurrentItem(position);
		setRadioPosition(position);
		NamedFragment namedFragment = (NamedFragment) fragments.get(position);
		L.out("setPosition focusedPage: " + focusedPage + " " +
				namedFragment.getTitle());
		transportActivity.updatePageTitle(namedFragment.getTitle());
	}

	public class MyPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
		@Override
		public void onPageSelected(int position) {

			NamedFragment namedFragment = (NamedFragment) fragments.get(position);
			L.out("MyPageChangeListener focusedPage: " + focusedPage + " " +
					namedFragment.getTitle());
			transportActivity.updatePageTitle(namedFragment.getTitle());
			if (position != focusedPage)
				setRadioPosition(position);
			focusedPage = position;

			// controlActionFragment(namedFragment.wantActions());

		}

		// private void controlActionFragment(boolean wantActions) {
		//
		// View view = fragmentActivity.findViewById(R.id.actionPager);
		// if (view == null) {
		// L.out("Unable to get view for actionPager!");
		// return;
		// }
		// L.out("wantActions: " + wantActions + " view: " +
		// view.getVisibility());
		// if (wantActions)
		// view.setVisibility(View.VISIBLE);
		// else
		// view.setVisibility(View.GONE);
		// }

	}

	public void setRadioPosition(int position) {
		RadioFragment radioFragment = (RadioFragment)
				fragmentActivity.getSupportFragmentManager().findFragmentByTag(RadioFragment.RADIO_FRAGMENT_TAG);
		// L.out("position: " + position + " focusedPage: " + focusedPage);
		if (radioFragment != null)
			// if (position != focusedPage)
			radioFragment.setPosition(position);

		else
			L.out("*** ERROR RadioFragment not found!");
	}
}
