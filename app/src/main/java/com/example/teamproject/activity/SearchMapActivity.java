package com.example.teamproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.teamproject.MarkerItem;
import com.example.teamproject.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.media.CamcorderProfile.get;

public class SearchMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMarkerClickListener,GoogleMap.OnMyLocationChangeListener {
    private GoogleApiClient mGoogleApiClient = null;
    private Marker currentMarker = null;

    private static final String TAG = "googleMap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;
    LatLng currentPosition1;
    ArrayList<MarkerItem> markerList = new ArrayList();
    MarkerItem markerItem;
    Marker selectedMarker;
    Geocoder geocoder;
    //Location location1 = null;


    LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_search_map);
        setTitle("?????? ?????? ??????");


        mActivity = this;

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_google1);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }


        //??? ???????????? ???????????? ?????????????????? ?????? ??????????????? ??????.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }
    }

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : ????????? ???????????? ??????");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }

    }


    private void stopLocationUpdates() {

        Log.d(TAG, "stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MarkerOptions markerOptions = new MarkerOptions();
        geocoder = new Geocoder(this);

        Log.d(TAG, "onMapReady :");

        mGoogleMap = googleMap;



        //????????? ????????? ?????? ??????????????? GPS ?????? ?????? ???????????? ???????????????
        //????????? ??????????????? ????????? ??????

        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        getSampleMarkerItems();
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d(TAG, "onMyLocationButtonClick : ????????? ?????? ????????? ?????? ?????????");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d(TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates) {

                    Log.d(TAG, "onCameraMove : ????????? ?????? ????????? ?????? ????????????");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {


            }
        });
        String locationAddress = getIntent().getStringExtra("location");
        if (locationAddress == null) {
            mGoogleMap.setOnMyLocationChangeListener(this);

        }else{
            List<Address> addressList=null;
            try {
                addressList=geocoder.getFromLocationName(locationAddress,20);
            }catch (Exception e){
                e.printStackTrace();
            }
            String[] splitStr=addressList.get(0).toString().split(",");
            String address=splitStr[0].substring(splitStr[0].indexOf("/")+1,splitStr[0].length()-2);
            String latitude=splitStr[10].substring(splitStr[10].indexOf("=")+1);
            String longitude=splitStr[12].substring(splitStr[12].indexOf("=")+1);
            LatLng point=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
            MarkerOptions markerOptions1=new MarkerOptions();
            markerOptions1.title(locationAddress);
            markerOptions1.snippet(address);
            markerOptions1.position(point);
            mGoogleMap.addMarker(markerOptions1);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));


        }

    }



    private void getSampleMarkerItems() {


        markerList.add(new MarkerItem(37.562603, 126.990499, "New stay inn"));
        markerList.add(new MarkerItem(37.589148, 127.018659, "RG ??????"));
        markerList.add(new MarkerItem(37.601480, 127.020532, "W Mini hotel"));
        markerList.add(new MarkerItem(37.495893, 126.845681, "???????????? ??????"));
        markerList.add(new MarkerItem(37.540524, 127.063458, "?????? HOTEL K WORLD"));
        markerList.add(new MarkerItem(37.558614, 127.076551, "?????? ??? ???????????????"));
        markerList.add(new MarkerItem(37.551446, 127.068994, "?????? ?????? "));
        markerList.add(new MarkerItem(37.588943, 127.054971, "????????? ???\t"));
        markerList.add(new MarkerItem(37.540975, 127.142258, "?????? ???"));
        markerList.add(new MarkerItem(37.654705, 127.058582, "?????? ?????? ??????"));
        markerList.add(new MarkerItem(37.544186, 126.973494, "????????? ??????"));
        markerList.add(new MarkerItem(37.566715, 127.053543, "????????? SM ????????? ??????"));
        markerList.add(new MarkerItem(37.572356, 127.047366, "????????? ???\t"));
        markerList.add(new MarkerItem(37.573277, 127.023885, "????????? ??????"));
        markerList.add(new MarkerItem(37.617784, 126.920399, "???????????????"));
        markerList.add(new MarkerItem(37.572334, 126.991096, "???????????? ??????"));
        markerList.add(new MarkerItem(37.566875, 126.987948, "????????????????????????\t"));
        markerList.add(new MarkerItem(37.571902, 126.989559, "?????? ??????\t"));
        markerList.add(new MarkerItem(37.499109, 126.918886, "????????? ???????????? ??????"));
        markerList.add(new MarkerItem(37.599834, 126.921207, "????????? ?????? ??????"));
        markerList.add(new MarkerItem(37.474380, 126.980955, "?????? ?????? 25"));
        markerList.add(new MarkerItem(37.506944, 127.054003, "?????? ????????????"));
        markerList.add(new MarkerItem(37.595709, 127.093531, "?????? ?????? ????????????\t"));
        markerList.add(new MarkerItem(37.598135, 127.093704, "?????? ?????? ????????????\t"));
        markerList.add(new MarkerItem(37.597906, 127.093721, "?????? ????????????\t"));
        markerList.add(new MarkerItem(37.486454, 127.013594, "?????? LAVA ?????????????????????\t"));
        markerList.add(new MarkerItem(37.634712, 127.021318, "?????? ?????????"));
        markerList.add(new MarkerItem(37.635796, 127.024615, "?????? ?????????"));
        markerList.add(new MarkerItem(37.635887, 127.024577, "?????? ????????? ??????\t"));
        markerList.add(new MarkerItem(37.572556, 126.981136, "??????????????? ?????????\t"));
        markerList.add(new MarkerItem(37.562378, 127.006777, "????????? ??????"));
        markerList.add(new MarkerItem(37.531876, 126.971148, "????????? ??????"));
        markerList.add(new MarkerItem(37.557429, 126.942554, "???????????? ????????????"));
        markerList.add(new MarkerItem(37.517898, 126.910180, "????????? ?????????"));
        markerList.add(new MarkerItem(37.563343, 127.035042, "????????? FULLMOON\t"));
        markerList.add(new MarkerItem(37.562794, 127.034515, "????????? ???????????????"));
        markerList.add(new MarkerItem(37.562554, 127.034765, "????????? ?????????"));
        markerList.add(new MarkerItem(37.602088, 127.062265, "?????? Life hotel Raha"));
        markerList.add(new MarkerItem(37.510933, 127.081628, "?????? FORESTAR"));
        markerList.add(new MarkerItem(37.562703, 127.036914, "?????? ??????\t"));
        markerList.add(new MarkerItem(37.648337, 127.043814, "?????? HOTEL 99 ???????????????"));
        markerList.add(new MarkerItem(37.607723, 127.078534, "?????? ?????? ?????????"));
        markerList.add(new MarkerItem(37.565618, 126.979433, "??????????????? ??????"));
        markerList.add(new MarkerItem(37.534762, 126.993572, "????????? ??????"));
        markerList.add(new MarkerItem(37.560577, 126.997191, "????????????"));
        markerList.add(new MarkerItem(37.551964, 126.917619, "?????? ??? ???????????????"));
        markerList.add(new MarkerItem(126.917619, 126.846286, "?????? ??????\t"));
        markerList.add(new MarkerItem(37.496191,127.029881,"?????? BNN "));
        markerList.add(new MarkerItem(37.536080,127.136833,"?????? ?????????"));
        markerList.add(new MarkerItem(37.525013,126.876242,"?????? ???"));
        markerList.add(new MarkerItem(37.494226,126.985963,"?????? STYLE"));
        markerList.add(new MarkerItem(37.475626,126.980688,"?????? KOTEL"));
        markerList.add(new MarkerItem(37.483051,126.954581,"??????????????? ??????"));
        markerList.add(new MarkerItem(37.515606,127.016519,"?????? ????????????"));
        markerList.add(new MarkerItem(37.510746,127.080299,"?????? ??????\t"));
        markerList.add(new MarkerItem(37.501308,127.042083,"?????? ?????????"));
        markerList.add(new MarkerItem(37.520597,126.903476,"????????? ??????"));
        markerList.add(new MarkerItem(37.518534,126.908169,"????????? ????????????"));
        markerList.add(new MarkerItem(37.515765,127.109191,"?????? ?????????"));
        markerList.add(new MarkerItem(37.534189,127.133519,"?????? ???"));
        markerList.add(new MarkerItem(37.529219,126.847286,"?????? ?????????"));

        for (MarkerItem markerItem : markerList) {
            addMarker(markerItem, false);
        }
    }

    private Marker addMarker(MarkerItem markerItem, boolean isSelectedMarker) {
        LatLng position = new LatLng(markerItem.getLat(), markerItem.getLon());
        String motelName = markerItem.getMotelName();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(String.valueOf(motelName));
        markerOptions.position(position);


        return mGoogleMap.addMarker(markerOptions);
    }


    @Override
    public void onLocationChanged(Location location) {

        currentPosition
                = new LatLng(location.getLatitude(), location.getLongitude());


        Log.d(TAG, "onLocationChanged : ");

        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "??????:" + String.valueOf(location.getLatitude())
                + " ??????:" + String.valueOf(location.getLongitude());

        //?????? ????????? ?????? ???????????? ??????
        //setCurrentLocation(location, markerTitle, markerSnippet);

        mCurrentLocatiion = location;
    }



    @Override
    protected void onStart() {

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected() == false) {

            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {

        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {


        if (mRequestingLocationUpdates == false) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : ????????? ????????? ??????");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            } else {

                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }


    @Override
    public void onConnectionSuspended(int cause) {

        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }


    public String getCurrentAddress(LatLng latlng) {

        //????????????... GPS??? ????????? ??????
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //???????????? ??????
            Toast.makeText(this, "???????????? ????????? ????????????", Toast.LENGTH_LONG).show();
            return "???????????? ????????? ????????????";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "????????? GPS ??????", Toast.LENGTH_LONG).show();
            return "????????? GPS ??????";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "?????? ?????????", Toast.LENGTH_LONG).show();
            return "?????? ?????????";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(false);


        currentMarker = mGoogleMap.addMarker(markerOptions);


        if (mMoveMapByAPI) {

            Log.d(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude());
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15));
        }
    }



    public void setDefaultLocation() {

        mMoveMapByUser = false;


        //????????? ??????, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "???????????? ????????? ??? ??????";
        String markerSnippet = "?????? ???????????? GPS ?????? ?????? ???????????????";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //??????????????? ????????? ????????? ????????? ?????? ????????????
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("?????? ??????????????? ???????????? ????????????????????????.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("????????? ?????? + Don't ask again(?????? ?????? ??????) " +
                    "?????? ????????? ????????? ????????? ???????????? ????????? ?????????????????????.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : ????????? ????????? ??????");

            if (mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : ????????? ????????? ??????");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {


                if (mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }


            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchMapActivity.this);
        builder.setTitle("??????");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchMapActivity.this);
        builder.setTitle("??????");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchMapActivity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : ????????? ????????? ??????");


                        if (mGoogleApiClient.isConnected() == false) {

                            Log.d(TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }

    private Marker addMarker(Marker marker, boolean isSelectedMarker) {
        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;
        String motelName = String.valueOf(marker.getTitle());
        MarkerItem temp = new MarkerItem(lat, lon, motelName);
        return addMarker(temp, isSelectedMarker);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        mGoogleMap.animateCamera(center);

        changeSelectedMarker(marker);

        return true;


    }

    private void changeSelectedMarker(Marker marker) {
        if (selectedMarker != null) {
            addMarker(selectedMarker, false);
            selectedMarker.remove();
        }
        if (marker != null) {
            selectedMarker = addMarker(marker, true);
            marker.remove();
        }
    }

    public void onMapClick(LatLng latLng) {
        changeSelectedMarker(null);
    }

    @Override
    public void onMyLocationChange(Location myLocation) {
        double d1=myLocation.getLatitude();
        double d2=myLocation.getLongitude();
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d1,d2),15));
    }
}
