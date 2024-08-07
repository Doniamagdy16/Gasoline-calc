package com.example.gasolinecal;

public class Station {
    private String name;
    private double distance;

    public Station(String name, double distance) {
        this.name = name;
        this.distance = distance;
    }

    public Station() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
