package com.danilobarreto.stockapp.valuation.domain

sealed interface ValuationResult {
    data class FairPrice(val value: Double) : ValuationResult
    data class Unavailable(val reason: String) : ValuationResult
}