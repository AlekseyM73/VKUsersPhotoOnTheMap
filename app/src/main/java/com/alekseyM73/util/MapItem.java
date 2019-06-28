package com.alekseyM73.util;

import com.alekseyM73.model.photo.Item;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

public class MapItem implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private MarkerOptions marker;
    private Item item;

    public MapItem(Item item) {
        mPosition = new LatLng(item.getLat(), item.getLong());
        this.item = item;
    }

    public MapItem(Item item, String title, String snippet) {
        mPosition = new LatLng(item.getLat(), item.getLong());
        this.item = item;
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public MarkerOptions getMarker() {
        return marker;
    }

    public void setMarker(MarkerOptions marker) {
        this.marker = marker;
    }

    public Item getItem() {
        return item;
    }
}