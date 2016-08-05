package net.comet.lazyorder.model.bean;

/**
 * author：cheikh on 16/5/17 14:35
 * email：wanghonghi@126.com
 */
public class ShoppingEntity {
    private String id;
    private String categoryId;
    private String name;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private Product product;

    public static ShoppingEntity initWithProduct(Product product) {
        ShoppingEntity entity = new ShoppingEntity();
        entity.setId(product.getId());
        entity.setCategoryId(product.getCategoryId());
        entity.setName(product.getName());
        entity.setUnitPrice(product.getPrice());
        entity.setQuantity(1);
        entity.setProduct(product);

        return entity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

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
        this.totalPrice = this.quantity * this.unitPrice;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
