package com.app.dogsapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.app.dogsapp.R;
import com.app.dogsapp.activities.MainActivity;
import com.app.dogsapp.objects.Dog;
import com.app.dogsapp.objects.MyFragment;
import com.app.dogsapp.objects.User;
import com.app.dogsapp.tools.Database;
import com.app.dogsapp.tools.Storage;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class InfoFragment  extends MyFragment {




    //Views
    private ImageView profilePic,dogsPic;
    private TextView userName,dogsName;
    private TextView dogsSize,dogsAttributes;
    private Button friend_btn,enemy_btn,locationBtn,defaultBtn;
    private TextView statusText;



    //Variables
    private String currentUserID="testUser";
    private User mUser;
    private Dog mDog;
    private int friendshipStatus=-1;//-1=Unknown,0=Enemies,1=Friends



    public static InfoFragment newInstance() {
        return new InfoFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment,container,false);

        init();

        if(getArguments()!=null) {
            currentUserID = getArguments().getString("userID");
            friendshipStatus=getArguments().getInt("friendship");
        }

        profilePic = view.findViewById(R.id.info_profile_iv);
        dogsPic = view.findViewById(R.id.info_dog_iv);
        userName = view.findViewById(R.id.info_username);
        dogsName = view.findViewById(R.id.info_dogs_name);
        dogsSize = view.findViewById(R.id.info_dogs_size);
        statusText = view.findViewById(R.id.status_text);
        locationBtn=view.findViewById(R.id.location_btn);

        dogsAttributes = view.findViewById(R.id.info_dogs_attributes);






        //It shows the users location on the google maps app
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUser!=null && mUser.getLocation()!=null) {

                    // Create a Uri from an intent string. Use the result to create an Intent.
                    Uri gmmIntentUri = Uri.parse("geo:" + mUser.getLocation().getLatitude() + "," + mUser.getLocation().getLongitude());


                    // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    // Make the Intent explicit by setting the Google Maps package
                    mapIntent.setPackage("com.google.android.apps.maps");

                    // Attempt to start an activity that can handle the Intent
                    if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }

                }

            }
        });


        friend_btn = view.findViewById(R.id.friend_btn);
        friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnCompleteListener listener=new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        friendshipStatus=1;
                        enemy_btn.setEnabled(true);
                        friend_btn.setEnabled(false);
                    }
                };
                db.setFriendship(userID,currentUserID,true,listener, mContext);
            }
        });




        enemy_btn = view.findViewById(R.id.enemy_btn);
        enemy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnCompleteListener listener=new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        friendshipStatus=0;
                        enemy_btn.setEnabled(false);
                        friend_btn.setEnabled(true);
                    }
                };
                db.setFriendship(userID,currentUserID,false,listener, mContext);
            }
        });

        defaultBtn = view.findViewById(R.id.default_btn);
        defaultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference.CompletionListener listener =new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        friendshipStatus=-1;
                        enemy_btn.setEnabled(true);
                        friend_btn.setEnabled(true);
                    }
                };

                db.deleteFriend(userID,currentUserID,listener);
            }
        });


        if(friendshipStatus==1){
            friend_btn.setEnabled(false);
        }else if(friendshipStatus==0){
            enemy_btn.setEnabled(false);
        }








        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        loadData();

    }


    public void loadData(){

        //Show the loading view
        mProgressListener.loadingStarted();
        //It runs 3 taks on the backgrounf show set the task to 3
        addTasks(3);
        //Value event listener that handles that data take from Firebase database
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get User object and use the values to update the UI
                mUser= dataSnapshot.getValue(User.class);
                //If the user isn't null initialize the views
                if(mUser!=null){

                    userName.setText(mUser.getUserName());

                    mDog=mUser.getDog();
                    dogsName.setText( mDog.getName());
                    String attributes="";
                    //Get dogs attributes
                    for(int i=0;mDog.getAttributesList()!=null &&i< mDog.getAttributesList().size();i++){
                        if(mDog.getAttributesList().get(i).equals(Dog.Attributes.FRIENDLY)){
                            attributes+="\nFriendly";
                        }else
                        if(mDog.getAttributesList().get(i).equals(Dog.Attributes.GOODWITHPEOPLE)){
                            attributes+="\nGood with people";
                        }else
                        if(mDog.getAttributesList().get(i).equals(Dog.Attributes.PLAYFUL)){
                            attributes+="\nPlayful";
                        }
                    }

                    dogsAttributes.setText("Attributes:"+attributes);

                    //Get dogs size
                    String size="";
                    if(mDog.getSize().equals(Dog.Size.SMALL))
                        size="Small";
                    else
                    if(mDog.getSize().equals(Dog.Size.MEDIUM))
                        size="Medium";
                    else
                        size="Large";


                    dogsSize.setText("Size:"+size);

                    //Set the status info
                    if(mUser.getLocation()!=null) {
                        //Check if the user updated his location 15 minutes or less from now
                        long lastUpdate=mUser.getLocation().getLastUpdate();
                        if(System.currentTimeMillis()-lastUpdate<(15*60*1000)){
                            statusText.setText(mContext.getString(R.string.status_online));
                            statusText.setTextColor(ContextCompat.getColor(mContext, R.color.online_color));

                        }else{
                            statusText.setText(mContext.getString(R.string.status_offline));
                            statusText.setTextColor(ContextCompat.getColor(mContext, R.color.offline_color));
                        }


                    }else{
                        statusText.setText(mContext.getString(R.string.status_unknown));
                    }

                    //Load the images if they exist
                    storage.loadImage(currentUserID + Storage.USER_IMAGE, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            taskDone();
                            Glide.with(InfoFragment.this).load(uri).centerCrop().into(profilePic);
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            taskDone();
                        }
                    });

                    storage.loadImage(currentUserID + Storage.DOG_IMAGE, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            taskDone();
                            Glide.with(InfoFragment.this).load(uri).centerCrop().into(dogsPic);
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            taskDone();
                        }
                    });

                    taskDone();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                mProgressListener.showGeneralError();
            }
        };


        db.getUser(currentUserID,userListener);





    }




}
