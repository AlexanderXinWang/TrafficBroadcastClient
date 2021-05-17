package com.iflytek.vivian.traffic.android.dto;


import java.util.List;

public class Search {
    private List<User> users;
    private List<Event> events;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
