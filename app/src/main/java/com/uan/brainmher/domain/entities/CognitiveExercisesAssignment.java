package com.uan.brainmher.domain.entities;

import java.util.List;

public class CognitiveExercisesAssignment {

    private String uidPatient;
    private String uriImageExcercise;
    private int idExcercise;
    private String nameExcercise;
    private String descriptionExcercise;
    private int level;
    private int bestScore;
    private String statement;
    private int rating;

    public CognitiveExercisesAssignment() {
    }

    public CognitiveExercisesAssignment(String uidPatient, String uriImageExcercise, int idExcercise, String nameExcercise, String descriptionExcercise, int level, int bestScore, String statement, int rating) {
        this.uidPatient = uidPatient;
        this.uriImageExcercise = uriImageExcercise;
        this.idExcercise = idExcercise;
        this.nameExcercise = nameExcercise;
        this.descriptionExcercise = descriptionExcercise;
        this.level = level;
        this.bestScore = bestScore;
        this.statement = statement;
        this.rating = rating;
    }

    public String getUidPatient() {
        return uidPatient;
    }

    public void setUidPatient(String uidPatient) {
        this.uidPatient = uidPatient;
    }

    public String getUriImageExcercise() {
        return uriImageExcercise;
    }

    public void setUriImageExcercise(String uriImageExcercise) {
        this.uriImageExcercise = uriImageExcercise;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}