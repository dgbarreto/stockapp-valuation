package com.danilobarreto.stockapp.valuation.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GordonCalculatorTest {
    @Test
    fun calculatesFairPriceWhenDiscountExceedsGrowth() {
        val result = GordonCalculator.calculate(dividendPerShareTtm = 1.0, growthRate = 0.03, discountRate = 0.08)
        assertIs<ValuationResult.FairPrice>(result)
        assertEquals(20.6, result.value, absoluteTolerance = 0.01)
    }

    @Test
    fun returnsUnavailableWhenDiscountEqualsGrowth() {
        val result = GordonCalculator.calculate(dividendPerShareTtm = 1.0, growthRate = 0.05, discountRate = 0.05)
        assertIs<ValuationResult.Unavailable>(result)
    }

    @Test
    fun returnsUnavailableWhenDiscountBelowGrowth() {
        val result = GordonCalculator.calculate(dividendPerShareTtm = 1.0, growthRate = 0.10, discountRate = 0.05)
        assertIs<ValuationResult.Unavailable>(result)
    }
}