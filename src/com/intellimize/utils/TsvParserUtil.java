package com.intellimize.utils;

import com.intellimize.model.Event;
import com.intellimize.model.EventType;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TsvParserUtil {
    private static final TsvParserSettings TSV_PARSER_SETTINGS = new TsvParserSettings();
    private static final TsvParser PARSER = new TsvParser(TSV_PARSER_SETTINGS);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss z");

    public static List<Event> parseEventsFromFile() {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(new File("resource/Sim raw input data.tsv")));
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            List<String[]> rows = PARSER.parseAll(br);
            // skip first title row
            List<Event> events = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                events.add(Event.builder()
                        .adId(Integer.parseInt(row[0]))
                        .timestamp(getZonedDateTime(row[1]))
                        .eventType(EventType.fromString(row[2]))
                        .impressionId(row[3])
                        .userId(row[4])
                        .ip(row[5])
                        .userAgent(row[6])
                        .build());
            }
            return events;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read input", e);
        }
    }

    private static ZonedDateTime getZonedDateTime(String timestamp) {
        return ZonedDateTime.from(DATE_TIME_FORMATTER.parse(timestamp));
    }

}
