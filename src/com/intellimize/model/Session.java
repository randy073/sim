package com.intellimize.model;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class Session {
    private final String id = UUID.randomUUID().toString();
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private final Set<Integer> adsViewed = new HashSet<>();
    private final Set<Integer> adsClicked = new HashSet<>();
    private final Map<Integer, Set<String>> adsImpressions = new HashMap<>();
}
