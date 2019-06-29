package com.alekseyM73.model.place;

import com.google.android.libraries.places.api.internal.impl.net.pablo.PlaceResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("location")
    @Expose
    private PlaceLocation location;

    @SerializedName("viewport")
    @Expose
    private PlaceResult.Geometry.Viewport viewport;

    public PlaceLocation getLocation() {
        return location;
    }

    public void setLocation(PlaceLocation location) {
        this.location = location;
    }

    public PlaceResult.Geometry.Viewport getViewport() {
        return viewport;
    }

    public void setViewport(PlaceResult.Geometry.Viewport viewport) {
        this.viewport = viewport;
    }

}
