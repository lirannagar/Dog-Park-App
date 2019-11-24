package com.app.dogsapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dogsapp.R;
import com.app.dogsapp.activities.MainActivity;
import com.app.dogsapp.adapters.DogsListAdapter;
import com.app.dogsapp.adapters.MessagesListAdapter;
import com.app.dogsapp.objects.Message;
import com.app.dogsapp.objects.MyFragment;
import com.app.dogsapp.objects.NearbyUser;
import com.app.dogsapp.objects.Park;
import com.app.dogsapp.tools.Networking;
import com.app.dogsapp.tools.Storage;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;


public class ParkInfoFragment extends MyFragment implements DogsListAdapter.DogsListAdapterOnClickHandler {


    ParkInfoFragmentListener listener;

    public void setOnUserInParkSelectedListener(ParkInfoFragmentListener listener) {
        this.listener = listener;

    }


    private Park park;

    List<NearbyUser> nearbyUsers;

    private RecyclerView dogsAround;
    private RecyclerView.LayoutManager layoutManager;
    private DogsListAdapter mDogsListAdapter;

    private RecyclerView messageRV;
    private RecyclerView.LayoutManager layoutManager2;
    private MessagesListAdapter mMessagesListAdapter;
    private EditText messageET;
    private TextView parkName;


    private SharedPreferences prefs;

    private Button sendBtn;
    private ChildEventListener childEventListener;


    public static ParkInfoFragment newInstance() {
        return new ParkInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.park_info_fragment, container, false);

        init();

        //Get the park object
        if (getArguments() != null) {
            park = getArguments().getParcelable("park");
        }

        prefs = mContext.getSharedPreferences(mContext.getString(R.string.prefs_name), MODE_PRIVATE);

        messageRV = view.findViewById(R.id.messages_rv);
        dogsAround = view.findViewById(R.id.dogs_rv);
        sendBtn = view.findViewById(R.id.send_btn);
        messageET = view.findViewById(R.id.message_et);
        parkName = view.findViewById(R.id.park_name);
        parkName.setText(park.getParkName() + ":");


        //Upload the message to the database
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgDate = new SimpleDateFormat("MM/dd-HH:mm ", Locale.getDefault()).format(new Date());

                db.writeMessage(park.getId(), new Message(msgDate,  prefs.getString("username", ""), userID, messageET.getText().toString()));
                messageET.setText("");
            }
        });


        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messageRV.setHasFixedSize(false);
        dogsAround.setHasFixedSize(true);

        //Init the Recycler Views
        layoutManager2 = new LinearLayoutManager(mContext);
        dogsAround.setLayoutManager(layoutManager2);
        mDogsListAdapter = new DogsListAdapter(this, mContext, storage);
        dogsAround.setAdapter(mDogsListAdapter);


        layoutManager = new LinearLayoutManager(mContext);
        messageRV.setLayoutManager(layoutManager);
        mMessagesListAdapter = new MessagesListAdapter(mContext);
        messageRV.setAdapter(mMessagesListAdapter);

        //This child event listener check if there are any new messages and updates the recycler view
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Message newMes = dataSnapshot.getValue(Message.class);


                mMessagesListAdapter.addToList(newMes);
                messageRV.scrollToPosition(mMessagesListAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        db.getMessages(park.getId(), childEventListener);


        loadData();
    }


    @Override
    public void onClick(NearbyUser user) {

        listener.setOnUserInParkSelected(user);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.stopListener(park.getId(), childEventListener);
    }

    public void loadData() {

        mProgressListener.loadingStarted();
        addTasks(1);
        //Get the users near the park
        Callback callback = new Callback() {


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

                    String res = response.body().string();


                    nearbyUsers = Networking.parseJSONNearbyUsers(res);


                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                taskDone();
                                mDogsListAdapter.setList(nearbyUsers);

                            }
                        });

                }
            }
        };

        Networking.getUsersNearby(userID, park.getLatitude(), park.getLongitude(), park.getRadius(), callback);


    }


    public interface ParkInfoFragmentListener {
        public void setOnUserInParkSelected(NearbyUser user);
    }


}
