package com.cloud;

import java.io.Serializable;

public class CloudData implements Serializable {
    private String data;
    private boolean shouldUpdata;
    private double score;

    public void setData(String data) {
        this.data = data;
    }

    public void setShouldUpdata(boolean shouldUpdata) {
        this.shouldUpdata = shouldUpdata;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public CloudData(String data, boolean shouldUpdata, double score) {
        this.data = data;
        this.shouldUpdata = shouldUpdata;
        this.score = score;
    }

    public CloudData(){
        this("", false, 0);
    }

    public boolean shouldUpdate() {
        return shouldUpdata;
    }

    public double getScore() {
        return score;
    }
}
