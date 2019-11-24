package com.app.dogsapp.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.dogsapp.adapters.DogsListAdapter;
import com.app.dogsapp.objects.Friend;
import com.app.dogsapp.objects.NearbyUser;
import com.app.dogsapp.objects.Park;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Networking {


    final static String BASE_URL = "https://us-central1-dogsapp-1d3a6.cloudfunctions.net/";

    final static String NEARBY_URL="getNearbyUsers";
    final static String FRIENDS_URL="getFriendsAndEnemies";
    final static String PARKS_URL="getParksNearby ";

    public static void getUsersNearby(String id,double lat, double lon, double dist, Callback callback){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL+NEARBY_URL).newBuilder();
        urlBuilder.addQueryParameter("lat", ""+lat);
        urlBuilder.addQueryParameter("long", ""+lon);
        urlBuilder.addQueryParameter("dist", ""+dist);
        urlBuilder.addQueryParameter("id",id);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)

                .build();


        client.newCall(request).enqueue(callback);



    }

    public static void getFriends(String id, Callback callback){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL+FRIENDS_URL).newBuilder();
        urlBuilder.addQueryParameter("id",id);

        String url = urlBuilder.build().toString();
        Log.d("NETWORKINGTOOLS",url);
        Request request = new Request.Builder()
                .url(url)

                .build();


        client.newCall(request).enqueue(callback);



    }

    public static void getParks(double lat, double lon, double dist, Callback callback){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL+PARKS_URL).newBuilder();
        urlBuilder.addQueryParameter("lat", ""+lat);
        urlBuilder.addQueryParameter("long", ""+lon);
        urlBuilder.addQueryParameter("dist", ""+dist);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)

                .build();


        client.newCall(request).enqueue(callback);



    }


    public static List<Park> parseJSONParks(String json) {

        List<Park> parks=new ArrayList<>();
        try {

            JSONArray reader = new JSONArray(json);

            for(int i=0;i<reader.length();i++){
                Park tempPark=new Park();

                tempPark.setId(reader.getJSONObject(i).getString("id"));
                tempPark.setLatitude(reader.getJSONObject(i).getDouble("latitude"));
                tempPark.setLongitude(reader.getJSONObject(i).getDouble("longitude"));
                tempPark.setParkName(reader.getJSONObject(i).getString("name"));
                tempPark.setRadius(reader.getJSONObject(i).getDouble("radius"));


                parks.add(tempPark);


            }



            Log.e("USERS","USERS:"+reader.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parks;

    }



    public static List<NearbyUser> parseJSONNearbyUsers(String json) {

        List<NearbyUser> nearbyUsers=new ArrayList<>();
        try {

            JSONArray reader = new JSONArray(json);

            for(int i=0;i<reader.length();i++){
                NearbyUser tempUser=new NearbyUser();

                tempUser.setLatitude(reader.getJSONObject(i).getDouble("latitude"));
                tempUser.setLongitude(reader.getJSONObject(i).getDouble("longitude"));
                tempUser.setDogsName(reader.getJSONObject(i).getString("dogsName"));
                tempUser.setFriend(reader.getJSONObject(i).getInt("friend"));
                tempUser.setId(reader.getJSONObject(i).getString("userId"));
                nearbyUsers.add(tempUser);


            }



            Log.e("USERS","USERS:"+reader.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return nearbyUsers;

    }

    public static List<Friend> parseJSONFriends(String json) {

        List<Friend> friends=new ArrayList<>();
        try {

            JSONArray reader = new JSONArray(json);

            for(int i=0;i<reader.length();i++){
                Friend tempFriend=new Friend();

                tempFriend.setId(reader.getJSONObject(i).getString("id"));
                tempFriend.setFriend(reader.getJSONObject(i).getBoolean("friendship"));

                tempFriend.setDogsName(reader.getJSONObject(i).getString("dog"));
                long lastUpdate=reader.getJSONObject(i).getLong("lastUpdate");
                if(lastUpdate>0 && System.currentTimeMillis()-lastUpdate<=(15*60*1000))
                tempFriend.setOnline(true);
                else
                tempFriend.setOnline(false);

                friends.add(tempFriend);


            }



            Log.e("USERS","USERS:"+reader.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return friends;

    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
