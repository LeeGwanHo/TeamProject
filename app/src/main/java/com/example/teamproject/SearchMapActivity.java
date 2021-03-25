package com.example.teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

public class SearchMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;

    private static final int LOCATION_PERMISSION_REQUEST_CODE=1000;
    private FusedLocationSource locationSource;
    private  NaverMap naverMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_map);

        mapView=findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);



        locationSource=new FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)){
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap=naverMap;

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);


        UiSettings uiSettings=naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true); //나침반
        uiSettings.setScaleBarEnabled(true);//거리
        uiSettings.setZoomControlEnabled(true);//줌
        uiSettings.setLocationButtonEnabled(true);//내가 있는곳

        Marker marker=new Marker();
        marker.setPosition(new LatLng(37.5670135,126.9783740));
        marker.setMap(naverMap);
    }
}