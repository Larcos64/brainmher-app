package com.uan.brainmher.domain.entities;

public class Carer {

    //region Variables
    private String carerUID;
    private String firstName;
    private String lastName;
    private String residenceCountry;
    private String gender;
    private String birthday;
    private long phoneNumber;
    private String email;
    private String userName;
    private String password;
    private String profession;
    private String role;
    private String uriImg;
    private String playerId;
    //endregion

    //region Builders
    public Carer() {
    }

    public Carer(String carerUID, String firstName, String lastName, String residenceCountry,
                 String profession, String gender, String birthday, long phoneNumber,
                 String password, String email, String role, String uriImg) {
        this.carerUID = carerUID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.residenceCountry = residenceCountry;
        this.profession = profession;
        this.gender = gender;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
        this.role = role;
        this.uriImg = uriImg;
    }
    //endregion

    //region Getters and Setters
    public String getCarerUId() {
        return carerUID;
    }

    public void setCarerUId(String patientId) {
        this.carerUID = patientId;
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

    public String getResidenceCountry() {
        return residenceCountry;
    }

    public void setResidenceCountry(String residenceCountry) {
        this.residenceCountry = residenceCountry;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
        return "Carer{" +
                "carerId='" + carerUID + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", residenceCountry='" + residenceCountry + '\'' +
                ", profession='" + profession + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
    //endregion
}