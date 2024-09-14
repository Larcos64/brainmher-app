package com.uan.brainmher.domain.entities;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Patient implements Serializable {
    //region Variables
    private String patientUID;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthday;
    private String password;
    private String confirmPassword;
    private String email;
    private long familyPhoneNumber;
    private String dateDiagnostic;
    private String diagnostic;
    private String emergencyNumber;
    private String role;
    List<String> assigns;
    private String uriImg;
    private String playerId;

    //endregion

    //region Builders
    public Patient() {
    }

    public Patient(String patientUID, String firstName, String lastName, String gender, String birthday,
                   String password, String confirmPassword, String email, long familyPhoneNumber, String dateDiagnostic,
                   String diagnostic, String emergencyNumber, String role, List<String> assigns, String uriImg) {
        this.patientUID = patientUID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
        this.familyPhoneNumber = familyPhoneNumber;
        this.dateDiagnostic = dateDiagnostic;
        this.diagnostic = diagnostic;
        this.emergencyNumber = emergencyNumber;
        this.role = role;
        this.assigns = assigns;
        this.uriImg = uriImg;
    }
    //endregion

    //region Getters and Setters
    public String getPatientUID() {
        return patientUID;
    }

    public void setPatientUID(String patientUID) {
        this.patientUID = patientUID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getFamilyPhoneNumber() {
        return familyPhoneNumber;
    }

    public void setFamilyPhoneNumber(long familyPhoneNumber) { this.familyPhoneNumber = familyPhoneNumber;}

    public String getDateDiagnostic() {
        return dateDiagnostic;
    }

    public void setDateDiagnostic(String dateDiagnostic) {
        this.dateDiagnostic = dateDiagnostic;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getEmergencyNumber() {
        return emergencyNumber;
    }

    public void setEmergencyNumber(String emergencyNumber) { this.emergencyNumber = emergencyNumber; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role;}

    public List<String> getAssigns() {
        return assigns;
    }

    public void setAssigns(List<String> assigns) {
        this.assigns = assigns;
    }

    public String getUriImg() {
        return uriImg;
    }

    public void setUriImg(String uriImg) {
        this.uriImg = uriImg;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    //endregion

    //region toString
    @Override
    public String toString() {
        return "Patient{" +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", familyPhoneNumber=" + familyPhoneNumber +
                ", dateDiagnostic='" + dateDiagnostic + '\'' +
                ", diagnostic='" + diagnostic + '\'' +
                '}';
    }
    //endregion
}