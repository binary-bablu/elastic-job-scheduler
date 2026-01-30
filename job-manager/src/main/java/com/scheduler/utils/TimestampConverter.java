package com.scheduler.utils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimestampConverter {
	
	// Common date-time patterns
    public static final String PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String PATTERN_DISPLAY = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_WITH_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_WITH_ZONE = "yyyy-MM-dd HH:mm:ss z";
    public static final String PATTERN_DATE_ONLY = "yyyy-MM-dd";
    public static final String PATTERN_TIME_ONLY = "HH:mm:ss";
	
	 /**
     * Convert Timestamp to ZonedDateTime in specific timezone
     */
    public static ZonedDateTime toZonedDateTime(Timestamp timestamp, String timezone) {
        if (timestamp == null || timezone == null) return null;
        return timestamp.toInstant().atZone(ZoneId.of(timezone));
    }
    
    /**
     * Convert Timestamp to ZonedDateTime using system default timezone
     */
    public static ZonedDateTime toZonedDateTime(Timestamp timestamp) {
        if (timestamp == null) return null;
        return timestamp.toInstant().atZone(ZoneId.systemDefault());
    }
    
    /**
     * Convert Timestamp to LocalDateTime in specific timezone
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp, String timezone) {
        if (timestamp == null || timezone == null) return null;
        return timestamp.toInstant()
            .atZone(ZoneId.of(timezone))
            .toLocalDateTime();
    }
    
    /**
     * Convert Timestamp to LocalDateTime using Timestamp's native method
     * NOTE: This uses the JVM's default timezone
     */
    public static LocalDateTime toLocalDateTimeNative(Timestamp timestamp) {
        if (timestamp == null) return null;
        return timestamp.toLocalDateTime();
    }
    
    /**
     * Convert Timestamp to Instant
     */
    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) return null;
        return timestamp.toInstant();
    }
    
    /**
     * Convert LocalDateTime to Timestamp in specific timezone
     */
    public static Timestamp fromLocalDateTime(LocalDateTime localDateTime, String timezone) {
        if (localDateTime == null || timezone == null) return null;
        Instant instant = localDateTime.atZone(ZoneId.of(timezone)).toInstant();
        return Timestamp.from(instant);
    }
    
    /**
     * Convert ZonedDateTime to Timestamp
     */
    public static Timestamp fromZonedDateTime(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return null;
        return Timestamp.from(zonedDateTime.toInstant());
    }
    
    /**
     * Convert Instant to Timestamp
     */
    public static Timestamp fromInstant(Instant instant) {
        if (instant == null) return null;
        return Timestamp.from(instant);
    }
    
    /**
     * Format Timestamp in specific timezone
     */
    public static String formatTimestamp(Timestamp timestamp, String timezone, String pattern) {
        if (timestamp == null || timezone == null) return null;
        ZonedDateTime zdt = toZonedDateTime(timestamp, timezone);
        return zdt.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Format Timestamp in specific timezone
     */
    public static String formatTimestamp(Timestamp timestamp, String timezone) {
        if (timestamp == null || timezone == null) return null;
        
        Instant instant = Instant.now();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        String sourceTimezone = zdt.getZone().toString();
        
     // Step 1: Get LocalDateTime from Timestamp
        LocalDateTime ldt = timestamp.toLocalDateTime();
        
        // Step 2: Interpret as source timezone
        ZonedDateTime sourceZdt = ldt.atZone(ZoneId.of(sourceTimezone));
        
        // Step 3: Convert to target timezone (time values change)
        ZonedDateTime targetZdt = sourceZdt.withZoneSameInstant(ZoneId.of(timezone));
        
        return targetZdt.format(DateTimeFormatter.ofPattern(PATTERN_DISPLAY));
        
    }
    /**
     * Format Timestamp in specific timezone (ISO format)
     */
    public static String formatTimestampISO(Timestamp timestamp, String timezone) {
        if (timestamp == null || timezone == null) return null;
        ZonedDateTime zdt = toZonedDateTime(timestamp, timezone);
        return zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
    
    /**
     * Get current Timestamp
     */
    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Get Timestamp for specific LocalDateTime in timezone
     */
    public static Timestamp of(int year, int month, int day, int hour, int minute, int second, String timezone) {
        LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, minute, second);
        return fromLocalDateTime(ldt, timezone);
    }
    
    /**
     * Format LocalDateTime in specific timezone with custom pattern
     * NOTE: LocalDateTime doesn't have timezone info, so we add it
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String timezone, String pattern) {
        if (localDateTime == null || timezone == null || pattern == null) return null;
        
        Instant instant = Instant.now();
        ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
        String sourceTimezone = zdt.getZone().toString();
        
        // Step 1: Interpret LocalDateTime in source timezone
        ZonedDateTime sourceZdt = localDateTime.atZone(ZoneId.of(sourceTimezone));
        
        // Step 2: Convert to target timezone
        ZonedDateTime targetZdt = sourceZdt.withZoneSameInstant(ZoneId.of(timezone));
        
        // Step 3: Format
        return targetZdt.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * Format LocalDateTime in specific timezone (default display pattern)
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String timezone) {
        return formatLocalDateTime(localDateTime, timezone, PATTERN_DISPLAY);
    }
    
    /**
     * Format LocalDateTime in specific timezone with ISO offset format
     */
    public static String formatLocalDateTimeISOWithZone(LocalDateTime localDateTime, String timezone) {
        if (localDateTime == null || timezone == null) return null;
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.of(timezone));
        return zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

}
