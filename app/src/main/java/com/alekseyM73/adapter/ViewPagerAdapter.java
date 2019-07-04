package com.alekseyM73.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.alekseyM73.R;
import com.alekseyM73.listeners.ViewPagerListener;
import com.alekseyM73.model.photo.Item;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPagerAdapter extends PagerAdapter {

    private List<Item> items;
    private ViewPagerListener listener;

    public ViewPagerAdapter(List<Item> mapItems, Context context, ViewPagerListener listener) {
        this.items = mapItems;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Item item = items.get(position);
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.photo_adapter, container, false);
        TextView tvFullName = itemView.findViewById(R.id.full_name);
        TextView bDate = itemView.findViewById(R.id.bday);
        TextView tvCity = itemView.findViewById(R.id.city);
        Button actionToPhoto = itemView.findViewById(R.id.actionToPhoto);
        View actionToPage = itemView.findViewById(R.id.actionToPage);
        CircleImageView userPhoto = itemView.findViewById(R.id.user_photo);
        ImageView photo = itemView.findViewById(R.id.iv_photo);

        StringBuilder stringBuilder = new StringBuilder();
        if (item.getUser().getFirstName() != null){
            stringBuilder.append(item.getUser().getFirstName());
        }
        if (item.getUser().getLastName() != null){
            stringBuilder.append(" ").append(item.getUser().getLastName());
        }
        tvFullName.setText(stringBuilder.toString());

        if (item.getUser().getCity() != null){
            tvCity.setText(item.getUser().getCity().getTitle());
        }

        Picasso.get().load(item.getUser().getPhoto()).into(userPhoto);
        Picasso.get().load(item.getPhotos().get(item.getPhotos().size()-1).getUrl()).into(photo);

        actionToPage.setOnClickListener(v -> {
            listener.showPage(item);
        });
        actionToPhoto.setOnClickListener(v -> {
            listener.showPhoto(item);
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }
}
