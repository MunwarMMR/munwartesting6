package com.ii.mobile.speed;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.GregorianCalendar;

import android.content.res.Resources;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.model.Sample;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.surveyList.SurveyFragment;
import com.ii.mobile.update.Version;
import com.ii.mobile.util.L;

public class SpeedTest {

	private final Sample sample;
	private final int sampleTimeInterval = 5000;

	public SpeedTest(Sample sample) {
		this.sample = sample;
		run();
	}

	private synchronized void run() {
		Thread thread = new Thread()
		{
			@Override
			public void run() {
				sample.testing = true;
				sample.speed = doSpeedTest();
				L.out("sample.speed: " + sample.speed);
				sample.failed = ((sample.speed > 0.0f) ? false : true);
				TransportActivity.transportActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						sample.testing = false;
						SurveyFragment.surveyFragment.update();
						// SampleFragment.sampleFragment.update();
					}
				});
			}

			private float doSpeedTest() {
				GregorianCalendar started = L.startTimer();
				int count = 0;
				while (true) {
					// L.sleep(100);
					GJon gson = downloadTest();
					if (gson != null && gson.isValidated()) {
						// L.out("gson.isValidated(): " + ((gson != null) ?
						// gson.isValidated() : gson));
						count += gson.getJson().length();
					}
					else {
						return 0.0f;
					}
					GregorianCalendar now = new GregorianCalendar();
					long diff = now.getTimeInMillis() - started.getTimeInMillis();
					if (diff > sampleTimeInterval)
						return count / (sampleTimeInterval / 1000);
				}
			}

			private final String configFile = "version.json";

			private GJon downloadTest() {
				Resources resources = TransportActivity.transportActivity.getResources();

				String downloadURL = getDownloadURL();

				// L.out("downloadURL: " + downloadURL);
				String appFile = resources.getString(R.string.app_file);
				GJon newVersion = null;
				try {
					URL updateURL = new URL(downloadURL + appFile + "_" + configFile);
					// L.out("updateURL: " + updateURL);
					URLConnection conn = updateURL.openConnection();
					InputStream is = conn.getInputStream();

					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String sResponse;
					StringBuilder s = new StringBuilder();

					while ((sResponse = reader.readLine()) != null) {
						s = s.append(sResponse);
					}
					String responseString = s.toString();

					// L.out("responseString: " + s);
					newVersion = Version.getGJon(responseString);
					// L.out("newVersion: " + newVersion);
				} catch (Exception e) {

				}
				return newVersion;
			}

			private String getDownloadURL() {
				Resources resources = TransportActivity.transportActivity.getResources();
				boolean isProduction = resources.getBoolean(R.bool.isProduction);
				String downloadURL;
				if (isProduction)
					downloadURL = resources.getString(R.string.production_download_url);
				else
					downloadURL = resources.getString(R.string.beta_download_url);
				return downloadURL;
			}
		};

		thread.start();

	}
}
