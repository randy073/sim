package com.intellimize.service;

import com.intellimize.model.AdMetrics;
import com.intellimize.model.Event;
import com.intellimize.model.EventType;
import com.intellimize.model.Metric;
import com.intellimize.model.Session;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intellimize.utils.TsvParserUtil.parseEventsFromFile;

public class ConversionsCalculator {
    private final SessionManager sessionManager;
    private final Map<Integer, AdMetrics> adMetricsMap;

    public ConversionsCalculator(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.adMetricsMap = new HashMap<>();
    }

    public Map<Integer, AdMetrics> getMetrics() {
       List<Event> events = parseEventsFromFile();
       sortEventsByTimestamp(events);
       filterInvalidEvents(events);
       populateSession(events);
       calculateImpressionMetrics(events);
       calculateSessionMetrics();
       calculateUserMetrics(events);

       return adMetricsMap;
    }

    private void calculateImpressionMetrics(List<Event> events) {
        for (Event event : events) {
            AdMetrics adMetrics = adMetricsMap.get(event.getAdId());
            if (adMetrics == null) {
                adMetrics = new AdMetrics();
                adMetricsMap.put(event.getAdId(), adMetrics);
            }
            String key = getDailyKey(event.getTimestamp());
            Metric metric = adMetrics.getDailyImpressionConversion().get(key);
            if (metric == null) {
                metric = new Metric();
                adMetrics.getDailyImpressionConversion().put(key, metric);
            }
            if (event.getEventType() == EventType.VIEW) {
                metric.getTotal().add(event.getImpressionId());
            } else {
                metric.getClicks().add(event.getImpressionId());
            }
        }
    }

    private void calculateSessionMetrics() {
        List<Session> sessions = sessionManager.getAllSessions();
        for (Session session : sessions) {
            String key = getDailyKey(session.getEndTime());
            for (Integer adId : session.getAdsViewed()) {
                AdMetrics adMetrics = adMetricsMap.get(adId);
                if (adMetrics == null) {
                    adMetrics = new AdMetrics();
                    adMetricsMap.put(adId, adMetrics);
                }
                Metric metric = adMetrics.getDailySessionConversion().get(key);
                if (metric == null) {
                    metric = new Metric();
                    adMetrics.getDailySessionConversion().put(key, metric);
                }
                metric.getTotal().add(session.getId());
            }
            for (Integer adId : session.getAdsClicked()) {
                AdMetrics adMetrics = adMetricsMap.get(adId);
                if (adMetrics == null) {
                    adMetrics = new AdMetrics();
                    adMetricsMap.put(adId, adMetrics);
                }
                Metric metric = adMetrics.getDailySessionConversion().get(key);
                if (metric == null) {
                    metric = new Metric();
                    adMetrics.getDailySessionConversion().put(key, metric);
                }
                metric.getClicks().add(session.getId());
            }

        }
    }

    private void calculateUserMetrics(List<Event> events) {
        for (Event event : events) {
            AdMetrics adMetrics = adMetricsMap.get(event.getAdId());
            if (adMetrics == null) {
                adMetrics = new AdMetrics();
                adMetricsMap.put(event.getAdId(), adMetrics);
            }
            String key = getDailyKey(event.getTimestamp());
            Metric metric = adMetrics.getDailyUserConversion().get(key);
            if (metric == null) {
                metric = new Metric();
                adMetrics.getDailyUserConversion().put(key, metric);
            }
            if (event.getEventType() == EventType.VIEW) {
                metric.getTotal().add(event.getUserId());
            } else {
                metric.getClicks().add(event.getUserId());
            }
        }

        // remove clicks with no views
        for (AdMetrics adMetrics : adMetricsMap.values()) {
            for (Metric metric : adMetrics.getDailyUserConversion().values()) {
                metric.getClicks().removeIf(userIdClick -> !metric.getTotal().contains(userIdClick));
            }
        }
    }

    private void populateSession(List<Event> events) {
        for (Event event : events) {
            event.setSession(sessionManager.attachSession(event));
        }
    }

    private void filterInvalidEvents(List<Event> events) {
        events.removeIf(event -> event.getUserAgent().contains("HeadlessChrome")); // remove prerender headless chrome
    }

    private void sortEventsByTimestamp(List<Event> events) {
        events.sort((a, b) -> {
            if (a.getTimestamp().isBefore(b.getTimestamp())) {
                return -1;
            } else if (a.getTimestamp().isAfter(b.getTimestamp())) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    private String getDailyKey(ZonedDateTime zonedDateTime) {
        return "" + zonedDateTime.getMonth() + "-" + zonedDateTime.getDayOfMonth();
    }


}
