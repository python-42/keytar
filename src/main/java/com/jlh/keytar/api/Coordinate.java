package com.jlh.keytar.api;

public class Coordinate {
    
    private Integer X = null;
    private Integer Y = null;
    private boolean fillX = true;

    public Coordinate (int x, int y) {
        X = x;
        Y = y;
    }

    public Coordinate() {}


    public int getX() {
        return this.X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public int getY() {
        return this.Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public void fill(int val) {
        if(fillX) {
            X = val;
            fillX = false;
        }else {
            Y = val;
            fillX = true;
        }
    }

    /**
     * Get the current fill position tracked by the object
     * @return true if X will be filled next, false otherwise
     */
    public boolean getCurrentFillPosition() {
        return fillX;
    }

    public boolean readyToSend() {
        return X != null && Y != null;
    }

    @Override
    public String toString() {
        return "(" + X + " ," + Y + ")";
    }

}
