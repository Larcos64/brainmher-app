package com.uan.brainmher.domain.entities;

public class StretchingExercise {

    String nameExercise;
    String descriptionExercise;
    String uriGif;

    public StretchingExercise() {
    }

    public StretchingExercise(String nameExercise, String descriptionExercise, String uriGif) {
        this.nameExercise = nameExercise;
        this.descriptionExercise = descriptionExercise;
        this.uriGif = uriGif;
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

    public String getUriGif() {
        return uriGif;
    }

    public void setUriGif(String uriGif) {
        this.uriGif = uriGif;
    }
}
