package com.example.covid_19stats;

public class Stat {
    Country country;
    String date;
    String cases;
    String deaths;

    public Stat(Country country, String date, String cases, String deaths) {
        this.country = country;
        this.date = date;
        this.cases = cases;
        this.deaths = deaths;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    @Override
    public String toString() {
        return "Stat{" +
                "country=" + country +
                ", date='" + date + '\'' +
                ", cases='" + cases + '\'' +
                ", deaths='" + deaths + '\'' +
                '}';
    }
}
