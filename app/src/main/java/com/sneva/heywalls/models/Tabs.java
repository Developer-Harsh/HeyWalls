package com.sneva.heywalls.models;

public class Tabs {

    String id;
    int sortID;

    public Tabs() {

    }

    public Tabs(String id, int sortID) {
        this.id = id;
        this.sortID = sortID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSortID() {
        return sortID;
    }

    public void setSortID(int sortID) {
        this.sortID = sortID;
    }
}
