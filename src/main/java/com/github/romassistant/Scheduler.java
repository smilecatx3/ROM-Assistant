package com.github.romassistant;

import com.github.romassistant.task.NotifyTask;
import com.github.romassistant.task.SchedulerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;


@Service
@ConditionalOnExpression("${service.scheduler.enabled:true}")
public class Scheduler {
    private static final Log log = LogFactory.getLog(Scheduler.class);
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    private ScheduledExecutorService scheduler;
    private Collection<SchedulerTask> tasks;
    private ZoneId zoneId;


    public Scheduler(@Value("${system.zone}") String zone) throws IOException {
        tasks = parseTasks(new ClassPathResource("tasks.json").getFile().toPath());
        scheduler = Executors.newScheduledThreadPool(tasks.size());
        zoneId = ZoneId.of(zone);
    }

    @PostConstruct
    public void run() {
        tasks.forEach(task -> scheduler.scheduleAtFixedRate(
                task, getInitialDelay(task), task.getPeriod(), TIME_UNIT));
    }

    private long getInitialDelay(SchedulerTask task) {
        var now = ZonedDateTime.now(zoneId);
        var dateTime = ZonedDateTime.of(LocalDate.now(), task.getTime(), zoneId);

        var unit = TIME_UNIT.toChronoUnit();
        var execTime = dateTime.with(TemporalAdjusters.nextOrSame(task.getDay()));
        var value = unit.between(now, execTime);
        if (value < 0) {
            execTime = dateTime.with(TemporalAdjusters.next(task.getDay()));
            value = unit.between(now, execTime);
        }
        log.info(String.format("The task %s will be executed at %s.", task, execTime));

        return value;
    }

    private Collection<SchedulerTask> parseTasks(Path file) throws IOException {
        var json = new JSONObject(new String(Files.readAllBytes(file)));

        List<NotifyTask> notifyTasks = new ArrayList<>();
        for(var entry : json.getJSONArray("notify")) {
            var task = (JSONObject)entry;
            var day = DayOfWeek.valueOf(task.getString("day"));
            var time = LocalTime.parse(task.getString("time"));
            var message = Objects.requireNonNull(task.getString("message"));
            notifyTasks.add(new NotifyTask(day, time, message));
        }

        return Collections.unmodifiableCollection(notifyTasks);
    }
}
