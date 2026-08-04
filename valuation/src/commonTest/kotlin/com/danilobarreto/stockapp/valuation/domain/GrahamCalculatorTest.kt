package com.danilobarreto.stockapp.valuation.domain

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GrahamCalculatorTest {
    @Test
    fun calculatesFairPriceForPositiveEpsAndBookValue() {
        val result = GrahamCalculator.calculate(eps = 2.0, bookValuePerShare = 10.0)
        assertIs<ValuationResult.FairPrice>(result)
        assertTrue(abs(result.value - 21.2132) < 0.001)
    }

    @Test
    fun returnsUnavailableForNegativeEps() {
        val result = GrahamCalculator.calculate(eps = -1.0, bookValuePerShare = 10.0)
        assertIs<ValuationResult.Unavailable>(result)
    }

    @Test
    fun returnsUnavailableForNegativeBookValue() {
        val result = GrahamCalculator.calculate(eps = 2.0, bookValuePerShare = -5.0)
        assertIs<ValuationResult.Unavailable>(result)
    }
}