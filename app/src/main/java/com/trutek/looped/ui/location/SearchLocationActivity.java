package com.trutek.looped.ui.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.trutek.looped.App;
import com.trutek.looped.R;
import com.trutek.looped.data.contracts.models.LocationModel;
import com.trutek.looped.data.contracts.models.ProfileModel;
import com.trutek.looped.data.contracts.services.ILocationService;
import com.trutek.looped.data.contracts.services.IProfileService;
import com.trutek.looped.geoCode.Constants;
import com.trutek.looped.geoCode.GeoCodeIntentService;
import com.trutek.looped.msas.common.contracts.AsyncResult;
import com.trutek.looped.msas.common.helpers.PreferenceHelper;
import com.trutek.looped.msas.common.models.Page;
import com.trutek.looped.ui.authenticate.SignupLocationCategoryActivity;
import com.trutek.looped.ui.base.BaseAppCompatActivity;
import com.trutek.looped.ui.communityDashboard.publiccommunity.PublicCommunityActivity;
import com.trutek.looped.utils.KeyboardUtils;
import com.trutek.looped.utils.ToastUtils;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class SearchLocationActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @Inject
    IProfileService _ProfileService;

    @Inject
    ILocationService _LocationService;

    static final String TAG = SearchLocationActivity.class.getSimpleName();
    static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    AddressResultReceiver mResultReceiver;

    CharacterStyle STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    ImageView imageView_back, imageView_cancel;
    TextView textView_searching;
    ProgressBar progressBar;
    EditText editText_search;
    Button doneButton;

    GoogleApiClient mGoogleApiClientSearch, mGoogleApiClient;

    RecyclerView mRecyclerView;
    SearchedLocationAdapter mAdapter;
    ArrayList<LocationModel> mLocations;
    public String ACTIVITY_CATEGORY = "1001";

    @Override
    protected int getContentResId() {
        return R.layout.activity_search_location;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setFonts();
        listeners();
        setGoogleApiClient();
        setAdapter(mLocations);
        showWaiting();
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).component().inject(this);
    }

    private void initViews() {
        imageView_back = (ImageView) findViewById(R.id.search_location_imageView_back);
        imageView_cancel = (ImageView) findViewById(R.id.search_location_imageView_cancel);
        editText_search = (EditText) findViewById(R.id.search_location_editText_search);
        mRecyclerView = (RecyclerView) findViewById(R.id.searchLocation_recyclerView);
        textView_searching = (TextView) findViewById(R.id.search_location_textView_searching);
        progressBar = (ProgressBar) findViewById(R.id.lsearch_location_progressBar);
        doneButton =(Button) findViewById(R.id.search_location_done_button);
        mGoogleApiClientSearch = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, connectionFailedListener)
                .build();
        mResultReceiver = new AddressResultReceiver(null);
        mLocations = new ArrayList<>();
        doneButton.setOnClickListener(this);
    }

    void setGoogleApiClient() {
        if (null == mGoogleApiClient) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedListener)
                    .addApi(LocationServices.API).build();
        }
    }

    private void setFonts() {
        editText_search.setTypeface(avenirNextRegular);
    }

    private void listeners() {
        imageView_back.setOnClickListener(this);
        imageView_cancel.setOnClickListener(this);
        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    imageView_cancel.setVisibility(View.VISIBLE);
                    searchCities(editable.toString());
                } else {
                    imageView_cancel.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void setAdapter(ArrayList<LocationModel> locations) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new SearchedLocationAdapter(locations, asyncResult_selectedLocation);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == imageView_back.getId()) {
            finish();
        } else {
            if (view.getId() == imageView_cancel.getId()) {
                editText_search.setText("");
                KeyboardUtils.hideKeyboard(editText_search);
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
                mGoogleApiClient.connect();
            }
        }
        if(view.getId() == doneButton.getId()){
            if(mLocations.get(0).coordinates.size()>0)
            {
                Intent i = new Intent(SearchLocationActivity.this, SignupLocationCategoryActivity.class);
                PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
                helper.savePreference(PreferenceHelper.PARENT_ACTIVITY, ACTIVITY_CATEGORY);
                startActivity(i);

            }else{
                Toast.makeText(getApplicationContext(),"Please select location before proceeding", Toast.LENGTH_SHORT).show();
            }
        }
    }

    GoogleApiClient.OnConnectionFailedListener connectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "Connection failed listener" + connectionResult.toString());
        }
    };

    ResultCallback<AutocompletePredictionBuffer> autocompletePredictionBufferResultCallback = new ResultCallback<AutocompletePredictionBuffer>() {
        @Override
        public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
            Log.d(TAG, autocompletePredictions.toString());
        }
    };

    void searchCities(String cityName) {
        showWaiting();
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS).build();

        final PendingResult<AutocompletePredictionBuffer> result =
                Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClientSearch, cityName,
                        null, autocompleteFilter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                AutocompletePredictionBuffer autocompletePredictions = result.await(60, TimeUnit.SECONDS);
                final Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String error;
                            if (status.getStatusCode() == CommonStatusCodes.NETWORK_ERROR) {
                                error = "Please check internet connectivity";
                            }else{
                                error = status.getStatusMessage();
                            }
                            hideWaiting();
                        }
                    });
                    Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                    autocompletePredictions.release();
                } else {

                    Log.d(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                            + " predictions.");
                    final ArrayList<AutocompletePrediction> predictions = DataBufferUtils.freezeAndClose(autocompletePredictions);


                    // Freeze the results immutable representation that can be stored safely.
                    Log.d(TAG, predictions.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLocations.clear();
                            for (AutocompletePrediction autocompletePrediction : predictions) {
                                LocationModel locationModel = new LocationModel();
                                locationModel.setPlaceId(autocompletePrediction.getPlaceId());
                                locationModel.setName(autocompletePrediction.getFullText(null).toString());
                                mLocations.add(locationModel);
                            }
                            if (null != mAdapter) {
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Log.e(TAG, "SearchCities: Recycler adapter is null");
                            }
                            hideWaiting();
                        }
                    });
                }
            }
        }).start();
    }

    GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (null == bundle) {
                Log.d(TAG, "ConnectionCallback: onConnected bundle is null");
            } else {
                Log.d(TAG, "ConnectionCallback: onConnected" + bundle.toString());
            }


            if (ContextCompat.checkSelfPermission(SearchLocationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (null != mLastLocation) {
                    Log.d(TAG, "ConnectionCallback: onConnected - " + mLastLocation.toString());
                    getLocation(mLastLocation);
                } else {
                    mLocations.clear();
                    mLocations.add(locationNotDetectedModel());
                    if (null != mAdapter) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "ConnectionCallBack: Recycler adapter is null");
                    }
                    hideWaiting();
//                    textView_change_location.performClick();
                }

            } else {
                Log.e(TAG, "ConnectionCallback: onConnected - Permission required");
                requestPermission();
            }

        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "ConnectionCallback: onConnectionSuspended" + i);
        }
    };

    void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                //TODO Display no current location
                mLocations.clear();
                mLocations.add(locationNotDetectedModel());
                if (null != mAdapter) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "onRequestPermissionResult: Recycler adapter is null");
                }
            } else {
                if (null != mGoogleApiClient) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        if (null != mGoogleApiClient) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void getLocation(Location location) {
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
            Log.e(TAG, "OnReceiveResult: ResultCode - " + resultCode);
            if (resultCode == com.trutek.looped.geoCode.Constants.SUCCESS_RESULT) {
                Log.d(TAG, "OnReceiveResult: " + resultData.toString());
                final Address address = resultData.getParcelable(com.trutek.looped.geoCode.Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != address && !address.getLocality().isEmpty()) {
                            LocationModel locationModel = new LocationModel();
                            locationModel.setAutoDetect(true);
                            locationModel.setName(address.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryCode());
                            locationModel.coordinates.add(String.valueOf(address.getLongitude()));
                            locationModel.coordinates.add(String.valueOf(address.getLatitude()));
                            mLocations.clear();
                            mLocations.add(locationModel);
                            getNearByLocations(locationModel);


                        } else {
                            //TODO SHOW SNACK BAR
//                            textView_change_location.performClick();
                        }
                        String name = resultData.getString(com.trutek.looped.geoCode.Constants.RESULT_DATA_KEY);
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLocations.clear();
                        mLocations.add(locationNotDetectedModel());
                        if (null != mAdapter) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "ConnectionCallBack: Recycler adapter is null");
                        }
                        hideWaiting();
                    }
                });
            }
        }
    }

    LocationModel locationNotDetectedModel() {
        LocationModel locationModel = new LocationModel();
        locationModel.setAutoDetect(true);
        locationModel.setName(getString(R.string.sla_locationNotDetected));
        return locationModel;
    }

    AsyncResult<LocationModel> asyncResult_selectedLocation = new AsyncResult<LocationModel>() {
        @Override
        public void success(LocationModel locationModel) {

            if(locationModel.coordinates.size() >0) {
                goNext(locationModel);
            }else {
                findPlace(locationModel);
            }
        }

        @Override
        public void error(String error) {

        }
    };

    void findPlace(LocationModel locationModel){
        if(null == locationModel.getPlaceId() || locationModel.getPlaceId().isEmpty()){
            return;
        }
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClientSearch, locationModel.getPlaceId());
        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
    }

    ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            LocationModel locationModel = new LocationModel();
            locationModel.setName(place.getAddress().toString());
            locationModel.coordinates.add(String.valueOf(place.getLatLng().longitude));
            locationModel.coordinates.add(String.valueOf(place.getLatLng().latitude));

            goNext(locationModel);

            // Format details of the place for display and show it in a TextView.
            /*mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri()));*/

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
           /* if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }*/

            Log.i(TAG, "Place details received: " + place.getAddress() + ":" + place.getLatLng());

            places.release();
        }
    };

    void showWaiting() {
        textView_searching.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    void hideWaiting() {
        textView_searching.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    void goNext(LocationModel locationModel){
        ProfileModel profileModel = _ProfileService.getMyProfile(null);
        profileModel.setLocation(locationModel);
        showProgress();
        _ProfileService.updateProfile(profileModel, new AsyncResult<ProfileModel>() {
            @Override
            public void success(ProfileModel profileModel) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SearchLocationActivity.this, SignupLocationCategoryActivity.class);
                        PreferenceHelper helper = PreferenceHelper.getPrefsHelper();
                        helper.savePreference(PreferenceHelper.PARENT_ACTIVITY, ACTIVITY_CATEGORY);
                        startActivity(i);
                        hideProgress();
                    }
                });

            }

            @Override
            public void error(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress();
                        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    void getNearByLocations(LocationModel locationModel){
        _LocationService.fetchNearByLocation(locationModel, new AsyncResult<Page<LocationModel>>() {
            @Override
            public void success(Page<LocationModel> locationModelPage) {

                for (LocationModel locationModel:locationModelPage.items) {
                    mLocations.add(locationModel);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (null != mAdapter) {
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.e(TAG, "AddressResultReceiver: Recycler adapter is null");
                        }
                        hideWaiting();
                    }
                });

            }

            @Override
            public void error(String error) {

            }
        });

    }
}
