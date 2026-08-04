package com.danilobarreto.stockapp.valuation.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppBadge
import com.danilobarreto.stockapp.designsystem.components.StockAppBadgeStyle
import com.danilobarreto.stockapp.designsystem.components.StockAppCard
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.valuation.domain.ValuationResult

@Composable
fun ValuationListScreen(
    viewModel: ValuationListViewModel,
    onItemClick: (ticker: String) -> Unit,
    onBack: () -> Unit,
) {
    val items by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Valuation da carteira") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.ticker }) { item ->
                ValuationListRow(item, onClick = { onItemClick(item.ticker) })
            }
        }
    }
}

@Composable
private fun ValuationListRow(item: ValuationListItem, onClick: () -> Unit) {
    StockAppCard(modifier = Modifier.clickable(onClick = onClick)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.ticker, style = MaterialTheme.typography.titleMedium)
                Text(item.currentPrice?.toDecimalString() ?: "—", style = MaterialTheme.typography.bodyMedium)
            }
            Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item.grahamResult?.let { StatusBadge("Graham", it, item.currentPrice) }
                item.bazinResult?.let { StatusBadge("Bazin", it, item.currentPrice) }
                item.gordonResult?.let { StatusBadge("Gordon", it, item.currentPrice) }
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, result: ValuationResult, currentPrice: Double?) {
    if (result is ValuationResult.FairPrice && currentPrice != null) {
        val isBelow = currentPrice <= result.value
        StockAppBadge(
            text = "$label ${if (isBelow) "↓" else "↑"}",
            style = if (isBelow) StockAppBadgeStyle.Success else StockAppBadgeStyle.Danger
        )
    }
}