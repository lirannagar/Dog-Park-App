package com.app.dogsapp.objects;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.fragment.app.Fragment;

import com.app.dogsapp.R;
import com.app.dogsapp.activities.MainActivity;
import com.app.dogsapp.interfaces.ProgressListener;
import com.app.dogsapp.tools.Database;
import com.app.dogsapp.tools.Storage;

import static android.content.Context.MODE_PRIVATE;


//This class is used to avoid repeating the same methods for every fragment
public class MyFragment extends Fragment {

    protected ProgressListener mProgressListener;


    public void setProgressListener(ProgressListener mProgressListener){
        this.mProgressListener=mProgressListener;
    }

    protected Database db;
    protected Storage storage;
    protected Context mContext;
    protected MainActivity activity;
    protected String userID;
    protected SharedPreferences prefs;
    protected int tasks=0;




    //Add a number of tasks
    public void addTasks(int tasks){
        this.tasks=tasks;
    }

    //If a task is finished and there are no other tasks hide the loading view
    public void taskDone(){
        tasks--;
        if(tasks==0){
            Log.d("LOADING","Finished");
            mProgressListener.loadingFinished();
        }
    }

    //Get the variables that every fragment needs
    public void init(){
        activity = (MainActivity) getActivity();
        mContext=activity.getApplicationContext();
        prefs=mContext.getSharedPreferences(activity.getString(R.string.prefs_name),MODE_PRIVATE);
        userID=activity.getUserID();
        db=activity.getDb();
        storage = activity.getStorage();


    }

    public Location getUsersLocation(){
        float latitude=prefs.getFloat("latitude",0);
        float longitude=prefs.getFloat("longitude",0);
        return new Location(latitude,longitude,0);
    }


}
