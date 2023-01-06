package com.trustbank.maps;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.trustbank.Model.AtmModel;
import com.trustbank.Model.LocationModel;
import com.trustbank.R;
import com.trustbank.util.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private List<Marker> originMarkers = new ArrayList<>();
    private ArrayList<LocationModel> locationModels = new ArrayList<>();
    ArrayList<AtmModel> atmModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try {
            gpsTracker = new GPSTracker(MapsActivity.this, this);
            if (getIntent().getExtras() != null) {
                atmModels = (ArrayList<AtmModel>) getIntent().getExtras().getSerializable("locationDetArraylist");
            }


            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }catch (Exception  e){
            e.printStackTrace();
        }

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double lat = gpsTracker.getLatitude();
        double longi = gpsTracker.getLongitude();
        LatLng hcmus1 = new LatLng(lat, longi);

        if (atmModels != null) {
            for (int i = 0; i < atmModels.size(); i++) {
                if (atmModels.get(i).getLatitude() != null && atmModels.get(i).getLongitute() != null) {
                    LatLng hcmus = new LatLng(Double.parseDouble(atmModels.get(i).getLatitude()),
                            Double.parseDouble(atmModels.get(i).getLongitute()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus1, 11));

                    float[] results = new float[1];

                    Location.distanceBetween(lat, longi, Double.parseDouble(atmModels.get(i).getLatitude()), Double.parseDouble(atmModels.get(i).getLongitute()), results);
                    float distanceInMeters = results[0];
                    boolean isWithin10km = distanceInMeters < 10000;
                    if (isWithin10km) {
                        CircleOptions circleOptions = new CircleOptions()
                                .center(hcmus1)
                                .radius(10000)
                                .strokeWidth(2)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.parseColor("#500084d3"));

                        mMap.addCircle(circleOptions);

                        originMarkers.add(mMap.addMarker(new MarkerOptions()
                                .title(atmModels.get(i).getTerminalCode())
                                .snippet(atmModels.get(i).getLocation() + ", " + atmModels.get(i).getCity() + ", " +
                                        atmModels.get(i).getState() + "-" + atmModels.get(i).getPincode())
                                .position(hcmus)));
                    } else {

                        originMarkers.add(mMap.addMarker(new MarkerOptions()
                                .title(atmModels.get(i).getTerminalCode())
                                .snippet(atmModels.get(i).getLocation() + ", " + atmModels.get(i).getCity() + ", " +
                                        atmModels.get(i).getState() + "-" + atmModels.get(i).getPincode())
                                .position(hcmus)));

                    }

                    mMap.setMyLocationEnabled(true);

                }
            }
        }
    }
}
