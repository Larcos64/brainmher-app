package com.uan.brainmher.domain.entities;

import android.net.Uri;

import java.util.Calendar;

public class Notification {

    private String idNotification;
    private int startHour;
    private int startMinute;
    private long startTime;
    private long frequency=1000;
    private long unitOfTime;
    private Uri tone;
    private String titleNotification;
    private String MessageNotification="";
    private Boolean status=false;
    private String patientId;
    private int type;

    public Notification() {
    }

    public Notification(String idNotification, int startHour, int startMinute, long startTime, long frequency, long unitOfTime, Uri tone, String titleNotification, String messageNotification, Boolean status, String patientId, int type) {
        this.idNotification = idNotification;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.startTime = startTime;
        this.frequency = frequency;
        this.unitOfTime = unitOfTime;
        this.tone = tone;
        this.titleNotification = titleNotification;
        MessageNotification = messageNotification;
        this.status = status;
        this.patientId = patientId;
        this.type = type;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(String idNotification) {
        this.idNotification = idNotification;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public long getUnitOfTime() {
        return unitOfTime;
    }

    public void setUnitOfTime(long unitOfTime) {
        this.unitOfTime = unitOfTime;
    }

    public Uri getTone() {
        return tone;
    }

    public void setTone(Uri tone) {
        this.tone = tone;
    }

    public String getTitleNotification() {
        return titleNotification;
    }

    public void setTitleNotification(String titleNotification) {
        this.titleNotification = titleNotification;
    }

    public String getMessageNotification() {
        return MessageNotification;
    }

    public void setMessageNotification(String messageNotification) {
        MessageNotification = messageNotification;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NotificationData{" +
                "idNotification='" + idNotification + '\'' +
                ", startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", startTime=" + startTime +
                ", frequency=" + frequency +
                ", unitOfTime=" + unitOfTime +
                ", tone=" + tone +
                ", titleNotification='" + titleNotification + '\'' +
                ", MessageNotification='" + MessageNotification + '\'' +
                ", status=" + status +
                ", patientId='" + patientId + '\'' +
                ", type=" + type +
                '}';
    }
}