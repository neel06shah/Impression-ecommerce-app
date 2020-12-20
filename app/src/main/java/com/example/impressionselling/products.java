package com.example.impressionselling;

public class products {
    private String name, description, quantity, image, mrp, rate, discount,category,activity_category,company,activity_company,scheme;
    private Boolean activity,bulk;

    products(){}


    public products(String name, String description, String quantity, String image, String mrp, String rate, String discount, String category, String activity_category, String company, String activity_company, String scheme, Boolean activity, Boolean bulk) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.image = image;
        this.mrp = mrp;
        this.rate = rate;
        this.discount = discount;
        this.category = category;
        this.activity_category = activity_category;
        this.company = company;
        this.activity_company = activity_company;
        this.scheme = scheme;
        this.activity = activity;
        this.bulk = bulk;
    }

    public Boolean getBulk() {
        return bulk;
    }

    public void setBulk(Boolean bulk) {
        this.bulk = bulk;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getActivity_company() {
        return activity_company;
    }

    public void setActivity_company(String activity_company) {
        this.activity_company = activity_company;
    }

    public String getActivity_category() {
        return activity_category;
    }

    public void setActivity_category(String activity_category) {
        this.activity_category = activity_category;
    }

    public Boolean getActivity() {
        return activity;
    }

    public void setActivity(Boolean activity) {
        this.activity = activity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String toString()
    {
        return "Name :"+name+"\nDesc :"+description+"\nQuantity :"+quantity+"\nMRP :"+mrp+"\nRate :"+rate+"\nDiscount :"+ discount;
    }
}
