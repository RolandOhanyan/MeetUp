package com.example.meetup;

public class Event {
    private String eventName;
    private String eventDate;
    private String eventTime;
    private double latitude;
    private double longitude;
    private String imageUrl; // Добавляем поле для фото

    // Пустой конструктор для Firestore
    public Event() {
    }

    // Основной конструктор
    public Event(String eventName, String eventDate, String eventTime, double latitude, double longitude, String imageUrl) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl; // Теперь сохраняем фото
    }

    // Геттеры
    public String getEventName() { return eventName; }
    public String getEventDate() { return eventDate; }
    public String getEventTime() { return eventTime; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getImageUrl() { return imageUrl; } // Добавлен геттер

    // Сеттеры
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; } // Добавлен сеттер
}
