package com.intellimize.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AdMetrics {
    private final Map<String, Metric> dailyImpressionConversion;
    private final Map<String, Metric> dailySessionConversion;
    private final Map<String, Metric> dailyUserConversion;

    public AdMetrics() {
        this.dailyImpressionConversion = new HashMap<>();
        this.dailySessionConversion = new HashMap<>();
        this.dailyUserConversion = new HashMap<>();
    }

}
