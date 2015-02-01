package com.bagsta.places;

import java.util.ArrayList;
import java.util.Collections;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
//import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
//import com.google.android.gms.location.LocationClient;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;


@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MainActivity extends ListActivity implements LocationServiceCallbacks {
//public class MainActivity extends ListActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
	ProgressDialog dialog;

	private static String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static String PARAM_LOCATION = "location=";
    private static String PARAM_RADIUS = "&radius=";
    private static String PARAM_KEY = "&key=";
    private static String PARAM_TYPES = "&types=";
    private static String GOOGLE_CONSOLE_KEY = "AIzaSyB6MXMO0JnuzYZ-dmAK8uBfvL6HqlgN1H0";
    private static int RADIUS = 500;

//    private static String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&key=AIzaSyB6MXMO0JnuzYZ-dmAK8uBfvL6HqlgN1H0";

	private static final String TAG_STATUS = "status";
	private static final String TAG_RESULTS = "results";
	private static final String TAG_NAME = "name";
    private static final String TAG_ICON_URL = "icon";
	private static final String TAG_GEO = "geometry";
	private static final String TAG_LOCATION = "location";
	private static final String TAG_LATITUDE = "lat";
	private static final String TAG_LONGITUDE = "lng";
	private static final String TAG_TYPES = "types";
    public static final String EXTRA_CATEGORY = "";

    public static final int REQUEST_CATEGORY = 0;
    public static final int REQUEST_PLAY_SERVICES_ERROR = 1;
    public static final String EXTRA_ERROR_CODE ="error code";
    boolean mResolvingGooglePlayServicesError = false;

	ListView listView;
    CustomAdaptor adaptor;

    JSONParse parse;
//	LocationClient locationClient;
	Location userLocation;
//	LocationManagerService locationManagerService;

//    GoogleLocationService googleLocationService;
    LocationService locationService;
    int googlePlayStatus = -1;

	static ImageLoader imageLoader;

	enum Status {

		PENDING,
        FETCHING_LOCATION,
		FETCHED_LOCATION,
		PARSING_JSON,
		FINSIHED
	}

	Status mStatus = Status.PENDING;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		locationClient = new LocationClient(this, this, this);
//		locationClient.connect();
		listView = getListView();
		Button button = (Button) findViewById(R.id.clear_btn);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (adaptor != null) {
//					imageLoader.clearCache();
//					adaptor.notifyDataSetChanged();
                    Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                    startActivityForResult(intent, REQUEST_CATEGORY);
				}
			}
		});
		
		parse = new JSONParse();
//		locationManagerService = new LocationManagerService(this,this);
        locationService = new GoogleLocationService(this,this);
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.main, menu);
//		return super.onCreateOptionsMenu(menu);
//	}
		
	@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_search :
//				item.setActionView(R.layout.search_layout);
//				item.expandActionView();
				break;
			}
			return true;
		}

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        googlePlayStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // Log.d(TAG,"status : "+googlePlayStatus);
        if (googlePlayStatus != ConnectionResult.SUCCESS && !mResolvingGooglePlayServicesError) {
            showErrorDialog();
        } else {
            if (mStatus == Status.PENDING) {
                mStatus = Status.FETCHING_LOCATION;
                createDialog("Waiting for location");
                locationService.connect();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googlePlayStatus == ConnectionResult.SUCCESS && mStatus == Status.FETCHING_LOCATION) {
            mStatus = Status.PENDING;
            dismissDialog();
            locationService.disconnect();
        }
    }

    @Override
	protected void onDestroy() {
		dismissDialog();
		super.onDestroy();
	}

	class JSONParse extends AsyncTask<String, Void, ArrayList<QueryObject>> {
		
		@Override
		protected void onPreExecute() {
			mStatus = MainActivity.Status.PARSING_JSON;
			createDialog("Please Wait...");
		}

		@Override
		protected ArrayList<QueryObject> doInBackground(String... params) {
			ArrayList<QueryObject> arrayList = null;
			String jsonString = null;

			jsonString = HttpRequestService.requestString(params[0]);

			if (jsonString != null) {

				try {

					JSONObject jsonObject = new JSONObject(jsonString);
					String status = jsonObject.getString(TAG_STATUS);
					Log.d("STATUS", status);
					if (!status.equals("OK")) {
						return null;
					}
					
					JSONArray jsonArray = jsonObject.getJSONArray(TAG_RESULTS);
					arrayList = new ArrayList<QueryObject>();

					for (int i = 0; i < jsonArray.length(); i++) {

						JSONObject queryObject = jsonArray.getJSONObject(i);
						
						JSONObject geometryObject = queryObject.getJSONObject(TAG_GEO);
						JSONObject locationObject = geometryObject.getJSONObject(TAG_LOCATION);
						
						long latitude = locationObject.getLong(TAG_LATITUDE);
						long longitude = locationObject.getLong(TAG_LONGITUDE);
						
						String iconURL = queryObject.getString(TAG_ICON_URL);
						String name = queryObject.getString(TAG_NAME);

						JSONArray types = queryObject.getJSONArray(TAG_TYPES);
						
						String[] categories = new String[types.length()];
						
						for (int j = 0; j < types.length(); j++) {
							categories[j] = types.getString(j);
						}

						Location location = new Location("");
						location.setLatitude((latitude));
						location.setLongitude((longitude));

						float distance = userLocation.distanceTo(location);

						QueryObject queryObjects = new QueryObject(name,latitude, longitude, iconURL,distance,categories);
						arrayList.add(queryObjects);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			return arrayList;
		}
		
		@Override
		protected void onPostExecute(ArrayList<QueryObject> queryObjects) {
			if (MainActivity.this.isFinishing())
				return;
			
			mStatus = MainActivity.Status.FINSIHED;
//			Location location = locationClient.getLastLocation();
//			Toast.makeText(MainActivity.this, "location"+location, Toast.LENGTH_SHORT).show();
			dismissDialog();
			
			if (queryObjects != null) {
				Collections.sort(queryObjects);
				adaptor = new CustomAdaptor(MainActivity.this, queryObjects);
				listView.setAdapter(adaptor);
			} else {
				Toast.makeText(MainActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
			}
		}
	}

	boolean isConnectedToInternet() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		
		return networkInfo != null && networkInfo.isConnected();
	}
	
	AlertDialog.Builder alertDialog;
	void showSettingsAlert(String title, String message,String positiveText, final String ActivityName) {
		
		alertDialog  = new AlertDialog.Builder(this);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(ActivityName);
				startActivity(intent);
			}
		});
		
		alertDialog.show();
	}
	
