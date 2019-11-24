package com.app.dogsapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dogsapp.R;
import com.app.dogsapp.adapters.DogsListAdapter;
import com.app.dogsapp.objects.Dog;
import com.app.dogsapp.objects.Location;
import com.app.dogsapp.objects.MyFragment;
import com.app.dogsapp.objects.NearbyUser;
import com.app.dogsapp.objects.User;
import com.app.dogsapp.tools.Networking;
import com.app.dogsapp.tools.Storage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainFragment extends MyFragment implements DogsListAdapter.DogsListAdapterOnClickHandler {
    //Distance in meters between the curent user and the others
    final double DISTANCE_FROM_USER=1000000;

    MainFragmentListener listener;

    public void setOnUserSelectedListener(MainFragmentListener listener){
        this.listener=listener;

    }


    //Views
    private TextView userName;
    private TextView dogsName;
    private ImageView userProfilePic;
    private RecyclerView dogsAround;
    private RecyclerView.LayoutManager layoutManager;
    private DogsListAdapter mDogsListAdapter;
    private Button friendsBtn,parksBtn;



    private User mUser;
    private Dog mDog;





    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_screen_fragment,container,false);



        //Initialize the views
        userName = view.findViewById(R.id.username_tv);
        dogsName = view.findViewById(R.id.dogs_name_tv);
        userProfilePic = view.findViewById(R.id.profile_iv);

        dogsAround = view.findViewById(R.id.dogs_arround_rv);

        //Buttons
        parksBtn= view.findViewById(R.id.parks_btn);

        parksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            listener.onShowParks();

            }
        });

        friendsBtn= view.findViewById(R.id.friends_btn);

        friendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onShowFriends();

            }
        });


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();

        //Initialize the Recycler View
        dogsAround.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext);
        dogsAround.setLayoutManager(layoutManager);
        mDogsListAdapter = new DogsListAdapter(this,mContext,storage);
        dogsAround.setAdapter(mDogsListAdapter);


        //Load the data
        loadData();
    }

    @Override
    public void onClick(NearbyUser user) {

        //On click show the user information
        listener.onUserSelected(user);
    }





    //Load the user's data and the nearby users
    public void loadData(){

        mProgressListener.loadingStarted();
        addTasks(2);

        //Get the current user
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                mUser= dataSnapshot.getValue(User.class);
                if(mUser!=null && mUser.getUserName()!=null){

                    userName.setText(mUser.getUserName());

                    mDog=mUser.getDog();
                    dogsName.setText("Dog: " + mDog.getName());

                    //Load the image if it exists
                    storage.loadImage(userID+Storage.USER_IMAGE, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Glide.with(MainFragment.this).load(uri).placeholder(R.drawable.profile_icon).centerCrop().apply(RequestOptions.bitmapTransform(new RoundedCorners(14))).into(userProfilePic);
                        }
                    });


                }else{
                    listener.onUserDoesntExist();

                }
                taskDone();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                taskDone();
                mProgressListener.showGeneralError();
            }
        };
        db.getUser(userID,userListener);



        //Get the users nearby
        Callback callback=new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                mProgressListener.showGeneralError();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mProgressListener.showGeneralError();
                    throw new IOException("Unexpected code " + response);
                } else {

                    String res=response.body().string();

                    List<NearbyUser> list=Networking.parseJSONNearbyUsers(res);
                    mDogsListAdapter.setList(list);
                    taskDone();


                }

            }
        };
        Location currentLocation=getUsersLocation();
        Networking.getUsersNearby(userID,currentLocation.getLatitude(),currentLocation.getLongitude(),DISTANCE_FROM_USER,callback);



    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public interface  MainFragmentListener{
        public void onUserSelected(NearbyUser user);
        public void onUserDoesntExist();
        public void onShowFriends();
        public void onShowParks();
    }






}
