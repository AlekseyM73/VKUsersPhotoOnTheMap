package com.alekseyM73.model.user;

import com.alekseyM73.model.photo.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserInfoResponse {

    @SerializedName("response")
    @Expose
    private List<UserResponse> response = null;

    public List<UserResponse> getResponse() {
        return response;
    }

    public void setResponse(List<UserResponse> response) {
        this.response = response;
    }
}
