package com.uan.brainmher.domain.entities;

public class AlzheimerInformation {

    private String title;
    private String description;
    private String type;
    private String link;
    public AlzheimerInformation() {
    }

    public AlzheimerInformation(String title, String description, String type, String link) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.link = link;
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
}