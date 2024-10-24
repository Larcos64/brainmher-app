package com.uan.brainmher.domain.entities;

public class MedicationAssignment {

    private String medicamentUID;
    private String medicamentName;
    private String medicamentDescription;
    private String recipeDate;
    private String hours;
    private String frequency;
    private String dose;
    private String statement;
    private String uriImg;

    public MedicationAssignment() {
    }

    public MedicationAssignment(String medicamentUID, String medicamentName, String medicamentDescription, String recipeDate,
                                String hours, String frequency, String dose, String statement, String uriImg) {
        this.medicamentUID = medicamentUID;
        this.medicamentName = medicamentName;
        this.medicamentDescription = medicamentDescription;
        this.recipeDate = recipeDate;
        this.hours = hours;
        this.frequency = frequency;
        this.dose = dose;
        this.statement = statement;
        this.uriImg = uriImg;
    }

    public String getMedicamentUID() {
        return medicamentUID;
    }

    public void setMedicamentUID(String medicamentUID) {
        this.medicamentUID = medicamentUID;
    }

    public String getMedicamentName() {
        return medicamentName;
    }

    public void setMedicamentName(String medicamentName) {
        this.medicamentName = medicamentName;
    }

    public String getMedicamentDescription() {
        return medicamentDescription;
    }

    public void setMedicamentDescription(String medicamentDescription) {
        this.medicamentDescription = medicamentDescription;
    }

    public String getRecipeDate() {
        return recipeDate;
    }

    public void setRecipeDate(String recipeDate) {
        this.recipeDate = recipeDate;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getUriImg() {
        return uriImg;
    }

    public void setUriImg(String uriImg) {
        this.uriImg = uriImg;
    }
}