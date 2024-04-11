package com.example.banvainhacuong.Models;

public class ProductBill {
    private Bill bill;
    private Product product;
    private int amount;

    public ProductBill() {
    }

    public ProductBill(Bill bill, Product product, int amount) {
        this.bill = bill;
        this.product = product;
        this.amount = amount;
    }

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
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
