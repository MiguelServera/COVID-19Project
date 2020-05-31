package com.example.covid_19stats.POJO;

import java.util.Comparator;

public class StatPercent {
    String code;
    float percent;
    //This will return the highest percent of all the StatPercent objects.
    public static final Comparator<StatPercent> descendingStats = new Comparator<StatPercent>() {
        @Override
        public int compare(StatPercent o1, StatPercent o2) {
            return Float.compare(o2.percent, o1.percent);
        }
    };

    public StatPercent(String code, float percent) {
        this.code = code;
        this.percent = percent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    @Override
    public String toString() {
        return "StatPercent{" +
                "code='" + code + '\'' +
                ", percent=" + percent +
                '}';
    }
}
