package com.uan.brainmher.domain.entities;

import java.util.List;

public class MotorExercises {

    private int idExercise;
    private String uriGifExercise;
    private String nameExercise;
    private String descriptionExercise;
    private String longDescriptionExercise;
    private int timeExercise;
    private List<String> assignments;

    public MotorExercises() {
    }

    public MotorExercises(int idExercise, String uriGifExercise, String nameExercise, String descriptionExercise, String longDescriptionExercise, int timeExercise, List<String> assignments) {
        this.idExercise = idExercise;
        this.uriGifExercise = uriGifExercise;
        this.nameExercise = nameExercise;
        this.descriptionExercise = descriptionExercise;
        this.longDescriptionExercise = longDescriptionExercise;
        this.timeExercise = timeExercise;
        this.assignments = assignments;
    }

    public int getIdExercise() {
        return idExercise;
    }

    public void setIdExercise(int idExercise) {
        this.idExercise = idExercise;
    }

    public String getUriGifExercise() {
        return uriGifExercise;
    }

    public void setUriGifExercise(String uriGifExercise) {
        this.uriGifExercise = uriGifExercise;
    }

    public String getNameExercise() {
        return nameExercise;
    }

    public void setNameExercise(String nameExercise) {
        this.nameExercise = nameExercise;
    }

    public String getDescriptionExercise() {
        return descriptionExercise;
    }

    public void setDescriptionExercise(String descriptionExercise) {
        this.descriptionExercise = descriptionExercise;
    }

    public String getLongDescriptionExercise() {
        return longDescriptionExercise;
    }

    public void setLongDescriptionExercise(String longDescriptionExercise) {
        this.longDescriptionExercise = longDescriptionExercise;
    }

    public int getTimeExercise() {
        return timeExercise;
    }

    public void setTimeExercise(int timeExercise) {
        this.timeExercise = timeExercise;
    }

    public List<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<String> assignments) {
        this.assignments = assignments;
    }
}
