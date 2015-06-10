package com.ii.mobile.beacon.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.MainActivity;
import com.ii.mobile.fragments.NamedFragment;

public class SelectGeographyFragment extends Fragment implements NamedFragment, OnClickListener
{
	Spinner sp_geography;
	ArrayList<String> geographyList;
	String str_geography;
	EditText et_geography;
	Button bt_continue;
	Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.geography, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		activity = getActivity();
		sp_geography = (Spinner) activity.findViewById(R.id.sp_geography);
		et_geography = (EditText) activity.findViewById(R.id.et_geography);
		bt_continue = (Button) activity.findViewById(R.id.btn_continue);
		bt_continue.setOnClickListener(this);
		geographyList = new ArrayList<String>();
		geographyList.add("Select Geo");
		geographyList.add("Stairwell 1");
		geographyList.add("Stairwell 2");
		geographyList.add("Room 10.2");
		geographyList.add("Other");
		sp_geography.setAdapter(new ArrayAdapter<String>(activity, R.layout.spinner_item, geographyList));
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
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.btn_continue:
//			if (sp_geography.getSelectedItemPosition() == 0)
//			{
//				Toast.makeText(activity, "Please Select a Geography", Toast.LENGTH_LONG).show();
//			}
//			else
//			{
//				Intent intent = new Intent(activity, MainActivity.class);
//				if (sp_geography.getSelectedItemPosition() == geographyList.size() - 1)
//				{
//					str_geography = et_geography.getText().toString().trim();
//					if (null == str_geography || str_geography.equals(""))
//					{
//						Toast.makeText(activity, "Please Enter Geography Name", Toast.LENGTH_LONG).show();
//					}
//					else
//					{
//						intent.putExtra("geo", str_geography);
//						startActivity(intent);
//					}
//				}
//				else
//				{
//					intent.putExtra("geo", str_geography);
//					startActivity(intent);
//				}
//			}
//
//			break;
		}
	}

	@Override
	public String getTitle() {
		return "Select Geography";
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean wantActions() {
		// TODO Auto-generated method stub
		return false;
	}
}