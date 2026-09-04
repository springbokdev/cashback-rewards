package com.serenitydojo.cashback_rewards.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cashback is 2% of the purchase amount")
class CashbackCalculatorTest {

    @Test
    void calculatesTwoPercentOfThePurchaseAmount() {
        BigDecimal cashback = CashbackCalculator.cashbackFor(new BigDecimal("100.00"));

        assertThat(cashback).isEqualByComparingTo("2.00");
    }

    @Test
    void roundsFractionsOfACentDown() {
        BigDecimal cashback = CashbackCalculator.cashbackFor(new BigDecimal("10.99"));

        assertThat(cashback).isEqualByComparingTo("0.21");
    }

    @Test
    void earnsNoCashbackOnAZeroAmountPurchase() {
        BigDecimal cashback = CashbackCalculator.cashbackFor(BigDecimal.ZERO);

        assertThat(cashback).isEqualByComparingTo("0.00");
    }
}
