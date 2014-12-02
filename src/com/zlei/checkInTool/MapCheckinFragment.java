package com.zlei.checkInTool;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapCheckinFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener {

    private static ArrayList<String> venueNames;
    private static ArrayList<String[]> venueCoordinates;


    private static View view;
    private static GoogleMap map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        view = inflater.inflate(R.layout.activity_map, container, false);

        setUpMapIfNeeded(); // For setting up the MapFragment

        return view;
    }

    public void setUpMapIfNeeded() {
        if (map == null) {
            //TODO need to find a better way
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                map = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.location_map)).getMap();
            else
                map = ((MapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.location_map)).getMap();
            if (map != null)
                setUpMap();
        }
    }

    private void setUpMap() {
        map.setOnInfoWindowClickListener(this);

        if (!MVenuesActivity.venueNames.isEmpty()) {
            venueNames = MVenuesActivity.venueNames;
            venueCoordinates = MVenuesActivity.venueCoordinates;
            String[] coreCoor = venueCoordinates.get(0);
            double coreLat = Double.valueOf(coreCoor[0]);
            double coreLng = Double.valueOf(coreCoor[1]);
            LatLng core = new LatLng(coreLat, coreLng);

            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(core, 16));

            for (String name : venueNames) {
                int i = venueNames.indexOf(name);
                coreCoor = venueCoordinates.get(i);
                coreLat = Double.valueOf(coreCoor[0]);
                coreLng = Double.valueOf(coreCoor[1]);
                core = new LatLng(coreLat, coreLng);
                if (coreCoor[2].equals("checkable")) {
                    map.addMarker(new MarkerOptions()
                            .title(name)
                            .snippet("Click to check in here!")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                            .position(core));
                } else {
                    map.addMarker(new MarkerOptions()
                            .title(name)
                            .position(core));
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (map != null)
            setUpMap();

        if (map == null) {
            map = ((MapFragment) MainActivity.fragmentManager
                    .findFragmentById(R.id.location_map)).getMap();
            if (map != null)
                setUpMap();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (map != null) {
            MainActivity.fragmentManager.beginTransaction()
                    .remove(MainActivity.fragmentManager.findFragmentById(R.id.location_map)).commit();
            map = null;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int position = venueNames.indexOf(marker.getTitle());
        if (venueCoordinates.get(position)[2].equals("checkable")) {
            Intent i = new Intent(this.getActivity(), MCheckInActivity.class);
            i.putExtra("selectedVenue", position);
            startActivity(i);
        }
    }
}