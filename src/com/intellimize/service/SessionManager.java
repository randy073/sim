package com.intellimize.service;

import com.intellimize.model.Event;
import com.intellimize.model.EventType;
import com.intellimize.model.Session;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;

public class SessionManager {
    private static final int THIRTY_MINUTES_IN_SECONDS = 30 * 60;

    private Map<String, List<Session>> sessionMap;

    public SessionManager() {
        this.sessionMap = new HashMap<>();
    }

    public Session attachSession(Event event) {
        String key = event.getUserId() + event.getUserAgent();
        ZonedDateTime eventTime = event.getTimestamp();

        List<Session> sessions = sessionMap.get(key);
        if (sessions == null) {
            sessions = new ArrayList<>();
            sessionMap.put(key, sessions);
        }

        for (Session session : sessions) {
            Duration timeToStart = Duration.between(eventTime, session.getStartTime());
            Duration timeFromEnd = Duration.between(session.getEndTime(), eventTime);
            if (eventTime.isAfter(session.getStartTime()) && eventTime.isBefore(session.getEndTime())
                    || (timeToStart.getSeconds() <= THIRTY_MINUTES_IN_SECONDS && timeFromEnd.getSeconds() <= THIRTY_MINUTES_IN_SECONDS)) {
                if (eventTime.isBefore(session.getStartTime())) {
                    session.setStartTime(eventTime);
                }
                if (eventTime.isAfter(session.getEndTime())) {
                    session.setEndTime(eventTime);
                }
                if (event.getEventType() == EventType.VIEW) {
                    session.getAdsViewed().add(event.getAdId());
                } else {
                    session.getAdsClicked().add(event.getAdId());
                }
                Set<String> adsImpressions = session.getAdsImpressions().get(event.getAdId());
                if (adsImpressions == null) {
                    adsImpressions = new HashSet<>();
                    session.getAdsImpressions().put(event.getAdId(), adsImpressions);
                }
                adsImpressions.add(event.getImpressionId());
                return session;
            }
        }
        Session session = Session.builder()
                .startTime(eventTime)
                .endTime(eventTime)
                .build();
        sessions.add(session);

        if (event.getEventType() == EventType.VIEW) {
            session.getAdsViewed().add(event.getAdId());
        } else {
            session.getAdsClicked().add(event.getAdId());
        }
        Set<String> adsImpressions = new HashSet<>();
        adsImpressions.add(event.getImpressionId());
        session.getAdsImpressions().put(event.getAdId(), adsImpressions);

        return session;
    }

    public List<Session> getAllSessions() {
        List<Session> all = new ArrayList<>();
        for (List<Session> value : sessionMap.values()) {
            all.addAll(value);
        }
        return all;
    }


}
