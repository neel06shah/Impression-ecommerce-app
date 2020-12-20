package com.example.impressionselling.rmo;

public class serviceProduct {
    private String date,id,name,product,status,collectDate,completeDate,collectName,receiveName,number;
    serviceProduct(){}

    public serviceProduct(String date, String id, String name, String product, String status, String collectDate, String completeDate, String collectName, String receiveName, String number) {
        this.date = date;
        this.id = id;
        this.name = name;
        this.product = product;
        this.status = status;
        this.collectDate = collectDate;
        this.completeDate = completeDate;
        this.collectName = collectName;
        this.receiveName = receiveName;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCollectDate() {
        return collectDate;
    }

    public void setCollectDate(String collectDate) {
        this.collectDate = collectDate;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    public String getCollectName() {
        return collectName;
    }

    public void setCollectName(String collectName) {
        this.collectName = collectName;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

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

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
