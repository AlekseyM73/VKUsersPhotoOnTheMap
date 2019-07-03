package com.alekseyM73.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alekseyM73.listeners.PhotoListener;
import com.alekseyM73.R;
import com.alekseyM73.model.photo.Item;
import com.squareup.picasso.Picasso;
import java.util.LinkedList;

public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LinkedList<Item> items;
    private PhotoListener listener;

    public PhotosAdapter(LinkedList<Item> items, PhotoListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void addItems(LinkedList<Item> items){
        int start = this.items.size();
        this.items = items;
        int end = items.size()-1;
        notifyItemRangeInserted(start, end);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_photo, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);
        Picasso.get()
                .load(item.getPhotos().get(item.getPhotos().size() - 2).getUrl())
                .fit()
                .centerCrop()
                .into(((Holder)holder).photo);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }


    class Holder extends RecyclerView.ViewHolder {

        ImageView photo;

        Holder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);

            photo.setOnClickListener(v ->
                    listener.onClick(getAdapterPosition()));
        }
    }
}
