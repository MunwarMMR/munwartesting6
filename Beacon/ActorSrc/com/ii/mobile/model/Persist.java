package com.ii.mobile.model;

/**
 * 
 */
public class Persist {

	private int id = -1;
	protected String status = "Available";
	private String accessTime = "";
	public String ider;

	public int get_Id() {
		// foo comment
		return id;
	}

	public void set_Id(int id) {
		this.id = id;
	}

	public String getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(String accessTime) {
		this.accessTime = accessTime;
	}

	/**
	 * Return the status.
	 * 
	 * @return
	 */
	protected String getStatus() {
		return status;
	}

	/**
	 * Return the simple class name, used for the name of the SQL table.
	 * 
	 * @return
	 */
	public String getSimpleName() {
		String temp = getClass().getName();
		int index = temp.lastIndexOf(".");
		// System.out.println("getSimpleName: " + temp + " " + index);
		String foo = temp.substring(index + 1, temp.length());
		foo = foo.toLowerCase();
		// System.out.println("getSimpleName result: " + foo);
		return foo;
	}

	public void setStatus(String status) {
		this.status = status;

	}

	@Override
	public String toString() {
		return "Persist (" + id + ")";
	}

}
