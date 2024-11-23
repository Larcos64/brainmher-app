package com.uan.brainmher.domain.entities;

public class CarerInformation {

    private String title;
    private String description;
    private String type;
    private String link;
    private int order;

    public CarerInformation() {
    }

    public CarerInformation(String title, String description, String type) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.link = link;
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }
    public String getLink() {
        return link;
    }

    public void setType(String type) {
        this.type = type;
    }
}