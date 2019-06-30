package com.alekseyM73.model.photo;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Item implements ClusterItem{

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("album_id")
    @Expose
    private Long albumId;

    @SerializedName("owner_id")
    @Expose
    private Long ownerId;

    @SerializedName("sizes")
    @Expose
    private List<Photo> photos;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("date")
    @Expose
    private Long date;

    @SerializedName("lat")
    @Expose
    private Double lat;

    @SerializedName("long")
    @Expose
    private Double lon;

    @SerializedName("post_id")
    @Expose
    private Long postId;

    @SerializedName("user_id")
    @Expose
    private Long userId;

    private transient Bitmap bitmap;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLong() {
        return lon;
    }

    public void setLong(Double _long) {
        this.lon = _long;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return getId().equals(item.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", albumId=" + albumId +
                ", ownerId=" + ownerId +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", lat=" + lat +
                ", lon=" + lon +
                ", postId=" + postId +
                ", userId=" + userId +
                '}';
    }



    @Override
    public LatLng getPosition() {
        return new LatLng(lat, lon);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return text;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
