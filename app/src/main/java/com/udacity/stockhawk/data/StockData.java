package com.udacity.stockhawk.data;


public class StockData {
    private final float time;
    private final float value;

    public StockData(float time, float value) {
        this.time = time;
        this.value = value;
    }


    public float getTime() {
        return time;
    }

    public float getValue() {
        return value;
    }
}
