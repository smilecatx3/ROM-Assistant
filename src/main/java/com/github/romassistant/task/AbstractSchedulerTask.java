package com.github.romassistant.task;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;


abstract class AbstractSchedulerTask implements SchedulerTask {
    DayOfWeek day;
    LocalTime time;
    long period;


    AbstractSchedulerTask(DayOfWeek day, LocalTime time, long period) {
        this.day = day;
        this.time = time;
        this.period = period;
    }

    /**
     * Cretes a scheduler task with a default period of 7 days.
     */
    AbstractSchedulerTask(DayOfWeek day, LocalTime time) {
        this(day, time, Duration.ofDays(7).toSeconds());
    }

    @Override
    public DayOfWeek getDay() {
        return day;
    }

    @Override
    public LocalTime getTime() {
        return time;
    }

    @Override
    public long getPeriod() {
        return period;
    }
}
