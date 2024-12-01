package com.uan.brainmher.domain.entities;

public class CarerEvent {

    private String eventName,eventLocation, eventDescription;
    private String eventId;
    private String eventDate;
    private String eventStartHour, eventEndHour;
    private long eventStartTime;

    public CarerEvent() {
    }

    public CarerEvent(String eventName, String eventLocation, String eventDescription, String eventId, String eventDate, String eventStartHour, String eventEndHour, long eventStartTime) {
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.eventStartHour = eventStartHour;
        this.eventEndHour = eventEndHour;
        this.eventStartTime = eventStartTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public long getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(long eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventStartHour() {
        return eventStartHour;
    }

    public void setEventStartHour(String eventStartHour) {
        this.eventStartHour = eventStartHour;
    }

    public String getEventEndHour() {
        return eventEndHour;
    }

    public void setEventEndHour(String eventEndHour) {
        this.eventEndHour = eventEndHour;
    }

    @Override
    public String toString() {
        return "CarerEvent{" +
                "eventName='" + eventName + '\'' +
                ", eventLocation='" + eventLocation + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                ", eventId='" + eventId + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventStartHour='" + eventStartHour + '\'' +
                ", eventEndHour='" + eventEndHour + '\'' +
                ", eventStartTime=" + eventStartTime +
                '}';
    }
}