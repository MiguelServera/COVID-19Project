package com.example.covid_19stats;

public class CCAAStats {
    String code;
    String date;
    int cases;
    int pcr;
    int testAC;
    int hospitalized;
    int uci;
    int deaths;

    public CCAAStats(){}

    public CCAAStats(String code, String date, int cases, int pcr, int testAC, int hospitalized, int uci, int deaths) {
        this.code = code;
        this.date = date;
        this.cases = cases;
        this.pcr = pcr;
        this.testAC = testAC;
        this.hospitalized = hospitalized;
        this.uci = uci;
        this.deaths = deaths;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public int getPcr() {
        return pcr;
    }

    public void setPcr(int pcr) {
        this.pcr = pcr;
    }

    public int getTestAC() {
        return testAC;
    }

    public void setTestAC(int testAC) {
        this.testAC = testAC;
    }

    public int getHospitalized() {
        return hospitalized;
    }

    public void setHospitalized(int hospitalized) {
        this.hospitalized = hospitalized;
    }

    public int getUci() {
        return uci;
    }

    public void setUci(int uci) {
        this.uci = uci;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    @Override
    public String toString() {
        return "CCAAStats{" +
                "code='" + code + '\'' +
                ", date='" + date + '\'' +
                ", cases=" + cases +
                ", pcr=" + pcr +
                ", testAC=" + testAC +
                ", hospitalized=" + hospitalized +
                ", uci=" + uci +
                ", deaths=" + deaths +
                '}';
    }
}