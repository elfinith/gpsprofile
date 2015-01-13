package ru.elfinith.gpsprofile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	final String LOG_TAG = "myLogs";

	final String FILENAME_EXT = ".csv";
	String FILENAME = "untitled";

	final String DIR_SD = "MyFiles";
	final String FILENAME_SD = "fileSD";

	boolean bRecording = false;

	TextView tvLocationGPS;
	TextView tvEnabledGPS;
	TextView tvStatusGPS;

	private LocationManager locationManager;

	// StringBuilder sbGPS = new StringBuilder();
	// StringBuilder sbNet = new StringBuilder();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
		tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
		tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000 * 10, 10, locationListener);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
				locationListener);
		checkEnabled();
	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(locationListener);
	}

	public void onclick(View v) {
		switch (v.getId()) {
		case R.id.btnStart:
			bRecording = true;
			FILENAME = new java.text.SimpleDateFormat("yyMMddHHmmssZ")
					.format(java.util.Calendar.getInstance().getTime())
					+ FILENAME_EXT;
			Toast.makeText(this, "«апись в " + FILENAME, Toast.LENGTH_SHORT).show();
			break;
		case R.id.btnFinish:
			bRecording = false;
			break;
		}
	}

	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			showLocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			checkEnabled();
		}

		@Override
		public void onProviderEnabled(String provider) {
			checkEnabled();
			showLocation(locationManager.getLastKnownLocation(provider));
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if (provider.equals(LocationManager.GPS_PROVIDER)) {
				tvStatusGPS.setText("Status: " + String.valueOf(status));
			}
		}

	};

	private void showLocation(Location location) {
		if (location == null)
			return;
		if (location.getProvider().equals(LocationManager.GPS_PROVIDER)
				&& bRecording) {
			tvLocationGPS.setText(formatLocation(location));
			// try {
			// // отрываем поток дл€ записи
			// BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
			// openFileOutput(FILENAME, MODE_APPEND)));
			// // пишем данные
			// bw.write(tvLocationGPS.getText().toString());
			// // закрываем поток
			// bw.close();
			// } catch (FileNotFoundException e) {
			// e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				
				Toast.makeText(this, "SD-карта не доступна: "
						+ Environment.getExternalStorageState(), Toast.LENGTH_SHORT).show();
				return;
			}
			// получаем путь к SD
			File sdPath = Environment.getExternalStorageDirectory();
			// добавл€ем свой каталог к пути
			sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
			// создаем каталог
			sdPath.mkdirs();
			// формируем объект File, который содержит путь к файлу
			File sdFile = new File(sdPath, FILENAME);
			try {
				// открываем поток дл€ записи
				
				BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile, true));
			    //BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(FILENAME, MODE_APPEND)));
				
				// пишем данные
				//bw.write(tvLocationGPS.getText().toString());
				bw.append(tvLocationGPS.getText().toString());				
				// закрываем поток
				bw.close();
				// Log.d(LOG_TAG,	"‘айл записан на SD: " + sdFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private String formatLocation(Location location) {
		if (location == null)
			return "";
		return String.format("%1$.4f;%2$.4f;%3$.4f;%4$tF %4$tT;%n",
				location.getLatitude(), location.getLongitude(),
				location.getAltitude(), new Date(location.getTime()));
	}

	private void checkEnabled() {
		tvEnabledGPS.setText("GPS enabled: "
				+ locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER));
	}

}
