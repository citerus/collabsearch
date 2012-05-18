package se.citerus.lookingfor.android;

import java.util.Timer;
import java.util.TimerTask;

import se.citerus.lookingfor.android.rest.LookingForRest;
import se.citerus.lookingfor.android.rest.Response;
import se.citerus.lookingfor.android.rest.VoidData;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LookingForActivity extends Activity implements View.OnClickListener {
	/** Called when the activity is first created. */
	
	private Location mCurrentLocation;
	private Timer mTimer;
	private LookingForRest mRestClient;
	private TextView mPositionView;
	private Button mStartButton;
	private Button mStopButton;
	
	@SuppressWarnings({"unchecked" })
	private <T> T findView(int id) {
		return (T) findViewById(id);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mPositionView = findView(R.id.textViewPosition);
		mStartButton = findView(R.id.buttonStart);
		mStopButton = findView(R.id.buttonStop);
		mRestClient = new LookingForRest("http://192.168.80.144:8080/");
		mStartButton.setOnClickListener(this);
		mStopButton.setOnClickListener(this);
		
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      // Called when a new location is found by the network location provider.
		    	mCurrentLocation = location;
		    	mPositionView.setText(location.getLatitude() + ", " + location.getLongitude());
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		mPositionView.setText(mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude());
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.buttonStart:
			mStopButton.setEnabled(true);
			mStartButton.setEnabled(false);
			mTimer = new Timer();
			mTimer.scheduleAtFixedRate(new TimerTask() {
			    @Override
			    public void run() {
			    	if(mCurrentLocation != null) {
						Response<VoidData> response = mRestClient.postFootprint("aaa", "bbb", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), mCurrentLocation.getAccuracy(), mCurrentLocation.getTime(), "some hash");
						Log.d("lookingfor", "Response: " + response.getHttpResult());
					}
			         }
			    }, 0, 10000);

			break;
			
		case R.id.buttonStop:
			mTimer.cancel();
			mStopButton.setEnabled(false);
			mStartButton.setEnabled(true);
		}
	}
}