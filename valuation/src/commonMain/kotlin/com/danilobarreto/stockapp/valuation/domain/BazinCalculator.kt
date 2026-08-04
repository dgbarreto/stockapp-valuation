package com.danilobarreto.stockapp.valuation.domain

/**
 * Preço-teto de Bazin: dividendo por ação (12m) ÷ yield alvo.
 * Vale pra ação e FII — ambos expõem dividendo TTM por cota/ação.
 */
object BazinCalculator {
    fun calculate(dividendPerShareTtm: Double, targetYield: Double): ValuationResult {
        if (dividendPerShareTtm <= 0) {
            return ValuationResult.Unavailable("Sem dividendo pago nos últimos 12 meses")
        }
        if (targetYield <= 0) {
            return ValuationResult.Unavailable("Yield alvo precisa ser maior que zero")
        }
        val fairPrice = dividendPerShareTtm / targetYield
        return ValuationResult.FairPrice(fairPrice)
    }
}