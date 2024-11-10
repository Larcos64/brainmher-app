package com.uan.brainmher.domain.entities;

import java.util.List;

public class CognitiveExercises {

    private int idExcercise;
    private String uriImageExcercise;
    private String nameExcercise;
    private String descriptionExcercise;
    private List<String> assignments;

    public CognitiveExercises() {
    }

    public CognitiveExercises(int idExcercise, String uriImageExcercise, String nameExcercise, String descriptionExcercise, List<String> assignments) {
        this.idExcercise = idExcercise;
        this.uriImageExcercise = uriImageExcercise;
        this.nameExcercise = nameExcercise;
        this.descriptionExcercise = descriptionExcercise;
        this.assignments = assignments;
    }

    public int getIdExcercise() {
        return idExcercise;
    }

    public void setIdExcercise(int idExcercise) {
        this.idExcercise = idExcercise;
    }

    public String getUriImageExcercise() {
        return uriImageExcercise;
    }

    public void setUriImageExcercise(String uriImageExcercise) {
        this.uriImageExcercise = uriImageExcercise;
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

    public List<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<String> assignments) {
        this.assignments = assignments;
    }
}