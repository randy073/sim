package com.intellimize;

import com.intellimize.service.ConversionsCalculator;
import com.intellimize.service.SessionManager;

public class Main {

    public static void main(String[] args) {
        SessionManager sessionManager = new SessionManager();
        ConversionsCalculator conversionsCalculator = new ConversionsCalculator(sessionManager);

        System.out.println(conversionsCalculator.getMetrics());
    }
}
