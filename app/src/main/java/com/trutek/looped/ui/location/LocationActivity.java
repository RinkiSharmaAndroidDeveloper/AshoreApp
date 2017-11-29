package com.trutek.looped.ui.location;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.trutek.looped.Manifest;
import com.trutek.looped.R;
import com.trutek.looped.geoCode.Constants;
import com.trutek.looped.geoCode.GeoCodeIntentService;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.utils.ToastUtils;


public class LocationActivity extends BaseAppCompatActivity implements View.OnClickListener {

    static final String TAG = LocationActivity.class.getSimpleName();
    static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    TextView textView_text,textView_change_location,textView_textSearched;
    Button button_continue;
    ProgressBar mProgressBar;
    LinearLayout linearLayout_footer;
    GoogleApiClient mGoogleApiClient;
    AddressResultReceiver mResultReceiver;

    @Override
    protected int getContentResId() {
        return R.layout.activity_location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        listener();
        setFonts();
        searchingLocation();
        setGoogleApiClient();

    }

    @Override
    protected void onStart() {
        if(null != mGoogleApiClient){
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(null != mGoogleApiClient && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void setupActivityComponent() {

    }

    void setGoogleApiClient(){
        if(null == mGoogleApiClient){
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .addApi(LocationServices.API).build();
        }
    }

    private void initViews() {
        textView_text = (TextView) findViewById(R.id.locationActivity_textView_text);
        textView_textSearched = (TextView) findViewById(R.id.locationActivity_textView_text_searched);
        textView_change_location = (TextView) findViewById(R.id.locationActivity_textView_change_location);
        button_continue = (Button) findViewById(R.id.locationActivity_button_continue);
        mProgressBar = (ProgressBar) findViewById(R.id.locationActivity_progressBar);
        linearLayout_footer = (LinearLayout) findViewById(R.id.locationActivity_linearLayout_footer);
        mResultReceiver = new AddressResultReceiver(null);
    }

    private void listener() {
        button_continue.setOnClickListener(this);
        textView_change_location.setOnClickListener(this);
    }

    private void setFonts() {
        textView_text.setTypeface(avenirNextRegular);
        textView_textSearched.setTypeface(avenirNextRegular);
        textView_change_location.setTypeface(avenirNextRegular);
        button_continue.setTypeface(avenirNextRegular);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == button_continue.getId()){
            //TODO open categories activity
        }else if(view.getId() == textView_change_location.getId()){
            Intent intent = new Intent(LocationActivity.this,SearchLocationActivity.class);
            startActivity(intent);
        }

    }

    void gotLocation(String locationName){
        locationName = locationName.toUpperCase();
        String footerString = String.format(getString(R.string.locationActivity_text_location_change),"<strong>" +locationName+"</strong>");
        textView_text.setVisibility(View.GONE);
        textView_textSearched.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        linearLayout_footer.setVisibility(View.VISIBLE);
        textView_change_location.setText(Html.fromHtml(footerString));
    }

    void searchingLocation(){
        textView_text.setVisibility(View.VISIBLE);
        textView_textSearched.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        linearLayout_footer.setVisibility(View.INVISIBLE);
    }

    GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {

            if(null == bundle) {
                Log.d(TAG, "ConnectionCallback: onConnected bundle is null");
            }else{
                Log.d(TAG, "ConnectionCallback: onConnected" + bundle.toString());
            }


            if(ContextCompat.checkSelfPermission(LocationActivity.this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if(null != mLastLocation) {
                    Log.d(TAG, "ConnectionCallback: onConnected - " + mLastLocation.toString());
                    onLocationClick(mLastLocation);
                }else{
                    textView_change_location.performClick();
                }
            }else {
                Log.e(TAG, "ConnectionCallback: onConnected - Permission required" );
               requestPermission();
            }

        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG,"ConnectionCallback: onConnectionSuspended" + i);
        }
    };

    GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG,"Connection failed listener" + connectionResult.toString());
        }
    };

    void requestPermission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission_group.LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission_group.LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                textView_change_location.performClick();
            }else {
                if(null != mGoogleApiClient) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void onLocationClick(Location location) {
        Intent intent = new Intent(this, GeoCodeIntentService.class);
        intent.putExtra(com.trutek.looped.geoCode.Constants.RECEIVER, mResultReceiver);
        int fetchType = Constants.USE_ADDRESS_LOCATION;
        intent.putExtra(com.trutek.looped.geoCode.Constants.FETCH_TYPE_EXTRA, fetchType);
        intent.putExtra(com.trutek.looped.geoCode.Constants.LOCATION_DATA_EXTRA, location);

        Log.d(TAG, "Starting Service");
        startService(intent);
    }

    @SuppressLint("ParcelCreator")
    class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == com.trutek.looped.geoCode.Constants.SUCCESS_RESULT) {
                Log.d(TAG,"OnReceiveResult: " + resultData.toString());
                final Address address = resultData.getParcelable(com.trutek.looped.geoCode.Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null != address && !address.getLocality().isEmpty()) {
                            gotLocation(address.getLocality());
                        }else{
                            textView_change_location.performClick();
                        }
                        String name = resultData.getString(com.trutek.looped.geoCode.Constants.RESULT_DATA_KEY);
                        /*if (address != null) {
                            profile.location = new LocationModel();
                            profile.location.coordinates.add(String.valueOf(address.getLongitude()));
                            profile.location.coordinates.add(String.valueOf(address.getLatitude()));
                            profile.location.description = name;
                            profile.location.name = name;
                        }
                        textView_location.setText(name);*/
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.longToast(R.string.location_error);
                    }
                });
            }
        }
    }
}
