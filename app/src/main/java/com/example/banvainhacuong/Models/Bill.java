package com.example.banvainhacuong.Models;


public class Bill {
    private String billId;
    private String createDate;
    private Table table;
    private double totalCost;

    public Bill() {
    }

    public Bill(String billId, String createDate, Table table, double totalCost) {
        this.billId = billId;
        this.createDate = createDate;
        this.table = table;
        this.totalCost = totalCost;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
