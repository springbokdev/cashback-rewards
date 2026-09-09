package com.serenitydojo.cashback_rewards.domain.model;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;

public record ReportingPeriod(Instant start, Instant end) {

    public static ReportingPeriod forMonth(YearMonth month, ZoneId timezone) {
        Instant start = month.atDay(1).atStartOfDay(timezone).toInstant();
        Instant end = month.plusMonths(1).atDay(1).atStartOfDay(timezone).toInstant();
        return new ReportingPeriod(start, end);
    }

    public boolean contains(Instant postedAt) {
        return !postedAt.isBefore(start) && postedAt.isBefore(end);
    }
}
