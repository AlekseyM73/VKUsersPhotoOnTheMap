package com.alekseyM73.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class IconRenderer extends DefaultClusterRenderer<MapItem> {

    public IconRenderer(Context context, GoogleMap map, ClusterManager<MapItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(MapItem item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getMarker().getIcon());
        markerOptions.snippet(item.getSnippet());
        markerOptions.title(item.getTitle());
    }
}
