package com.example.covid_19stats;

public class StatFromOneCountry {
    String date;
    String cases;
    String deaths;
    String cured;

    public StatFromOneCountry() {
    }

    public StatFromOneCountry(String date, String cases, String deaths, String cured) {
        this.date = date;
        this.cases = cases;
        this.deaths = deaths;
        this.cured = cured;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getCases() { return cases; }

    public void setCases(String cases) { this.cases = cases; }

    public String getDeaths() { return deaths; }

    public void setDeaths(String deaths) { this.deaths = deaths; }

    public String getCured() { return cured; }

    public void setCured(String cured) { this.cured = cured; }

    @Override
    public String toString() {
        return "StatFromOneCountry{" +
                "date='" + date + '\'' +
                ", cases='" + cases + '\'' +
                ", deaths='" + deaths + '\'' +
                ", cured='" + cured + '\'' +
                '}';
    }
}