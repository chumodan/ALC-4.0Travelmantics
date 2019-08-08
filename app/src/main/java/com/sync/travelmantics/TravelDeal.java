package com.sync.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable {
    private String id;
    private String title;
    private String price;
    private String description;
    private String imgURL;
    private String imageName;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public TravelDeal(){

    }

    public TravelDeal( String title, String price, String description, String imgURL, String imageName) {
        this.setId(id);
        this.setTitle(title);
        this.setPrice(price);
        this.setDescription(description);
        this.setImgURL(imgURL);
        this.setImageName(imageName);
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}