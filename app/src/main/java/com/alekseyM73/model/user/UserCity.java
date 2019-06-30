package com.alekseyM73.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserCity {

    @Expose
    @SerializedName("id")
    private long id;

    @Expose
    @SerializedName("title")
    private String title;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
