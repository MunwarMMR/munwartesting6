/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.tab;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.ii.mobile.beacon.R;

/**
 * 
 * @author kfairchild
 */
public class TaskHistoryActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.default_layout);
		TextView textview = (TextView) findViewById(R.id.defaultTextView);
		textview.setText("Welcome to Crothall TeamFlow \n"
				+ "Task History Activity is not yet implemented!");
	}
}
