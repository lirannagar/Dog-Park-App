package com.app.dogsapp.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dogsapp.R;
import com.app.dogsapp.activities.MainActivity;
import com.app.dogsapp.adapters.ParksListAdapter;
import com.app.dogsapp.objects.Location;
import com.app.dogsapp.objects.MyFragment;
import com.app.dogsapp.objects.NearbyUser;
import com.app.dogsapp.objects.Park;
import com.app.dogsapp.tools.Networking;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class ParksFragment extends MyFragment implements OnMapReadyCallback, ParksListAdapter.ParksListAdapterOnClickHandler {

    ParksFragment.ParksFragmentListener listener;

    public void setParksFragmentListener(ParksFragment.ParksFragmentListener listener){
        this.listener=listener;

    }

    //Distance in meters
    final double DISTANCE_FROM_USER=1000000;





    private List<Park> parksList;
    private List<NearbyUser> nearbyUsersList;


    MapView mapView;
    private GoogleMap gmap;



    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private RecyclerView parksAround;
    private RecyclerView.LayoutManager layoutManager;
    private ParksListAdapter mParksListAdapter;

    private Marker userMarker;

    SharedPreferences.OnSharedPreferenceChangeListener spChanged;

    public static ParksFragment newInstance() {
        return new ParksFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parks_fragment,container,false);


        init();

        //Get the arguments
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView=view.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);


        parksAround = view.findViewById(R.id.parks_list);






        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext=getActivity().getApplicationContext();

        MainActivity activity = (MainActivity) getActivity();

        userID=activity.getUserID();




        //create a listener to update users location in the map every time it changes
       spChanged = new
                SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                          String key) {
                        float latitude=prefs.getFloat("latitude",0);
                        float longitude=prefs.getFloat("longitude",0);
                        userMarker.setPosition(new LatLng(latitude,longitude));

                    }
                };
        prefs.registerOnSharedPreferenceChangeListener(spChanged);



        // use a linear layout manager
        layoutManager = new LinearLayoutManager(mContext);
        parksAround.setLayoutManager(layoutManager);




        mParksListAdapter = new ParksListAdapter(this,mContext);
        parksAround.setAdapter(mParksListAdapter);


        mProgressListener.loadingStarted();
        getParksNearby();
        getNearbyUsers();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showInfoIntoMap(){
        if(gmap!=null && nearbyUsersList!=null && parksList!=null) {


            gmap.clear();
            //Add the user
            float latitude=prefs.getFloat("latitude",0);
            float longitude=prefs.getFloat("longitude",0);
            userMarker= gmap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker))
                    .title("You"));
            gmap.moveCamera(CameraUpdateFactory.newLatLng(userMarker.getPosition()));
            gmap.animateCamera( CameraUpdateFactory.zoomTo( 13.8f ) );


            //Add the parks
            for (int i = 0; i < parksList.size(); i++) {

                gmap.addMarker(new MarkerOptions()
                        .position(new LatLng(parksList.get(i).getLatitude(), parksList.get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.park_marker))
                        .title(parksList.get(i).getParkName()));

                //Ad a circle arround the park to show it's radius
                gmap.addCircle(new CircleOptions().center(new LatLng(parksList.get(i).getLatitude(), parksList.get(i).getLongitude()))
                        .radius(parksList.get(i).getRadius())
                        .strokeColor(Color.GREEN)
                        .fillColor(Color.GREEN));

            }

            //Add the users
            for (int i = 0; i < nearbyUsersList.size(); i++) {
                gmap.addMarker(new MarkerOptions()
                        .position(new LatLng(nearbyUsersList.get(i).getLatitude(), nearbyUsersList.get(i).getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.dog_marker))
                        .title(nearbyUsersList.get(i).getDogsName()));


            }

        }
    }



    public void getParksNearby(){
        addTasks(1);
        float latitude=prefs.getFloat("latitude",0);
        float longitude=prefs.getFloat("longitude",0);

        Callback callback=new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {


                    String res=response.body().string();

                    Log.d("MainFragment.java",res);
                    parksList= Networking.parseJSONParks(res);


                    if(getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                             mParksListAdapter.setList(parksList);
                            showInfoIntoMap();
                        }
                    });
                    taskDone();



                }

            }
        };

        Location currentLocation=getUsersLocation();
        Networking.getParks(currentLocation.getLatitude(),currentLocation.getLongitude(),DISTANCE_FROM_USER,callback);
    }

    public void getNearbyUsers(){

         addTasks(1);


        Callback callback=new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {

                    String res=response.body().string();

                    nearbyUsersList=Networking.parseJSONNearbyUsers(res);

                    if(getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {


                            showInfoIntoMap();
                        }
                    });
                    taskDone();
                }
            }
        };

        Networking.getUsersNearby(userID,0,0,DISTANCE_FROM_USER,callback);


    }





    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        gmap = googleMap;
        gmap.getUiSettings().setZoomGesturesEnabled(true);
        gmap.getUiSettings().setZoomControlsEnabled(true);


        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                showInfoIntoMap();
            }
        });
    }

    @Override
    public void onClick(Park park) {
        if(park!=null)
        listener.onParkSelected(park);
    }

    public interface  ParksFragmentListener{
        public void onParkSelected(Park park);

    }

}
