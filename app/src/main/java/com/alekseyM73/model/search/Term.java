package com.alekseyM73.model.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class Term {

    @SerializedName("offset")
    @Expose
    private long offset;
    @SerializedName("value")
    @Expose
    private String value;

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
