package com.alekseyM73.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.R;
import android.widget.TextView;

import com.alekseyM73.model.search.Prediction;

import java.util.List;

public class PlaceAutoCompleteAdapter extends ArrayAdapter<Prediction> {

    public PlaceAutoCompleteAdapter(@NonNull Context context, int resource, @NonNull List<Prediction> objects) {
        super(context, resource, objects);
    }



    //А все эти три метода обязательно оверрайдить если они только вызывают супер ?
    @Nullable
    @Override
    public Prediction getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(@Nullable Prediction item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

        @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.simple_dropdown_item_1line, parent, false);
        } else{
            view = convertView;
        }
        Prediction prediction = getItem(position);
        if (prediction != null) {
            ((TextView)view.findViewById(R.id.text1)).setText(prediction.getDescription());
        }

        return view;
    }
}
