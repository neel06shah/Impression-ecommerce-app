package com.example.impressionselling;

public class subCategory {
    public String title,image,category;
    public subCategory(){}


    public subCategory(String title, String image, String category) {
        this.title = title;
        this.image = image;
        this.category = category;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}