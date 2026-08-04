package com.danilobarreto.stockapp.valuation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppBadge
import com.danilobarreto.stockapp.designsystem.components.StockAppBadgeStyle
import com.danilobarreto.stockapp.designsystem.components.StockAppCard
import com.danilobarreto.stockapp.designsystem.components.StockAppKeyValueRow
import com.danilobarreto.stockapp.designsystem.components.StockAppTextField
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.valuation.domain.ValuationResult

@Composable
fun ValuationScreen(viewModel: ValuationViewModel, ticker: String, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Valuation — $ticker") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", style = StockAppTypography.titleLarge, color = StockAppColors.textPrimary)
                    }
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StockAppCard {
                    Column {
                        StockAppKeyValueRow("Preço atual", uiState.currentPrice?.toDecimalString() ?: "—")
                        StockAppKeyValueRow("DY (12m)", uiState.dividendYieldTtm?.let { "${(it * 100).toDecimalString()}%" } ?: "—")
                        StockAppKeyValueRow("P/S (PSR)", uiState.priceToSalesRatio?.toDecimalString() ?: "—")
                    }
                }
            }
            item {
                StockAppTextField(
                    label = "Yield alvo do Bazin (%)",
                    value = uiState.targetYieldPercent,
                    onValueChange = viewModel::onTargetYieldChanged,
                    keyboardType = KeyboardType.Decimal
                )
            }
            item {
                StockAppTextField(
                    label = "Crescimento esperado — g (%)",
                    value = uiState.growthPercent,
                    onValueChange = viewModel::onGrowthChanged,
                    keyboardType = KeyboardType.Decimal,
                    supportingText = "Sugestão: CAGR de lucro 5 anos, editável"
                )
            }
            item {
                StockAppTextField(
                    label = "Taxa de desconto — k (%)",
                    value = uiState.discountPercent,
                    onValueChange = viewModel::onDiscountChanged,
                    keyboardType = KeyboardType.Decimal,
                    supportingText = "Sugestão: 6% (referência de mercado americano, ajuste pro seu contexto)"
                )
            }
            if (uiState.isGrahamApplicable) {
                item { ValuationResultCard("Graham", uiState.grahamResult, uiState.currentPrice) }
            }
            item { ValuationResultCard("Bazin", uiState.bazinResult, uiState.currentPrice) }
            item { ValuationResultCard("Gordon", uiState.gordonResult, uiState.currentPrice) }
        }
    }
}
@Composable
private fun ValuationResultCard(title: String, result: ValuationResult?, currentPrice: Double?) {
    StockAppCard {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium)
            when (result) {
                is ValuationResult.FairPrice -> {
                    StockAppKeyValueRow("Preço-teto", result.value.toDecimalString())
                    if (currentPrice != null) {
                        StockAppKeyValueRow("Preço atual", currentPrice.toDecimalString())
                        val isBelowCeiling = currentPrice <= result.value
                        Spacer(modifier = Modifier.padding(top = 6.dp))
                        StockAppBadge(
                            text = if (isBelowCeiling) "Abaixo do teto" else "Acima do teto",
                            style = if (isBelowCeiling) StockAppBadgeStyle.Success else StockAppBadgeStyle.Danger
                        )
                    }
                }
                is ValuationResult.Unavailable -> Text(result.reason, style = MaterialTheme.typography.bodySmall)
                null -> Text("Calculando…", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}