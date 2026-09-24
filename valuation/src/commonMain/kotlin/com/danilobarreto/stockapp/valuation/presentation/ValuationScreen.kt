package com.danilobarreto.stockapp.valuation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppBadge
import com.danilobarreto.stockapp.designsystem.components.StockAppBadgeStyle
import com.danilobarreto.stockapp.designsystem.components.StockAppCard
import com.danilobarreto.stockapp.designsystem.components.StockAppKeyValueRow
import com.danilobarreto.stockapp.designsystem.components.StockAppTextField
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.valuation.domain.ValuationResult

@Composable
fun ValuationScreen(viewModel: ValuationViewModel, ticker: String, onBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(StockAppColors.surface1)) {
        Column(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)).padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(StockAppColors.surface2)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(StockAppIcons.ArrowLeft, contentDescription = "Voltar", tint = StockAppColors.textPrimary, modifier = Modifier.size(20.dp))
            }
            Text("Valuation", style = StockAppTypography.titleLarge, color = StockAppColors.textPrimary, modifier = Modifier.padding(top = 22.dp))
            Text(ticker, style = StockAppTypography.bodyMedium, color = StockAppColors.textMuted, modifier = Modifier.padding(top = 6.dp))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                StockAppCard {
                    Column {
                        StockAppKeyValueRow("Preço atual", uiState.currentPrice?.let { "R$ ${it.toDecimalString()}" } ?: "—")
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
            Text(title, style = StockAppTypography.titleMedium, color = StockAppColors.textPrimary)
            when (result) {
                is ValuationResult.FairPrice -> {
                    StockAppKeyValueRow("Preço-teto", "R$ ${result.value.toDecimalString()}")
                    if (currentPrice != null) {
                        StockAppKeyValueRow("Preço atual", "R$ ${currentPrice.toDecimalString()}")
                        val isBelowCeiling = currentPrice <= result.value
                        Spacer(modifier = Modifier.padding(top = 6.dp))
                        StockAppBadge(
                            text = if (isBelowCeiling) "Abaixo do teto" else "Acima do teto",
                            style = if (isBelowCeiling) StockAppBadgeStyle.Success else StockAppBadgeStyle.Danger
                        )
                    }
                }
                is ValuationResult.Unavailable -> Text(result.reason, style = StockAppTypography.bodySmall, color = StockAppColors.textMuted, modifier = Modifier.padding(top = 4.dp))
                null -> Text("Calculando…", style = StockAppTypography.bodySmall, color = StockAppColors.textMuted, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}