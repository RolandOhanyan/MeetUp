package com.example.meetup;

public class Event {
    private String id;
    private String title;
    private String location;
    private String dateTime;
    private boolean isRecurring;
    private String creatorId;

    // Конструктор по умолчанию (обязателен для Firestore)
    public Event() {}

    public Event(String title, String location, String dateTime, boolean isRecurring, String creatorId) {
        this.title = title;
        this.location = location;
        this.dateTime = dateTime;
        this.isRecurring = isRecurring;
        this.creatorId = creatorId;
    }

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}