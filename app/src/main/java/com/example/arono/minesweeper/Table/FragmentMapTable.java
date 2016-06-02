package com.example.arono.minesweeper.Table;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arono.minesweeper.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FragmentMapTable extends Fragment {

    private final LatLng ISRAEL_LATLNG = new LatLng(32.06615,34.7778);
    private GoogleMap googleMap;
    private TableManager table;
    private Geocoder coder;
    private List<Address> address = new ArrayList<>();
    public  FragmentMapTable(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        MapFragment mapFragment = MapFragment.newInstance();
        transaction.add(R.id.mapsPlaceHolder, mapFragment);
        transaction.commit();
        table = new TableManager(getActivity());
        table.loadScores();
        coder = new Geocoder(getActivity());


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setGoogleMap(googleMap);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ISRAEL_LATLNG, 13));
                for (Score s : table.getScores()) {
                    try {
                        address = coder.getFromLocation(s.getPosition().latitude, s.getPosition().longitude, 1);
                        if (address.size() != 0) {
                            Log.e("address", address.get(0).getAddressLine(0));
                            googleMap.addMarker(new MarkerOptions().position(s.getPosition()).title(address.get(0).getAddressLine(0)).snippet("name:" + s.getName() + " time:" + s.getTime()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void setGoogleMap(GoogleMap googleMap){
        this.googleMap = googleMap;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_fragment_map_table,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
