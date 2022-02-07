package com.intellimize.model;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class Event {
    private final int adId;
    private final ZonedDateTime timestamp;
    private final EventType eventType;
    private final String impressionId;
    private final String userId;
    private final String ip;
    private final String userAgent;
    private Session session;
}