//	Location getLocation() {
//		Location location = null;
//		try {
//			location = locationManagerService.getLocation();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return location;
//	}
	
	void getQuery() {
		String url = BASE_URL+PARAM_LOCATION+""+userLocation.getLatitude()+ "," + userLocation.getLongitude()+PARAM_RADIUS+""+RADIUS+""+PARAM_KEY+GOOGLE_CONSOLE_KEY;
        getQuery(url);
	}

    void getQuery(String url) {
        if (parse.getStatus() == AsyncTask.Status.FINISHED) {
            parse = new JSONParse();
        }
		if (parse.getStatus() == android.os.AsyncTask.Status.PENDING) {
			if (isConnectedToInternet()) {
                Log.d("URL",url);
				parse.execute(url);
			} else {
				showSettingsAlert("Settings", "Enable Internet Connection",
						"Settings", Settings.ACTION_SETTINGS);
			}
		}
	}
	
	void createDialog(String message) {
		if (dialog == null || !dialog.isShowing()) {
			dialog = new ProgressDialog(this);
			dialog.setMessage(message);
			dialog.setCancelable(false);
			dialog.show();
		}
	}
	
	void dismissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			Log.d("Dialog", "dialog.dismiss()");
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		userLocation = location;
		if (mStatus == Status.FETCHING_LOCATION) {
			if (userLocation != null) {
				dismissDialog();
				mStatus = Status.FETCHED_LOCATION;
				locationService.disconnect();
			} else {
				mStatus = Status.FETCHING_LOCATION;
				createDialog("Waiting for location");
			}
		}
		
		if (mStatus == Status.FETCHED_LOCATION) {
			getQuery();
		}
	}

//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void onProviderEnabled(String provider) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void onProviderDisabled(String provider) {
//		// TODO Auto-generated method stub
//	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult"," requestCode : "+requestCode+" resultCode : "+resultCode);

        if (requestCode == REQUEST_PLAY_SERVICES_ERROR) {
            googlePlayStatus = resultCode;
            Log.d(TAG,"status : "+googlePlayStatus);

            if (googlePlayStatus == ConnectionResult.SUCCESS) {
                mStatus = Status.FETCHING_LOCATION;
                createDialog("Waiting for location");
                locationService.connect();
            } else {
                GooglePlayServicesUtil.getErrorDialog(googlePlayStatus,this, REQUEST_PLAY_SERVICES_ERROR).show();
            }
        } else if (requestCode == REQUEST_CATEGORY && resultCode == RESULT_OK) {
            String category = data.getStringExtra(EXTRA_CATEGORY);
            String url = BASE_URL+PARAM_LOCATION+""+userLocation.getLatitude()+","+userLocation.getLongitude()+PARAM_RADIUS+RADIUS+PARAM_TYPES+category+PARAM_KEY+GOOGLE_CONSOLE_KEY;
            getQuery(url);
        }
    }

    void showErrorDialog() {
        ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_ERROR_CODE,googlePlayStatus);
        errorDialogFragment.setArguments(bundle);
        errorDialogFragment.show(getFragmentManager(),"ERROR DIALOG");
    }

    void onDismissDialog() {
        mResolvingGooglePlayServicesError = false;
    }

   public static class ErrorDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return super.onCreateDialog(savedInstanceState);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
        }
    }
}
