package com.example.banvainhacuong.Models;

public class Table {
    private String tableId;
    private String tableName;

    public Table(String tableId, String tableName) {
        this.tableId = tableId;
        this.tableName = tableName;
    }

    public Table() {
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
