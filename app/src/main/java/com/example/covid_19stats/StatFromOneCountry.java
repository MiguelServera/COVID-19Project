package com.example.covid_19stats;

public class StatFromOneCountry {
    String date;
    int cases;
    int deaths;
    int cured;

    public StatFromOneCountry(String date, int cases, int deaths, int cured) {
        this.date = date;
        this.cases = cases;
        this.deaths = deaths;
        this.cured = cured;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getCured() {
        return cured;
    }

    public void setCured(int cured) {
        this.cured = cured;
    }

    @Override
    public String toString() {
        return "StatFromOneCountry{" +
                "date='" + date + '\'' +
                ", cases=" + cases +
                ", deaths=" + deaths +
                ", cured=" + cured +
                '}';
    }
}