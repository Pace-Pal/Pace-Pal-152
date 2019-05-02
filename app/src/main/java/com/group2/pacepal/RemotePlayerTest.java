package com.group2.pacepal;

class RemotePlayerTest {


    private double distance = 0.0;
    private int place = 0;

    public double getDistance() {return this.distance; }
    public int getPlace(){ return this.place;}
    public void setPlace(int x) { this.place = x ;}
    public void setDistance(double x) {this.distance += x; }

    RemotePlayerTest() {
        place = 0;
        distance = 0.0;
    }




};