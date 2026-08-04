package com.danilobarreto.stockapp.valuation.presentation

import com.danilobarreto.stockapp.valuation.domain.ValuationResult

data class ValuationListItem(
    val ticker: String,
    val name: String?,
    val currentPrice: Double?,
    val grahamResult: ValuationResult?,
    val bazinResult: ValuationResult?,
    val gordonResult: ValuationResult?,
)