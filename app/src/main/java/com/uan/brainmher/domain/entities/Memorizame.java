package com.uan.brainmher.domain.entities;

import android.net.Uri;
import android.widget.ImageView;

public class Memorizame {
    private String patientUID;
    private String question;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String correctAnswer;
    private String uriImg;
    private String uuidGenerated;

    public Memorizame() {
    }



    public Memorizame(String patientUID, String question, String answer1, String answer2,
                      String answer3, String answer4, String correctAnswer, String uriImg, String uuidGenerated) {
        this.patientUID = patientUID;
        this.question = question;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correctAnswer = correctAnswer;
        this.uriImg = uriImg;
        this.uuidGenerated= uuidGenerated;
    }

    //region getter and setter
    public String getPatientUID() {
        return patientUID;
    }

    public void setPatientUID(String patientUID) {
        this.patientUID = patientUID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getUriImg() {
        return uriImg;
    }

    public void setUriImg(String uriImg) {
        this.uriImg = uriImg;
    }

    public String getUuidGenerated() {
        return uuidGenerated;
    }

    public void setUuidGenerated(String uuidGenerated) {
        this.uuidGenerated = uuidGenerated;
    }
    //endregion

    @Override
    public String toString() {
        return "Memorizame{" +
                "patientUID='" + patientUID + '\'' +
                ", question='" + question + '\'' +
                ", answer1='" + answer1 + '\'' +
                ", answer2='" + answer2 + '\'' +
                ", answer3='" + answer3 + '\'' +
                ", answer4='" + answer4 + '\'' +
                ", correctAnswer=" + correctAnswer +
                '}';
    }
}