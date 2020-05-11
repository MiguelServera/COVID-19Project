package com.example.covid_19stats;

public class Stat {
    String code;
    String name;
    String cases;
    String deaths;

    public Stat(){}

    public Stat(String codeC, String nameC, String cases, String deaths) {
        this.code = codeC;
        this.name = nameC;
        this.cases = cases;
        this.deaths = deaths;
    }

    public String getCodeC() {
        return code;
    }

    public void setCodeC(String codeC) {
        this.code = codeC;
    }

    public String getNameC() {
        return name;
    }

    public void setNameC(String nameC) {
        this.name = nameC;
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
                "codeC='" + code + '\'' +
                ", nameC='" + name + '\'' +
                ", cases='" + cases + '\'' +
                ", deaths='" + deaths + '\'' +
                '}';
    }
}