package net.comet.lazyorder.model.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by comet on 15/8/24.
 */
public class User {

    @SerializedName("id")
    String _id;

    @SerializedName("username")
    String username;

    @SerializedName("mobile")
    String mobile;

    @SerializedName("email")
    String email;

    @SerializedName("avatar_url")
    String avatarUrl;

    @SerializedName("last_address_id")
    long lastAddressId;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public long getLastAddressId() {
        return lastAddressId;
    }

    public void setLastAddressId(long lastAddressId) {
        this.lastAddressId = lastAddressId;
    }


    @Override
    public String toString() {
        return "User{" +
                "userId=" + _id +
                ", username='" + username + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
