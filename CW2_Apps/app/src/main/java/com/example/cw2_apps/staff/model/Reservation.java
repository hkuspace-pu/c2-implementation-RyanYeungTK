package com.example.cw2_apps.staff.model;

public class Reservation {
    public long id;
    public String datetime;
    public String party;
    public String status;

    public Reservation(long id, String datetime, String party, String status) {
        this.id = id; this.datetime = datetime; this.party = party; this.status = status;
    }
}