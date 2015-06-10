package com.ii.mobile.sync;

import com.google.gson.Gson;

public interface PayloadCallback {
	void callback(Gson gson);
}