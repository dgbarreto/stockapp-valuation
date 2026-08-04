package com.danilobarreto.stockapp.valuation.domain

/**
 * Input desacoplado de quotes/fiis — só os números que as fórmulas precisam.
 * stockapp-app mapeia QuoteFundamentals/FiiSnapshot pra isso antes de chamar o módulo.
 * Campos nulos indicam dado não aplicável ao tipo de ativo (ex.: eps/bookValuePerShare em FII)
 * ou não disponível na resposta da bolsai naquele momento.
 */
data class AssetFundamentals(
    val currentPrice: Double? = null,
    val eps: Double? = null,
    val bookValuePerShare: Double? = null,
    val dividendPerShareTtm: Double? = null,
    val dividendYieldTtm: Double? = null,
    val earningsCagr5y: Double? = null,
    val priceToSalesRatio: Double? = null,
)