package com.danilobarreto.stockapp.valuation.presentation

import androidx.lifecycle.ViewModel
import com.danilobarreto.stockapp.valuation.domain.AssetValuationInput
import com.danilobarreto.stockapp.valuation.domain.BazinCalculator
import com.danilobarreto.stockapp.valuation.domain.GordonCalculator
import com.danilobarreto.stockapp.valuation.domain.GrahamCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val DEFAULT_TARGET_YIELD = 0.06
private const val DEFAULT_DISCOUNT_RATE = 0.06

class ValuationListViewModel(items: List<AssetValuationInput>) : ViewModel() {

    private val _uiState = MutableStateFlow(items.map { it.toListItem() })
    val uiState: StateFlow<List<ValuationListItem>> = _uiState.asStateFlow()

    private fun AssetValuationInput.toListItem(): ValuationListItem {
        val f = fundamentals
        val graham = if (f.eps != null && f.bookValuePerShare != null) {
            GrahamCalculator.calculate(f.eps, f.bookValuePerShare)
        } else null

        val bazin = f.dividendPerShareTtm?.let { BazinCalculator.calculate(it, DEFAULT_TARGET_YIELD) }

        val gordon = if (f.dividendPerShareTtm != null && f.earningsCagr5y != null) {
            GordonCalculator.calculate(f.dividendPerShareTtm, f.earningsCagr5y, DEFAULT_DISCOUNT_RATE)
        } else null

        return ValuationListItem(
            ticker = ticker,
            name = name,
            currentPrice = f.currentPrice,
            grahamResult = graham,
            bazinResult = bazin,
            gordonResult = gordon,
        )
    }
}