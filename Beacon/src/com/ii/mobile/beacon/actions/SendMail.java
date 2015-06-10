package com.ii.mobile.beacon.actions;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.ii.mobile.beacon.fragments.WifiListFragment;
import com.ii.mobile.beacon.model.BeaconController;
import com.ii.mobile.beacon.model.BeaconSample;
import com.ii.mobile.beacon.model.Sample;
import com.ii.mobile.util.L;

public class SendMail {
	public static void sendMail(Activity activity) {
		L.out("sendMail");
		File file;
		WritableWorkbook workbook = null;
		WritableSheet sheet = null;
		BeaconSample beaconSample = BeaconController.beaconSample;
		// if (beaconSample == null) {
		// beaconSample = new BeaconSample();
		// SelectLocations selectLocations = UpdateController.selectLocations;
		// if (selectLocations.selectLocationsInner.stores == null
		// || selectLocations.selectLocationsInner.stores.size() == 0) {
		// MyToast.show("No Samples to send!");
		// return;
		// }
		// Stores stores = selectLocations.selectLocationsInner.stores.get(0);
		//
		// for (Items item : stores.items) {
		// beaconSample.beaconSampleInner.samples.add(new
		// Sample(item.description));
		// }
		// BeaconController.beaconSample = beaconSample;
		// }

		Sample[] newSamples = new Sample[beaconSample.beaconSampleInner.samples.size()];
		int i = 0;
		for (Sample sample : beaconSample.beaconSampleInner.samples) {
			newSamples[i] = sample;
			i += 1;
		}

		if (null == newSamples || newSamples.length == 0)
			L.out(" @@@@@ %%%%% $$$$ No Samples Available");
		else
		{
			String Fnamexls = "Beacon" + ".xls";
			File sdCard = Environment.getExternalStorageDirectory();
			File directory = new File(sdCard.getAbsolutePath() + "/newfolder");
			directory.mkdirs();

			file = new File(directory, Fnamexls);

			WorkbookSettings wbSettings = new WorkbookSettings();
			wbSettings.setLocale(new Locale("en", "EN"));

			try {
				workbook = Workbook.createWorkbook(file, wbSettings);
				sheet = workbook.createSheet("Beacon Session", 0);
				sheet.addCell(new Label(0, 3, " Name "));
				sheet.addCell(new Label(1, 3, " Location "));
				sheet.addCell(new Label(2, 3, " Speed "));
				sheet.addCell(new Label(3, 3, " Strength "));
				sheet.addCell(new Label(4, 3, " Event "));
				sheet.addCell(new Label(5, 3, " Type "));
				sheet.addCell(new Label(6, 3, " Time "));

			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			i = 4;
			int failed = 0;
			L.out("newSamples: " + newSamples.length);
			for (Sample sample : newSamples)
			{

				float speed = sample.speed;
				if (speed == 0)
					++failed;
				try {

					sheet.addCell(new Label(0, i, " " + sample.name + " "));
					sheet.addCell(new Label(1, i, " " + sample.location + " "));
					sheet.addCell(new Label(2, i, " " + sample.speed / 1000. + " kB/s"));
					sheet.addCell(new Label(3, i, " " + sample.strength + " "));
					sheet.addCell(new Label(4, i, sample.event));
					sheet.addCell(new Label(5, i, WifiListFragment.getNetworkName(sample.type)));
					sheet.addCell(new Label(6, i, L.toDateDayHourSecond(sample.dateStamp)));
					++i;
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}

			}

			Label label_summary = new Label(0, 1, "Total Samples " + newSamples.length +
					", Correct Samples : " + (newSamples.length - failed) + ", Failed Samples : " + failed);

			try {
				Label facilityLabel = new Label(0, 2, "Facility: " + BeaconController.beaconSampleName);
				sheet.addCell(facilityLabel);
				sheet.addCell(label_summary);
			} catch (RowsExceededException e1) {
				e1.printStackTrace();
			} catch (WriteException e1) {

				e1.printStackTrace();
			}

			System.out.println(" Active Samples " + i);
			try {
				if (null != workbook)
				{
					workbook.write();
					workbook.close();
				}
			} catch (IOException e) {

				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}

			// Code For sending a mail through one of the available Mailing
			// Clients

			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "malli930@gmail.com" });

			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Beacon Excel");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check Excel");
			emailIntent.setType("application/excel");
			if (file.exists())
			{
				System.out.println("@@@@@@@@@@@@@ ------  Adding Attachment to the Mail");
				// Adding Excel as an Attachment
				emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///" + file));
				activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
				// EventOutputFragment.receiveMessage("WifiList Fragment",
				// "Samples are Mailed");
			}
			else
			{
				System.out.println("@@@@@@@@@@@@@ ------  Attachment File does not exist!");
			}
		}
	}
}
