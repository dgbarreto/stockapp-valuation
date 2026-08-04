package com.danilobarreto.stockapp.valuation.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class BazinCalculatorTest {
    @Test
    fun calculatesFairPriceForValidDividendAndYield() {
        val result = BazinCalculator.calculate(dividendPerShareTtm = 1.2, targetYield = 0.06)
        assertIs<ValuationResult.FairPrice>(result)
        assertEquals(20.0, result.value, absoluteTolerance = 0.001)
    }

    @Test
    fun returnsUnavailableForZeroDividend() {
        val result = BazinCalculator.calculate(dividendPerShareTtm = 0.0, targetYield = 0.06)
        assertIs<ValuationResult.Unavailable>(result)
    }

    @Test
    fun returnsUnavailableForZeroTargetYield() {
        val result = BazinCalculator.calculate(dividendPerShareTtm = 1.2, targetYield = 0.0)
        assertIs<ValuationResult.Unavailable>(result)
    }
}