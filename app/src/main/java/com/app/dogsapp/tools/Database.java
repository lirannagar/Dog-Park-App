package com.app.dogsapp.tools;

import android.content.Context;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.dogsapp.objects.Location;
import com.app.dogsapp.objects.Message;
import com.app.dogsapp.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.*;


public class Database {

    FirebaseDatabase database;




    public Database() {
        database = FirebaseDatabase.getInstance();
    }

    public void writeUser(String id, User user, OnCompleteListener onComplete, final Context context) {
        database.getReference().child("users").child(id).setValue(user).addOnCompleteListener(onComplete).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to update the data.", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void setFriendship(String userId, String withUser, boolean friend, OnCompleteListener onComplete, final Context context) {
        database.getReference().child("friendships").child(userId).child(withUser).setValue(friend).addOnCompleteListener(onComplete).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to update the data.", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void deleteFriend(String userID, String withUser, DatabaseReference.CompletionListener onComplete){
        database.getReference().child("friendships").child(userID).child(withUser).removeValue(onComplete);
    }


    public void updateLocation(String id, Location loc) {
        database.getReference().child("users").child(id).child("location").setValue(loc);

    }

    public void getUser(String id, ValueEventListener userListener) {
        database.getReference().child("users").child(id).addListenerForSingleValueEvent(userListener);

    }

    public void getMessages(String id, ChildEventListener messageListener) {
        database.getReference().child("messages").child(id).addChildEventListener(messageListener);

    }

    public void stopListener(String id, ChildEventListener messageListener) {
        database.getReference().child("messages").child(id).removeEventListener(messageListener);
    }

    public void writeMessage(String id, Message msg) {
        database.getReference().child("messages").child(id).push().setValue(msg);
    }


    public void getLocations(ValueEventListener locationsListener) {
        database.getReference().child("locations").addListenerForSingleValueEvent(locationsListener);

    }

}
