package com.app.dogsapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.app.dogsapp.R;

import com.app.dogsapp.activities.MainActivity;
import com.app.dogsapp.objects.Dog;
import com.app.dogsapp.objects.MyFragment;
import com.app.dogsapp.objects.User;
import com.app.dogsapp.tools.Storage;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SettingsFragment extends MyFragment {


    SettingsFragmentListener listener;
    public void setSettingsFragmentListener(SettingsFragment.SettingsFragmentListener listener){
        this.listener=listener;

    }

    final int USERIMAGE=1;
    final int DOGIMAGE=2;


    private Dog.Size dogsSize;

    private ImageView profilePic,dogsPic;
    private EditText userName,dogsName;
    private RadioGroup dogSizeRG;
    private RadioButton smallRB,largeRB,mediumRB;
    private CheckBox friendlyRB,playfulRB,goodWithPeopleRB;
    private Button saveBtn;


    private boolean changedProfileImage,changedDogImage;

    private User mUser;
    private Dog mDog;



    private int tasks=0;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    private void checkIfDone(){
        if(tasks==0){
            //Save the user's username to use it later
            SharedPreferences.Editor editor = mContext.getSharedPreferences(mContext.getString(R.string.prefs_name), Context.MODE_PRIVATE).edit();
            editor.putString("username",mUser.getUserName());
            editor.apply();

            listener.onSaved();
        }
    }

    OnCompleteListener mOnCompleteListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment,container,false);

        init();

        profilePic = view.findViewById(R.id.user_profile_image);
        dogsPic = view.findViewById(R.id.dogs_profile_image);
        userName = view.findViewById(R.id.username_et);
        dogsName = view.findViewById(R.id.dogsname_et);

        dogSizeRG=view.findViewById(R.id.dogs_size_rg);

        friendlyRB = view.findViewById(R.id.friendly_cb);
        playfulRB = view.findViewById(R.id.playful_cb);
        goodWithPeopleRB = view.findViewById(R.id.good_cb);

        smallRB = view.findViewById(R.id.small_rb);
        mediumRB = view.findViewById(R.id.medium_rb);
        largeRB = view.findViewById(R.id.large_rb);

        saveBtn = view.findViewById(R.id.save_btn);


        smallRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dogsSize= Dog.Size.SMALL;
            }
        });

        mediumRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dogsSize= Dog.Size.MEDIUM;
            }
        });

        largeRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dogsSize= Dog.Size.LARGE;
            }
        });


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, USERIMAGE);
            }
        });

        dogsPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, DOGIMAGE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkData()){
                    uploadData();
                }
            }
        });



        mOnCompleteListener=new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                tasks--;
                checkIfDone();
            }
        };

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext=getActivity().getApplicationContext();
        MainActivity activity = (MainActivity) getActivity();

        userID=activity.getUserID();



        if(this.getArguments()==null)
        loadData();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==Activity.RESULT_OK && null != data){
            if(requestCode==USERIMAGE){
                Uri selectedImage = data.getData();

                changedProfileImage=true;
                Glide.with(this).load(selectedImage).centerCrop().into(profilePic);

            }
            if(requestCode==DOGIMAGE){
                Uri selectedImage = data.getData();
                changedDogImage=true;
                Glide.with(this).load(selectedImage).centerCrop().into(dogsPic);
            }

        }

    }


    public boolean checkData(){
        if(userName.getText().toString().length()<=0) {
            userName.setError("Please enter your user name");
            return false;
        }

        if(dogsName.getText().toString().length()<=0) {
            dogsName.setError("Please enter dog's name");
            return false;
        }


        if(dogsSize==null) {
            mProgressListener.showToastMessage("Please select dog's size");
            return false;
        }



        return true;
    }

    public void uploadData(){

        tasks=1;
        //Upload the images
        boolean uploadDone=uploadImages();
        if(!uploadDone){
           mProgressListener.showToastMessage("There was an error");
        }

        //Update Database
        if(mDog==null)
        mDog=new Dog();
        mDog.setName(dogsName.getText().toString());
        mDog.setSize(dogsSize);
        List<Dog.Attributes> dogAttributes=new ArrayList<>();
        if(playfulRB.isChecked())dogAttributes.add(Dog.Attributes.PLAYFUL);
        if(friendlyRB.isChecked())dogAttributes.add(Dog.Attributes.FRIENDLY);
        if(goodWithPeopleRB.isChecked()) dogAttributes.add(Dog.Attributes.GOODWITHPEOPLE);

        mDog.setAttributesList(dogAttributes);

        if(mUser==null)
        mUser=new User();
        mUser.setUserName(userName.getText().toString());
        mUser.setDog(mDog);




        db.writeUser(userID,mUser,mOnCompleteListener,mContext);




    }

    public void loadData(){

        addTasks(1);
        mProgressListener.loadingStarted();
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                mUser= dataSnapshot.getValue(User.class);
                if(mUser!=null && mUser.getUserName()!=null){
                    taskDone();
                    userName.setText(mUser.getUserName());
                    userName.setEnabled(false);
                    mDog=mUser.getDog();
                    dogsName.setText( mDog.getName());
                    //Get dogs attributes
                    for(int i=0;mDog.getAttributesList()!=null &&i< mDog.getAttributesList().size();i++){
                        if(mDog.getAttributesList().get(i).equals(Dog.Attributes.FRIENDLY)){
                            friendlyRB.setChecked(true);
                        }else
                        if(mDog.getAttributesList().get(i).equals(Dog.Attributes.GOODWITHPEOPLE)){
                            goodWithPeopleRB.setChecked(true);
                        }else
                        if(mDog.getAttributesList().get(i).equals(Dog.Attributes.PLAYFUL)){
                            playfulRB.setChecked(true);
                        }
                    }

                    //Get dogs size
                    dogsSize=mDog.getSize();
                    if(dogsSize.equals(Dog.Size.SMALL))
                        smallRB.setChecked(true);
                    else
                        if(dogsSize.equals(Dog.Size.MEDIUM))
                            mediumRB.setChecked(true);
                        else
                            largeRB.setChecked(true);


                    //Load the images if they exist
                    storage.loadImage(userID+Storage.USER_IMAGE, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(SettingsFragment.this).load(uri).centerCrop().into(profilePic);

                        }
                    });

                    storage.loadImage(userID+Storage.DOG_IMAGE, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(SettingsFragment.this).load(uri).centerCrop().into(dogsPic);

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                mProgressListener.showGeneralError();
            }
        };


        db.getUser(userID,userListener);





    }






    public boolean uploadImages(){




        if(changedProfileImage) {
            tasks++;
            BitmapDrawable drawable = (BitmapDrawable) profilePic.getDrawable();
            String profilePath = storage.uploadImage(userID+Storage.USER_IMAGE, drawable.getBitmap(),mOnCompleteListener,mContext);
            if (profilePath == null) {
                return false;
            }

        }
        if(changedDogImage){
            tasks++;
            BitmapDrawable drawable = (BitmapDrawable) dogsPic.getDrawable();
            String dogsPath=storage.uploadImage(userID+Storage.DOG_IMAGE,drawable.getBitmap(),mOnCompleteListener,mContext);
            if(dogsPath==null){
                return false;
            }

        }


        return true;
    }




    public interface  SettingsFragmentListener{
        public void onSaved();

    }




}
