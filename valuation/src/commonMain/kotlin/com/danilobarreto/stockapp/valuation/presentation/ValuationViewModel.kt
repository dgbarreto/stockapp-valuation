package com.danilobarreto.stockapp.valuation.presentation

import androidx.lifecycle.ViewModel
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.valuation.domain.AssetFundamentals
import com.danilobarreto.stockapp.valuation.domain.BazinCalculator
import com.danilobarreto.stockapp.valuation.domain.GordonCalculator
import com.danilobarreto.stockapp.valuation.domain.GrahamCalculator
import com.danilobarreto.stockapp.valuation.domain.ValuationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ValuationViewModel(
    private val fundamentals: AssetFundamentals,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ValuationUiState(
            currentPrice = fundamentals.currentPrice,
            dividendYieldTtm = fundamentals.dividendYieldTtm,
            priceToSalesRatio = fundamentals.priceToSalesRatio,
            growthPercent = fundamentals.earningsCagr5y?.let { (it * 100).toDecimalString(1) } ?: "",
            isGrahamApplicable = fundamentals.eps != null && fundamentals.bookValuePerShare != null,
        )
    )
    val uiState: StateFlow<ValuationUiState> = _uiState.asStateFlow()

    init { recalculate() }

    fun onTargetYieldChanged(rawValue: String) {
        _uiState.update { it.copy(targetYieldPercent = rawValue) }
        recalculate()
    }

    fun onGrowthChanged(rawValue: String) {
        _uiState.update { it.copy(growthPercent = rawValue) }
        recalculate()
    }

    fun onDiscountChanged(rawValue: String) {
        _uiState.update { it.copy(discountPercent = rawValue) }
        recalculate()
    }

    private fun recalculate() {
        val dividendPerShareTtm = fundamentals.dividendPerShareTtm

        val graham = if (fundamentals.eps != null && fundamentals.bookValuePerShare != null) {
            GrahamCalculator.calculate(fundamentals.eps, fundamentals.bookValuePerShare)
        } else {
            ValuationResult.Unavailable("LPA/VPA não disponíveis pra esse ativo")
        }

        val targetYield = _uiState.value.targetYieldPercent.toDoubleOrNull()?.div(100)
        val bazin = when {
            dividendPerShareTtm == null -> ValuationResult.Unavailable("Sem dividendo disponível pra esse ativo")
            targetYield == null -> ValuationResult.Unavailable("Yield alvo inválido")
            else -> BazinCalculator.calculate(dividendPerShareTtm, targetYield)
        }

        val growth = _uiState.value.growthPercent.toDoubleOrNull()?.div(100)
        val discount = _uiState.value.discountPercent.toDoubleOrNull()?.div(100)
        val gordon = when {
            dividendPerShareTtm == null -> ValuationResult.Unavailable("Sem dividendo disponível pra esse ativo")
            growth == null -> ValuationResult.Unavailable("Taxa de crescimento (g) inválida")
            discount == null -> ValuationResult.Unavailable("Taxa de desconto (k) inválida")
            else -> GordonCalculator.calculate(dividendPerShareTtm, growth, discount)
        }

        _uiState.update { it.copy(grahamResult = graham, bazinResult = bazin, gordonResult = gordon) }
    }
}