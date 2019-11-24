package com.app.dogsapp.tools;

import android.content.ContentProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class Storage {

    public static final String DOG_IMAGE="_dog";
    public static final String USER_IMAGE="_user";


    FirebaseStorage storage;
    StorageReference storageRef;

    String path;

    public Storage(){
        storage=FirebaseStorage.getInstance();
        storageRef=storage.getReference();
    }




    public void loadImage(String name,OnSuccessListener<Uri> listener){
        StorageReference pictureRef =storageRef.child(name+".jpg");

        pictureRef.getDownloadUrl().addOnSuccessListener(listener).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("STORAGE","File not found");
            }
        });


    }

    public void loadImage(String name,OnSuccessListener<Uri> sListener,OnFailureListener fListener){
        StorageReference pictureRef =storageRef.child(name+".jpg");

        pictureRef.getDownloadUrl().addOnSuccessListener(sListener).addOnFailureListener(fListener);


    }

    public String uploadImage(String name, Bitmap bmp, OnCompleteListener onComplete, final Context context){



        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        StorageReference pictureRef =storageRef.child(name+".jpg");
        path=pictureRef.getPath();
        UploadTask uploadTask = pictureRef.putBytes(byteArray);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                path=null;
            }
        });
        uploadTask.addOnCompleteListener(onComplete);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Failed to update the data.",Toast.LENGTH_LONG).show();
            }
        });

        return path;

    }

}
