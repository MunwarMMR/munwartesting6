package com.ii.mobile.paging;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ii.mobile.util.L;

/**
 * The <code>PagerAdapter</code> serves the fragments when paging.
 */

public class SliderAdapter extends FragmentStatePagerAdapter {

	private final List<Fragment> fragments;

	// private int replacedPosition = -1;

	/**
	 * @param fm
	 * @param fragments
	 */
	public SliderAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;

	}

	// @Override
	// protected void destroyItem(ViewGroup container, int position, Object
	// object) {
	// FragmentManager manager = ((Fragment)object).getFragmentManager();
	// FragmentTransaction trans = manager.beginTransaction();
	// trans.remove((Fragment)object);
	// trans.commit();
	// }

	@Override
	public Parcelable saveState() {
		// DO NOTHING
		L.out("doing nothing");
		return null;

	}

	synchronized public void replaceFragment(Fragment oldFragment, Fragment newFragment) {

		// L.out("oldFragment: " + oldFragment + " " + newFragment);
		// L.out("before fragments: " + fragments);
		int position = indexOfClass(oldFragment.getClass());
		if (position == -1) {
			L.out("Unable to find position for: " + oldFragment);
			if (indexOfClass(newFragment.getClass()) != -1) {
				// L.out("keeping newFragment: " + newFragment);
				return;
			}
		} else {
			fragments.remove(position);
			fragments.add(position, newFragment);
		}
		// L.out("finished fragments: " + fragments);
		try {
			notifyDataSetChanged();
		} catch (Exception e) {
			// MyToast.show("ERROR in replaceFragment - caught");
		}
	}

	private int indexOfClass(Class<? extends Fragment> compareClass) {
		for (int i = 0; i < fragments.size(); i++) {
			Fragment fragment = fragments.get(i);
			if (fragment.getClass().equals(compareClass)) {
				// L.out("found: " + fragment + " " + i);
				return i;
			}
		}
		return -1;
	}

	// @Override
	// public int getItemPosition(Object object) {
	// return POSITION_NONE;
	// }

	@Override
	public int getItemPosition(Object object) {
		// L.out("object: " + object + " " + fragments.contains(object));

		// Fragment fragment = (Fragment) object;
		// L.out("fragment: " + fragment + " " +
		// indexOfClass(fragment.getClass()));
		// L.out("fragments: " + fragments);
		int position = fragments.indexOf(object);
		// L.out("position: " + position);
		if (position != -1) {
			return POSITION_UNCHANGED;
		}
		// L.out("object: " + object + " " + POSITION_NONE);
		return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int item) {
		// L.out("item: " + item + " " + fragments.get(item));
		if (item >= fragments.size()) {
			return null;
		}
		return fragments.get(item);
	}

	@Override
	public int getCount() {
		// L.out("getCount: " + fragments.size() + " " + fragments);
		return fragments.size();
	}

}
