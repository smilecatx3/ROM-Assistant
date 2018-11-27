package com.github.romassistant.task;

import java.time.DayOfWeek;
import java.time.LocalTime;


public interface SchedulerTask extends Runnable {
    DayOfWeek getDay();
    LocalTime getTime();

    /**
     * Gets the period in seconds.
     */
    long getPeriod();
}
