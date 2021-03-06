package com.alekseyM73.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserResponse implements Serializable {

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("sex")
    @Expose
    private int sex;

    @SerializedName("bdate")
    @Expose
    private String bdate;

    @SerializedName("city")
    @Expose
    private UserCity city;

    @SerializedName("photo_100")
    @Expose
    private String photo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public UserCity getCity() {
        return city;
    }

    public void setCity(UserCity city) {
        this.city = city;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + id +
                ", sex=" + sex +
                ", bdate='" + bdate + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
