package com.example.banvainhacuong.Models;

public class Cart {
    private Product product;
    private int amount;

    public Cart() {
    }

    public Cart(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double toCast() {
        return product.getProductPrice() * amount;
    }

}

