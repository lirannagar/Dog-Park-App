package com.app.dogsapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dogsapp.R;
import com.app.dogsapp.activities.MainActivity;
import com.app.dogsapp.adapters.FriendsListAdapter;
import com.app.dogsapp.objects.Friend;
import com.app.dogsapp.objects.MyFragment;
import com.app.dogsapp.tools.Networking;
import com.app.dogsapp.tools.Storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RelationsFragment extends MyFragment implements FriendsListAdapter.FriendsListAdapterOnClickHandler {


    RelationsFragment.RelationsFragmentListener listener;

    public void setRelationsFragmentListener(RelationsFragment.RelationsFragmentListener listener){
        this.listener=listener;

    }



    private RecyclerView friendsListView;
    private RecyclerView enemiesListView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.LayoutManager layoutManager2;
    private FriendsListAdapter mFriendListAdapter;
    private FriendsListAdapter mEnemyListAdapter;


    private List<Friend> friendsList,enemiesList;



    public static RelationsFragment newInstance() {
        return new RelationsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.relations_fragment,container,false);

        init();

        friendsListView=view.findViewById(R.id.friends_list);
        enemiesListView=view.findViewById(R.id.enemies_list);



        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        friendsListView.setHasFixedSize(true);
       enemiesListView.setHasFixedSize(true);

        mContext=getActivity();

        MainActivity activity = (MainActivity) getActivity();

        userID=activity.getUserID();


        storage = new Storage();





        // use a linear layout manager
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager2 = new LinearLayoutManager(mContext);
        friendsListView.setLayoutManager(layoutManager);
        enemiesListView.setLayoutManager(layoutManager2);





        mFriendListAdapter = new FriendsListAdapter(this,mContext,storage);
        mEnemyListAdapter = new FriendsListAdapter(this,mContext,storage);
        friendsListView.setAdapter( mFriendListAdapter);
        enemiesListView.setAdapter( mEnemyListAdapter);



        loadData();
    }

    @Override
    public void onClick(Friend user) {

        listener.onFriendSelected(user);
    }

    public void loadData(){

        mProgressListener.loadingStarted();
        addTasks(1);

        Callback callback=new Callback() {



            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mProgressListener.showGeneralError();
                    throw new IOException("Unexpected code " + response);
                } else {


                    String res=response.body().string();


                    List<Friend> list= Networking.parseJSONFriends(res);
                    friendsList=new ArrayList<>();
                    enemiesList=new ArrayList<>();
                    friendsList.clear();
                    enemiesList.clear();

                    for(int i=0;i<list.size();i++){
                        if(list.get(i).isFriend()){
                            friendsList.add(list.get(i));
                        }else{
                            enemiesList.add(list.get(i));
                        }
                    }


                    mFriendListAdapter.setList(friendsList);
                    mEnemyListAdapter.setList(enemiesList);

                    taskDone();

                }
            }
        };
        Networking.getFriends(userID,callback);


    }




    public interface  RelationsFragmentListener{
        public void onFriendSelected(Friend friend);

    }






}
