package com.alekseyM73.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alekseyM73.R;
import com.alekseyM73.model.photo.Item;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import static com.vk.sdk.VKUIHelper.getApplicationContext;

public class IconRenderer extends DefaultClusterRenderer<Item> {

    private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
    private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
    private final ImageView mImageView;
    private final ImageView mClusterImageView;
    private final int mDimension;

    public IconRenderer(Activity context, GoogleMap map, ClusterManager<Item> clusterManager) {
        super(context, map, clusterManager);

        View multiProfile = context.getLayoutInflater().inflate(R.layout.multi_profile, null);
        mClusterIconGenerator.setContentView(multiProfile);
        mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

        mImageView = new ImageView(getApplicationContext());
        mDimension = (int) context.getResources().getDimension(R.dimen.custom_profile_image);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int padding = 2;
        mImageView.setPadding(padding, padding, padding, padding);
        mIconGenerator.setContentView(mImageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(Item item, MarkerOptions markerOptions) {
        mImageView.setImageBitmap(item.getBitmap());
        mImageView.setImageBitmap(item.getBitmap());
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Item> cluster, MarkerOptions markerOptions) {
        List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
        int width = mDimension;
        int height = mDimension;

        for (Item p : cluster.getItems()) {
            if (profilePhotos.size() == 4) break;
            Drawable drawable = new BitmapDrawable(p.getBitmap());
            drawable.setBounds(0, 0, width, height);
            profilePhotos.add(drawable);
        }
        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
        multiDrawable.setBounds(0, 0, width, height);

        mClusterImageView.setImageDrawable(multiDrawable);
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}
