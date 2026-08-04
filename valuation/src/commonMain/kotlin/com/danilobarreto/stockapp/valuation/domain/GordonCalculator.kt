package com.danilobarreto.stockapp.valuation.domain

/**
 * Preço-teto de Gordon (DDM): D1 / (k - g), D1 = DPA × (1 + g).
 * Vale pra ação e FII (baseado em dividendo, igual Bazin).
 */
object GordonCalculator {
    fun calculate(dividendPerShareTtm: Double, growthRate: Double, discountRate: Double): ValuationResult {
        if (dividendPerShareTtm <= 0) {
            return ValuationResult.Unavailable("Sem dividendo pago nos últimos 12 meses")
        }
        if (discountRate <= growthRate) {
            return ValuationResult.Unavailable("k precisa ser maior que g")
        }
        val nextDividend = dividendPerShareTtm * (1 + growthRate)
        val fairPrice = nextDividend / (discountRate - growthRate)
        return ValuationResult.FairPrice(fairPrice)
    }
}