package com.intellimize.model;

public enum EventType {
    VIEW,
    CLICK;

    public static EventType fromString(String s) {
        if (s.equals("view")) {
            return VIEW;
        } else if (s.equals("click")){
            return CLICK;
        } else {
            throw new IllegalArgumentException("Invalid event type");
        }
    }
}
