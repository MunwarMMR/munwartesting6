package com.ii.mobile.beacon;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.fragments.EventOutputFragment;
import com.ii.mobile.beacon.fragments.SelectGeographyFragment;
import com.ii.mobile.beacon.fragments.WifiListFragment;

public class ViewPagerActivity extends FragmentActivity {
	RadioGroup radioGroup;
	private ViewPager mViewPager;
	private MyAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_pager_main_radio);

		radioGroup = (RadioGroup) findViewById(R.id.rgroup);
		((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		myAdapter = new MyAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(myAdapter);
		// mViewPager.set
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	public static class MyAdapter extends FragmentPagerAdapter {
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {

			return 3;
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {
			case 0:
				return new EventOutputFragment();
			case 1:
				return new WifiListFragment();
			case 2:
				return new SelectGeographyFragment();
			default:
				return null;
			}
		}
	}
}