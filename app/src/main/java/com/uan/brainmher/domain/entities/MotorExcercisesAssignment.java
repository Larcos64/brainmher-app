package com.uan.brainmher.domain.entities;

public class MotorExcercisesAssignment {

    private String uidPatient;
    private String uriGifExercise;
    private int idExercise;
    private String nameExercise;
    private String descriptionExercise;
    private String longDescriptionExercise;
    private int timeExercise;
    private String finished;
    private int rating;

    public MotorExcercisesAssignment() {
    }

    public MotorExcercisesAssignment(String uidPatient, String uriGifExercise, int idExercise, String nameExercise, String descriptionExercise, String longDescriptionExercise, Integer timeExercise, String finished, Integer rating) {
        this.uidPatient = uidPatient;
        this.uriGifExercise = uriGifExercise;
        this.idExercise = idExercise;
        this.nameExercise = nameExercise;
        this.descriptionExercise = descriptionExercise;
        this.longDescriptionExercise = longDescriptionExercise;
        this.timeExercise = timeExercise;
        this.finished = finished;
        this.rating = rating;
    }

    public String getUidPatient() {
        return uidPatient;
    }

    public void setUidPatient(String uidPatient) {
        this.uidPatient = uidPatient;
    }

    public String getUriGifExercise() {
        return uriGifExercise;
    }

    public void setUriGifExercise(String uriGifExercise) {
        this.uriGifExercise = uriGifExercise;
    }

    public int getIdExercise() {
        return idExercise;
    }

    public void setIdExercise(int idExercise) {
        this.idExercise = idExercise;
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