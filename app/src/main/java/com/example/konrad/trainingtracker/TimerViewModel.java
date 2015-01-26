package com.example.konrad.trainingtracker;

/**
 * Created by Comarch on 2015-01-26.
 */
public class TimerViewModel {
    private int hours;
    private int minutes;
    private int seconds;
    private int miliseconds;

    public TimerViewModel(int h, int m, int s, int ms){
        hours = h;
        minutes = m;
        seconds = s;
        miliseconds = ms;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMiliseconds() {
        return miliseconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimerViewModel that = (TimerViewModel) o;

        if (hours != that.hours) return false;
        if (miliseconds != that.miliseconds) return false;
        if (minutes != that.minutes) return false;
        if (seconds != that.seconds) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hours;
        result = 31 * result + minutes;
        result = 31 * result + seconds;
        result = 31 * result + miliseconds;
        return result;
    }
}
