package net.comet.lazyorder.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by comet on 15/11/1.
 */
public class Business implements Parcelable {

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    @SerializedName("phone")
    String phone;

    @SerializedName("address")
    String address;

    @SerializedName("pic_url")
    String picUrl;

    @SerializedName("shipping_fee")
    double shippingFee;

    @SerializedName("min_price")
    double minPrice;

    @SerializedName("shipping_time")
    int shippingTime;

    @SerializedName("month_sales")
    int monthSales;

    @SerializedName("bulletin")
    String bulletin;

    @SerializedName("created_at")
    long createdAt;

    transient List<ProductCategory> productCategories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public int getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(int shippingTime) {
        this.shippingTime = shippingTime;
    }

    public int getMonthSales() {
        return monthSales;
    }

    public void setMonthSales(int monthSales) {
        this.monthSales = monthSales;
    }

    public String getBulletin() {
        return bulletin;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProductCategory> getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(List<ProductCategory> productCategories) {
        this.productCategories = productCategories;
    }

    @Override
    public String toString() {
        return "Business{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", shippingFee=" + shippingFee +
                ", minPrice=" + minPrice +
                ", shippingTime=" + shippingTime +
                ", bulletin='" + bulletin + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.address);
        dest.writeString(this.picUrl);
        dest.writeDouble(this.shippingFee);
        dest.writeDouble(this.minPrice);
        dest.writeInt(this.shippingTime);
        dest.writeInt(this.monthSales);
        dest.writeString(this.bulletin);
        dest.writeLong(this.createdAt);
    }

    public Business() {
    }

    protected Business(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.picUrl = in.readString();
        this.shippingFee = in.readDouble();
        this.minPrice = in.readDouble();
        this.shippingTime = in.readInt();
        this.monthSales = in.readInt();
        this.bulletin = in.readString();
        this.createdAt = in.readLong();
    }

    public static final Parcelable.Creator<Business> CREATOR = new Parcelable.Creator<Business>() {
        @Override
        public Business createFromParcel(Parcel source) {
            return new Business(source);
        }

        @Override
        public Business[] newArray(int size) {
            return new Business[size];
        }
    };
}
