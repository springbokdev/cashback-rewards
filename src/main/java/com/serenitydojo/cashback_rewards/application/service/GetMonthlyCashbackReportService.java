package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.GetMonthlyCashbackReportUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import com.serenitydojo.cashback_rewards.domain.model.ReportingPeriod;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;

@Service
public class GetMonthlyCashbackReportService implements GetMonthlyCashbackReportUseCase {

    private final CashbackRepository cashbacks;

    public GetMonthlyCashbackReportService(CashbackRepository cashbacks) {
        this.cashbacks = cashbacks;
    }

    @Override
    public List<CashbackRecord> reportFor(String customerId, YearMonth month, ZoneId timezone) {
        ReportingPeriod period = ReportingPeriod.forMonth(month, timezone);
        return cashbacks.findByCustomerId(customerId).stream()
                .filter(record -> period.contains(record.postedAt()))
                .toList();
    }
}
