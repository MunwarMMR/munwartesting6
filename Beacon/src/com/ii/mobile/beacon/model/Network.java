package com.ii.mobile.beacon.model;

public class Network {

	String name;
	String bssid;
	int strength = -100;
	int type = -1;
	int frequency = -1;
	public int throughput = -1;
	boolean hasPassword;

	public int getThroughput() {
		return throughput;
	}

	public void setThroughput(int throughput) {
		this.throughput = throughput;
	}

	public boolean hasPassword() {
		return hasPassword;
	}

	public void setHasPassword(boolean hasPassword) {
		this.hasPassword = hasPassword;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
