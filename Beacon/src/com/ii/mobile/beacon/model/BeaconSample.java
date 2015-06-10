package com.ii.mobile.beacon.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class BeaconSample extends GJon {
	@SerializedName("BeaconSampleInner")
	public BeaconSampleInner beaconSampleInner = new BeaconSampleInner();

	public class BeaconSampleInner {
		@SerializedName("samples")
		public List<Sample> samples = new ArrayList<Sample>();

		@SerializedName("name")
		String name;

		@Override
		public String toString() {
			return " name: " + name
					+ "\n samples: " + samples;
		}
	}

	public static String n(String string, String value, int space) {
		return space(space) + string + ": " + value + "\n";
	}

	public static String space(int space) {
		String temp = "";
		for (int i = 0; i < space; i++) {
			temp += "    ";
		}
		return temp;
	}

	@Override
	public boolean validate() {
		if (beaconSampleInner != null
				&& beaconSampleInner.samples != null)
			validated = true;
		else
			L.out("Unable to validate BeaconSample");
		return validated;
	}

	@Override
	public String toString() {
		if (beaconSampleInner == null)
			return "ERROR: beaconSampleInner is null";
		return beaconSampleInner.toString();
	}

	public static BeaconSample getGJon(String json) {
		// L.out("BeaconSample: ");
		// PrettyPrint.prettyPrint(json, true);
		BeaconSample beaconSample = (BeaconSample) getJSonObject(json, BeaconSample.class);
		beaconSample.validate();
		// L.out("output: " + beaconSample.getNewJson());
		return beaconSample;
	}
}
