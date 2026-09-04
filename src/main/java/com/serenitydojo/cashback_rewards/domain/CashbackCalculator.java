package com.serenitydojo.cashback_rewards.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates the cashback earned on a purchase.
 */
public final class CashbackCalculator {

    private static final BigDecimal CASHBACK_RATE = new BigDecimal("0.02");
    private static final int CENTS = 2;

    private CashbackCalculator() {
    }

    public static BigDecimal cashbackFor(BigDecimal purchaseAmount) {
        return purchaseAmount.multiply(CASHBACK_RATE)
                             .setScale(CENTS, RoundingMode.DOWN);
    }
}
