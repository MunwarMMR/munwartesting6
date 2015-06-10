package com.ii.mobile.flow;

import com.ii.mobile.soap.gson.GJon;

public interface SyncCallback {
	void callback(GJon gJon, String payloadName);
}