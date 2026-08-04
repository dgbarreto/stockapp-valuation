package com.danilobarreto.stockapp.valuation.presentation

import com.danilobarreto.stockapp.valuation.domain.ValuationResult

data class ValuationUiState(
    val currentPrice: Double? = null,
    val dividendYieldTtm: Double? = null,
    val priceToSalesRatio: Double? = null,
    val targetYieldPercent: String = "6",
    val growthPercent: String = "",
    val discountPercent: String = "6",
    val isGrahamApplicable: Boolean = false,
    val grahamResult: ValuationResult? = null,
    val bazinResult: ValuationResult? = null,
    val gordonResult: ValuationResult? = null,
)