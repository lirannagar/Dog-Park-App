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
import com.app.dogsapp.objects.NearbyUser;
import com.app.dogsapp.objects.Park;
import com.app.dogsapp.tools.Storage;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class ParksListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Park> parksList=new ArrayList<>();
    private ContextWrapper cw;



    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final ParksListAdapterOnClickHandler mClickHandler;
    private Context mContext;

    public ParksListAdapter(ParksListAdapterOnClickHandler mClickHandler, Context mContext) {
        this.mClickHandler = mClickHandler;
        this.mContext = mContext;
        cw= new ContextWrapper(mContext);

    }

    /**
     * The interface that receives onClick messages.
     */
    public interface ParksListAdapterOnClickHandler {
        void onClick(Park park);
    }

    /**
     * Cache of the children views for a category list item.
     */
    public class ParksListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTextView;


        public ParksListAdapterViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.item_park_name);
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

            mClickHandler.onClick(parksList.get(adapterPosition));
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


        int layoutIdForListItem = R.layout.park_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ParksListAdapterViewHolder(view);



    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position.
     * We check if the holder is a AdapterViewHolder
     * and we set up the views
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ParksListAdapterViewHolder) {
            final ParksListAdapterViewHolder ParksListAdapterViewHolder = (ParksListAdapterViewHolder) holder;


            //Get the park data
            String parkName = parksList.get(position).getParkName();
            ParksListAdapterViewHolder.mTextView.setText(parkName);

        }

    }

    @Override
    public int getItemCount() {
        return parksList.size();
    }

    public void setList(List<Park> parksList){
        this.parksList=parksList;
        notifyItemInserted(parksList.size());
    }

}