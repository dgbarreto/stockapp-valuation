package com.danilobarreto.stockapp.valuation.domain

data class AssetValuationInput(
    val ticker: String,
    val name: String? = null,
    val fundamentals: AssetFundamentals,
)