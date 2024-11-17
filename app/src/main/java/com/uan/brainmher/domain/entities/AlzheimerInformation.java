package com.uan.brainmher.domain.entities;

public class AlzheimerInformation {

    private String title;
    private String description;
    private String type;
    private String link;
    private int order; // Nuevo campo para ordenamiento

    public AlzheimerInformation() {
    }

    public AlzheimerInformation(String title, String description, String type, String link, int order) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.link = link;
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getLink() {
        return link;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}