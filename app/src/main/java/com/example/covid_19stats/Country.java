package com.example.covid_19stats;

public class Country {
    String name;
    String code;
    String population;

    public Country(){

    }

    public Country(String name, String population) {
        this.name = name;
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    @Override
    public String toString() {
        return "Country{" +
                "name='" + name + '\'' +
                ", population='" + population + '\'' +
                '}';
    }
}
