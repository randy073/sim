package com.intellimize.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Metric {
    private final Set<String> total = new HashSet<>();
    private final Set<String> clicks = new HashSet<>();

    public String toString() {
        double conversionRate = clicks.size() * 1.0 / total.size();
        return "total=" + total.size() + ", clicks=" + clicks.size() + ", conversionRate=" + conversionRate;
    }
}
