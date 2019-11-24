package com.app.dogsapp.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.dogsapp.R;
import com.app.dogsapp.objects.NearbyUser;
import com.app.dogsapp.tools.Storage;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class DogsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NearbyUser> usersList = new ArrayList<>();
    private ContextWrapper cw;
    private Storage storage;


    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final DogsListAdapterOnClickHandler mClickHandler;
    private Context mContext;

    public DogsListAdapter(DogsListAdapterOnClickHandler mClickHandler, Context mContext, Storage storage) {
        this.mClickHandler = mClickHandler;
        this.mContext = mContext;
        this.storage = storage;
        cw = new ContextWrapper(mContext);

    }

    /**
     * The interface that receives onClick messages.
     */
    public interface DogsListAdapterOnClickHandler {
        void onClick(NearbyUser user);
    }

    /**
     * Cache of the children views for a category list item.
     */
    public class DogsListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTextView;
        public final ImageView mImageView;
        public final LinearLayout mLayout;

        public DogsListAdapterViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.dog_name_list);
            mImageView = (ImageView) view.findViewById(R.id.dog_list_image);
            mLayout = (LinearLayout) view.findViewById(R.id.dog_item_layout);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();

            mClickHandler.onClick(usersList.get(adapterPosition));
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


        int layoutIdForListItem = R.layout.dog_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new DogsListAdapterViewHolder(view);


    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     * We check if the holder is a AdapterViewHolder
     * and we set up the views
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof DogsListAdapterViewHolder) {
            final DogsListAdapterViewHolder dogsListAdapterViewHolder = (DogsListAdapterViewHolder) holder;

            //Get the data

            String dogsName = usersList.get(position).getDogsName();
            dogsListAdapterViewHolder.mTextView.setText(dogsName);

            int friend = usersList.get(position).getFriend();
            if (friend == 0) {
                dogsListAdapterViewHolder.mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.enemy_color));
            } else if (friend == 1) {
                dogsListAdapterViewHolder.mLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.friend_color));
            } else {
                dogsListAdapterViewHolder.mLayout.setBackgroundColor(0x00000000);
            }


            //Get the image
            //The URI is saved, so we don't need to check if there is an available image every time
            Uri dogsImage = usersList.get(position).getDogImageURI();
            boolean hasImage = usersList.get(position).isHasImage();

            if (dogsImage == null) {
                Glide.with(mContext).clear(dogsListAdapterViewHolder.mImageView);
                //Load the images if they exist
                storage.loadImage(usersList.get(position).getId() + Storage.DOG_IMAGE, new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(mContext).load(uri).placeholder(R.drawable.dog_icon).centerCrop().into(dogsListAdapterViewHolder.mImageView);
                        usersList.get(position).setDogImageURI(uri);
                        usersList.get(position).setHasImage(true);
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        usersList.get(position).setHasImage(false);
                        usersList.get(position).setDogImageURI(Uri.parse(""));
                    }
                });

            } else {
                if (hasImage)
                    Glide.with(mContext).load(dogsImage).placeholder(R.drawable.dog_icon).centerCrop().into(dogsListAdapterViewHolder.mImageView);
                else
                    Glide.with(mContext).clear(dogsListAdapterViewHolder.mImageView);
            }


        }

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void setList(List<NearbyUser> usersList) {
        this.usersList = usersList;
        notifyItemInserted(usersList.size());
    }

}