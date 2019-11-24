package com.app.dogsapp.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.dogsapp.R;
import com.app.dogsapp.objects.Message;
import com.app.dogsapp.objects.NearbyUser;
import com.app.dogsapp.tools.Storage;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessagesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messagesList=new ArrayList<>();
    private ContextWrapper cw;




    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */

    private Context mContext;

    public MessagesListAdapter( Context mContext) {

        this.mContext = mContext;

        cw= new ContextWrapper(mContext);

    }



    /**
     * Cache of the children views for a category list item.
     */
    public class MessagesListAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mUsername;
        public final TextView mMessage;
        public final TextView mTime;

        public MessagesListAdapterViewHolder(View view) {
            super(view);
            mUsername = (TextView) view.findViewById(R.id.message_username);
            mMessage = (TextView) view.findViewById(R.id.message_text);
            mTime = (TextView) view.findViewById(R.id.message_time);
        }
    }


    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item
     * @return A new RecyclerView.ViewHolder that holds the View for each list item
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();

        int layoutIdForListItem = R.layout.message_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MessagesListAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     * We check if the holder is a AdapterViewHolder
     * and we set up the views
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof MessagesListAdapterViewHolder) {
            final MessagesListAdapterViewHolder messagesListAdapterViewHolder = (MessagesListAdapterViewHolder) holder;

            //get the message data
            String time = messagesList.get(position).getTime() +" ";
            String userName = messagesList.get(position).getUserName()+":";
            String message = messagesList.get(position).getMessage();
            messagesListAdapterViewHolder.mTime.setText(time);
            messagesListAdapterViewHolder.mMessage.setText(message);
            messagesListAdapterViewHolder.mUsername.setText(userName);
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void addToList(Message message){
        messagesList.add(message);
        notifyItemInserted(messagesList.size());
    }

}