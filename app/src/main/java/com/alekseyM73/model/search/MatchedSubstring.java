package com.alekseyM73.model.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatchedSubstring {

    @SerializedName("length")
    @Expose
    private long length;
    @SerializedName("offset")
    @Expose
    private long offset;

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
