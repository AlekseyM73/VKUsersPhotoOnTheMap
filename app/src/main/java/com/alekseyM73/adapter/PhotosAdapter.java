package com.alekseyM73.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// При коммите например из IDEA (Android Studio) есть волшебные галочки, прочитайте про них
// Там есть такая волшебная галка которая убирает неиспользуемые импорты
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alekseyM73.PhotoListener;
import com.alekseyM73.R;
import com.alekseyM73.model.photo.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


//если ViewHolder один - то можно сразу прописать его чтоб лишний раз не кастовать
public class PhotosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LinkedList<Item> items;
    private PhotoListener listener;

    public PhotosAdapter(LinkedList<Item> items, PhotoListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void addItems(LinkedList<Item> items){
//        Что мешало выше по использованию сконвертить в обычный лист и избежать этой возни ?
//        List<Item> beautifulSexyList = new ArrayList<>(items);
        int start = this.items.size();
        this.items = items;
        int end = items.size()-1;
        notifyItemRangeInserted(start, end);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//      Строка выходит за границы, это плохой тон, лучше перенести вот так
//        View view = LayoutInflater.from(parent.getContext())
//                                  .inflate(R.layout.list_item_photo, parent, false);

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


    //Если при определении items его сразу инициализировать - можно было бы избежать "элвиса" :)
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
                    listener.onClick(items.get(getAdapterPosition())));
        }
    }
}
