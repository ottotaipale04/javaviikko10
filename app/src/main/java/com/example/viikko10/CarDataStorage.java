package com.example.viikko10;

import java.util.ArrayList;

public class CarDataStorage {

    String city;
    int year;
    ArrayList<CarData> carData = new ArrayList<>();

    private static CarDataStorage instance = null;
    static public CarDataStorage getInstance() {
        if (instance == null) {
            instance = new CarDataStorage();
        }
        return instance;
    }

    public ArrayList<CarData> getCarData() {
        return carData;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void clearData() {

    }

    public String getCity() {
        return city;
    }

    public int getYear() {
        return year;
    }

    public void addCarData(CarData data) {
        carData.add(data);
    }

}
