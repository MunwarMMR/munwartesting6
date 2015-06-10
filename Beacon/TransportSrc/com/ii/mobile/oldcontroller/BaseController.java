package com.ii.mobile.oldcontroller;

import android.app.Activity;

import com.ii.mobile.cache.Cache;

public class BaseController {
	protected Cache cache = null;
	protected final Activity activity;

	public BaseController(Cache cache) {
		this.cache = cache;
		activity = (Activity) cache;
	}

}
