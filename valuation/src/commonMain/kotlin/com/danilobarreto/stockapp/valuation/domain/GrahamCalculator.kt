package com.danilobarreto.stockapp.valuation.domain

import kotlin.math.sqrt

/**
 * Preço-teto de Graham: raiz(22,5 × LPA × VPA).
 * Só ação — LPA (eps) e VPA (bookValuePerShare) não existem pra FII.
 * Fórmula original de Benjamin Graham (1949), sem revisão desde então.
 */
object GrahamCalculator {
    fun calculate(eps: Double, bookValuePerShare: Double): ValuationResult {
        if (eps <= 0 || bookValuePerShare <= 0) {
            return ValuationResult.Unavailable("LPA e VPA precisam ser positivos pra aplicar Graham")
        }
        val fairPrice = sqrt(22.5 * eps * bookValuePerShare)
        return ValuationResult.FairPrice(fairPrice)
    }
}