package com.ii.mobile.beacon;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ii.mobile.beacon.R;

public class SelectGeography extends Activity implements OnClickListener {

	Spinner sp_geography;
	ArrayList<String> geographyList;
	String str_geography;
	EditText et_geography;
	Button bt_continue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geography);
		sp_geography = (Spinner) findViewById(R.id.sp_geography);
		et_geography = (EditText) findViewById(R.id.et_geography);
		bt_continue = (Button) findViewById(R.id.btn_continue);
		bt_continue.setOnClickListener(this);
		geographyList = new ArrayList<String>();
		geographyList.add("Select Geo");
		geographyList.add("Stairwell 1");
		geographyList.add("Stairwell 2");
		geographyList.add("Room 10.2");
		geographyList.add("Other");
		sp_geography.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_item, geographyList));
		sp_geography.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == geographyList.size() - 1)
				{
					et_geography.setVisibility(View.VISIBLE);
				}
				else
				if (arg2 != 0)
				{
					et_geography.setVisibility(View.GONE);
					str_geography = geographyList.get(arg2);
				}
				else
					et_geography.setVisibility(View.GONE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_continue:
			if (sp_geography.getSelectedItemPosition() == 0)
			{
				Toast.makeText(this, "Please Select a Geography", Toast.LENGTH_LONG).show();
			}
			else
			{
				Intent intent = new Intent(this, MainActivity.class);
				if (sp_geography.getSelectedItemPosition() == geographyList.size() - 1)
				{
					str_geography = et_geography.getText().toString().trim();
					if (null == str_geography || str_geography.equals(""))
					{
						Toast.makeText(this, "Please Enter Geography Name", Toast.LENGTH_LONG).show();
					}
					else
					{
						intent.putExtra("geo", str_geography);
						startActivity(intent);
					}
				}
				else
				{
					intent.putExtra("geo", str_geography);
					startActivity(intent);
				}
			}

			break;
		}
	}
}