package com.communer.Fragments;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.communer.Activities.NewReport;
import com.communer.Models.LocationObj;
import com.communer.Models.SecurityEvent;
import com.communer.R;
import com.communer.Utils.AppUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SecurityMap extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private FloatingActionButton fab;

    private HashMap<Marker,SecurityEvent> securityEventHashMap;

    private TextView eventTitle, eventReporter, eventDesc, eventReportedBy;

    private LocationManager mLocationManager;

    private final int NEW_REPORT_CODE = 55;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.security_map, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getContext());
        mMapView.onResume();// needed to get the map to display immediately

        securityEventHashMap = new HashMap<Marker, SecurityEvent>();
        googleMap = mMapView.getMap();
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 5.0){
                    final OvershootInterpolator interpolator = new OvershootInterpolator();
                    ViewCompat.animate(fab).rotation(360f).withLayer().setDuration(600).setListener(new ViewPropertyAnimatorListener() {
                        @Override
                        public void onAnimationStart(View view) {

                        }

                        @Override
                        public void onAnimationEnd(View view) {
                            fab.setRotation(0);

                            Intent mIntent = new Intent(getActivity(), NewReport.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                getActivity().startActivityForResult(mIntent, NEW_REPORT_CODE,
                                        ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                            }else{
                                startActivityForResult(mIntent, NEW_REPORT_CODE);
                                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                            }
                        }

                        @Override
                        public void onAnimationCancel(View view) {

                        }
                    }).setInterpolator(interpolator).start();
                }else{
                    Intent mIntent = new Intent(getActivity(), NewReport.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getActivity().startActivityForResult(mIntent, NEW_REPORT_CODE,
                                ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }else{
                        startActivityForResult(mIntent, NEW_REPORT_CODE);
                        getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.stay_anim);
                    }
                }
            }
        });

        eventTitle = (TextView)view.findViewById(R.id.security_map_subject);
        eventReporter = (TextView)view.findViewById(R.id.security_map_reportedby);
        eventDesc = (TextView)view.findViewById(R.id.security_map_desc);
        eventReportedBy = (TextView)view.findViewById(R.id.security_reporter_name);

        AppUser appUserInstance = AppUser.getInstance();
        ArrayList<SecurityEvent> securityEvents = appUserInstance.getCurrentCommunity().getSecurityEvents();
        for (int i = 0; i < securityEvents.size(); i++) {
            SecurityEvent item = securityEvents.get(i);

            String coords = item.getReport().getLocationObj().getCords();
            coords = coords.replace(" ", "");
            String[] seperatedCoords = coords.split(",");
            double latitude = Double.parseDouble(seperatedCoords[0]);
            double longitude = Double.parseDouble(seperatedCoords[1]);

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(item.getReport().getLocationObj().getTitle());

            String severity = item.getReport().getSeverity();
            if (severity.equals("low")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_green_report));
            }else if (severity.equals("medium")) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_yellow_report));
            }else if (severity.equals("high")){
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_red_report));
            }else{
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_green_report));
            }

            Marker marker = googleMap.addMarker(markerOptions);
            securityEventHashMap.put(marker,item);

            if (i == 0) {
                centerMapMyLocation();
            }

            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    SecurityEvent securityEvent = securityEventHashMap.get(marker);
                    if (securityEvent != null){
                        handleMarkerClick(securityEvent);
                    }
                    return false;
                }
            });
        }
    }

    private void handleMarkerClick(SecurityEvent event){
        String title = event.getReport().getDescription();
        String location = event.getReport().getLocationObj().getTitle();
        String desc = event.getReport().getDescription();
        String reporter_name = event.getReporterName();

        eventTitle.setText(title);
        eventReporter.setText("Has been reported by " + event.getReporterName() + " at " + location);
        eventDesc.setText("Description \n" + desc);

        if (event.getReport().isPoliceReport()){
            eventReportedBy.setText("The police has been reported by " + reporter_name);
        }else{
            eventReportedBy.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case (NEW_REPORT_CODE):
                    SecurityEvent securityEvent = data.getParcelableExtra("security_event");
                    addNewSecurityEvent(securityEvent);
                    break;

                default:
                    break;
            }
        }
    }

    private void addNewSecurityEvent(SecurityEvent securityEvent) {
        String coords = securityEvent.getReport().getLocationObj().getCords();
        coords = coords.replace(" ", "");
        String[] seperatedCoords = coords.split(",");
        double latitude = Double.parseDouble(seperatedCoords[0]);
        double longitude = Double.parseDouble(seperatedCoords[1]);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(securityEvent.getReport().getLocationObj().getTitle());

        String severity = securityEvent.getReport().getSeverity();
        if (severity.equals("low")) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_green_report));
        }else if (severity.equals("medium")) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_yellow_report));
        }else if (severity.equals("high")){
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_red_report));
        }else{
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_green_report));
        }

        Marker marker = googleMap.addMarker(markerOptions);
        securityEventHashMap.put(marker,securityEvent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void centerMapMyLocation(){
        Location location = getLastKnownLocation();
        if (location != null){
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else{
            AppUser appUserInstance = AppUser.getInstance();
            LocationObj communityLocation = appUserInstance.getCurrentCommunity().getLocation();
            if (communityLocation != null){
                String locationCoords = communityLocation.getCords();
                locationCoords = locationCoords.replace(" ","");
                String[] locArray = locationCoords.split(",");

                Double locationLat = Double.parseDouble(locArray[0]);
                Double locationLong = Double.parseDouble(locArray[1]);

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(locationLat, locationLong), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(locationLat, locationLong))      // Sets the center of the map to location user
                        .zoom(15)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager)getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }

            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }
}
