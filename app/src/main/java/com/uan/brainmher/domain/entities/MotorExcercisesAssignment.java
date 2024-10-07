package com.uan.brainmher.domain.entities;

public class MotorExcercisesAssignment {

    private String uidPatient;
    private String uriGifExcercise;
    private int idExcercise;
    private String nameExcercise;
    private String descriptionExcercise;
    private String longDescriptionExcercise;
    private int timeExcercise;
    private String finished;
    private int rating;

    public MotorExcercisesAssignment() {
    }

    public MotorExcercisesAssignment(String uidPatient, String uriGifExcercise, int idExcercise, String nameExcercise, String descriptionExcercise, String longDescriptionExcercise, Integer timeExcercise, String finished, Integer rating) {
        this.uidPatient = uidPatient;
        this.uriGifExcercise = uriGifExcercise;
        this.idExcercise = idExcercise;
        this.nameExcercise = nameExcercise;
        this.descriptionExcercise = descriptionExcercise;
        this.longDescriptionExcercise = longDescriptionExcercise;
        this.timeExcercise = timeExcercise;
        this.finished = finished;
        this.rating = rating;
    }

    public String getUidPatient() {
        return uidPatient;
    }

    public void setUidPatient(String uidPatient) {
        this.uidPatient = uidPatient;
    }

    public String getUriGifExcercise() {
        return uriGifExcercise;
    }

    public void setUriGifExcercise(String uriGifExcercise) {
        this.uriGifExcercise = uriGifExcercise;
    }

    public int getIdExcercise() {
        return idExcercise;
    }

    public void setIdExcercise(int idExcercise) {
        this.idExcercise = idExcercise;
    }

    public String getNameExcercise() {
        return nameExcercise;
    }

    public void setNameExcercise(String nameExcercise) {
        this.nameExcercise = nameExcercise;
    }

    public String getDescriptionExcercise() {
        return descriptionExcercise;
    }

    public void setDescriptionExcercise(String descriptionExcercise) {
        this.descriptionExcercise = descriptionExcercise;
    }

    public String getLongDescriptionExcercise() {
        return longDescriptionExcercise;
    }

    public void setLongDescriptionExcercise(String longDescriptionExcercise) {
        this.longDescriptionExcercise = longDescriptionExcercise;
    }

    public int getTimeExcercise() {
        return timeExcercise;
    }

    public void setTimeExcercise(int timeExcercise) {
        this.timeExcercise = timeExcercise;
    }

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}