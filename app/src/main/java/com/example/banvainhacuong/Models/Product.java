package com.example.banvainhacuong.Models;
public class Product{
    private String id;
    private String productImage;
    private String productName;
    private int productPrice;
    private int inventory;
    private String type;

    public Product(String id, String productImage, String productName, int productPrice, int inventory, String type) {
        this.id = id;
        this.productImage = productImage;
        this.productName = productName;
        this.productPrice = productPrice;
        this.inventory = inventory;
        this.type = type;
    }

    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
