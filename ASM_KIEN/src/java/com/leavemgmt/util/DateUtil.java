package com.leavemgmt.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Date utility methods
 */
public class DateUtil {
    
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Convert String to LocalDate
     */
    public static LocalDate parseDate(String dateStr) {
        if (StringUtil.isEmpty(dateStr)) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Convert LocalDate to String
     */
    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(FORMATTER);
    }
    
    /**
     * Convert LocalDate to SQL Date
     */
    public static Date toSqlDate(LocalDate date) {
        return date == null ? null : Date.valueOf(date);
    }
    
    /**
     * Convert SQL Date to LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
    
    /**
     * Calculate days between two dates (inclusive)
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(start, end) + 1;
    }
    
    /**
     * Check if date is weekend
     */
    public static boolean isWeekend(LocalDate date) {
        if (date == null) return false;
        int dayOfWeek = date.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7;
    }
    
    /**
     * Get current date
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * Validate date range
     */
    public static boolean isValidRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return false;
        }
        return !end.isBefore(start);
    }
}