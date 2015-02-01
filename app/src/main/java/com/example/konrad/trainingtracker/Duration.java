package com.example.konrad.trainingtracker;

/**
 * Created by Comarch on 2015-01-26.
 */
public class Duration {
    private int hours;
    private int minutes;
    private int seconds;

    public Duration() {
        this(0);
    }

    public Duration(long miliseconds) {
        int totalSeconds = (int) miliseconds / 1000;
        seconds = totalSeconds % 60;
        int totalMinutes = (int) totalSeconds / 60;
        minutes = totalMinutes % 60;
        hours = (int) totalMinutes / 60;
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

    @Override
    public String toString() {
        final String FORMAT = "%02d";
        return String.format(FORMAT, getHours()) + ":" + String.format(FORMAT, getMinutes()) + ":" + String.format(FORMAT, getSeconds());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Duration that = (Duration) o;

        if (hours != that.hours) return false;
        if (minutes != that.minutes) return false;
        if (seconds != that.seconds) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hours;
        result = 31 * result + minutes;
        result = 31 * result + seconds;
        return result;
    }
}
