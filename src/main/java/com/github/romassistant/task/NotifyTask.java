package com.github.romassistant.task;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;


public class NotifyTask extends AbstractSchedulerTask {
    private static final Log log = LogFactory.getLog(NotifyTask.class);
    private static final String URL = "https://notify-api.line.me/api/notify";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String BEARER;

    private String message;


    static {
        var token = Objects.requireNonNull(System.getenv("LINE_NOTIFY_BEARER"),
                "The LINE_NOTIFY_BEARER environment variable is missing.");
        BEARER = "Bearer " + token;
        log.info("Line Notify bearer: " +
                new StringBuilder(token).replace(3, token.length()-3, "*"));
    }


    public NotifyTask(DayOfWeek day, LocalTime time, String message) {
        super(day, time);
        this.message = message;
    }

    @Override
    public void run() {
        HttpClient httpClient = HttpClients.createDefault();
        HttpEntity httpEntity = new UrlEncodedFormEntity(
                List.of(new BasicNameValuePair("message", message)), StandardCharsets.UTF_8);

        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Content-Type", CONTENT_TYPE);
        httpPost.setHeader("Authorization", BEARER);
        httpPost.setEntity(httpEntity);

        try {
            logResponse(httpClient.execute(httpPost));
        } catch (Exception e) {
            log.error("Failed to execute task.", e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s{day=%s; time=%s; period=%d; message=%s}",
                this.getClass().getSimpleName(), day, time, period, message);
    }

    private void logResponse(HttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream stream = entity.getContent()) {
                    log.debug(String.format("Task: %s; response: %s",
                            this, IOUtils.toString(stream, StandardCharsets.UTF_8)));
                }
            }
        }
    }
}
