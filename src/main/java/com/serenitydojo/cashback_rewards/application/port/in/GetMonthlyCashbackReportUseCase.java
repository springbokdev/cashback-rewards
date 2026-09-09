package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

public interface GetMonthlyCashbackReportUseCase {
    List<CashbackRecord> reportFor(String customerId, YearMonth month, ZoneId timezone);
}
