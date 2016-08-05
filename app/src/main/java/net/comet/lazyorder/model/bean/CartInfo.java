package net.comet.lazyorder.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * author：cheikh on 16/5/11 14:14
 * email：wanghonghi@126.com
 */
public class CartInfo {

    public class ShoppingProduct {

        @SerializedName("name")
        String name;

        @SerializedName("quantity")
        int quantity;

        @SerializedName("unit_price")
        double unitPrice;

        @SerializedName("total_price")
        double totalPrice;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(double unitPrice) {
            this.unitPrice = unitPrice;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(double totalPrice) {
            this.totalPrice = totalPrice;
        }
    }

    public class DiscountInfo {

        @SerializedName("name")
        String name;

        @SerializedName("price")
        double price;

        @SerializedName("description")
        String description;

        @SerializedName("icon_name")
        String iconName;

        @SerializedName("icon_color")
        String iconColor;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIconName() {
            return iconName;
        }

        public void setIconName(String iconName) {
            this.iconName = iconName;
        }

        public String getIconColor() {
            return iconColor;
        }

        public void setIconColor(String iconColor) {
            this.iconColor = iconColor;
        }
    }

    public class ExtraFee {

        @SerializedName("name")
        String name;

        @SerializedName("price")
        double price;

        @SerializedName("description")
        String description;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @SerializedName("id")
    String id;

    @SerializedName("origin_price")
    double originPrice;

    @SerializedName("discount_price")
    double discountPrice;

    @SerializedName("total_price")
    double totalPrice;

    @SerializedName("business_info")
    Business business;

    @SerializedName("product_list")
    List<ShoppingProduct> shoppingProducts;

    @SerializedName("extra_fee_list")
    List<ExtraFee> extraFees;

    @SerializedName("discount_list")
    List<DiscountInfo> discountInfos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(double originPrice) {
        this.originPrice = originPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public List<ShoppingProduct> getShoppingProducts() {
        return shoppingProducts;
    }

    public void setShoppingProducts(List<ShoppingProduct> shoppingProducts) {
        this.shoppingProducts = shoppingProducts;
    }

    public List<ExtraFee> getExtraFees() {
        return extraFees;
    }

    public void setExtraFees(List<ExtraFee> extraFees) {
        this.extraFees = extraFees;
    }

    public List<DiscountInfo> getDiscountInfos() {
        return discountInfos;
    }

    public void setDiscountInfos(List<DiscountInfo> discountInfos) {
        this.discountInfos = discountInfos;
    }
}
