package com.danilobarreto.stockapp.valuation.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.danilobarreto.stockapp.designsystem.components.StockAppBadge
import com.danilobarreto.stockapp.designsystem.components.StockAppBadgeStyle
import com.danilobarreto.stockapp.designsystem.components.StockAppSegmentedControl
import com.danilobarreto.stockapp.designsystem.icons.StockAppIcons
import com.danilobarreto.stockapp.designsystem.theme.StockAppColors
import com.danilobarreto.stockapp.designsystem.theme.StockAppShapes
import com.danilobarreto.stockapp.designsystem.theme.StockAppTypography
import com.danilobarreto.stockapp.designsystem.util.toDecimalString
import com.danilobarreto.stockapp.valuation.domain.ValuationResult

// Trilha da barra de progresso — cor específica do protótipo (#F0EBE4), não é um token
// do design system porque só é usada aqui.
private val progressTrackColor = Color(0xFFF0EBE4)

private enum class ValuationFilter { ALL, BELOW, ABOVE }

// A lista mostra um único "preço-teto" por ativo (a tela não tem espaço pra 3 fórmulas
// lado a lado). Preferência: Bazin (mais comum pros ativos desse app) > Graham > Gordon.
// As outras fórmulas continuam disponíveis no detalhe (ValuationScreen), essa é só a
// simplificação da linha da lista.
private val ValuationListItem.primaryCeiling: Double?
    get() = (bazinResult as? ValuationResult.FairPrice)?.value
        ?: (grahamResult as? ValuationResult.FairPrice)?.value
        ?: (gordonResult as? ValuationResult.FairPrice)?.value

private val ValuationListItem.isBelowCeiling: Boolean?
    get() {
        val ceiling = primaryCeiling ?: return null
        val price = currentPrice ?: return null
        return price <= ceiling
    }

@Composable
fun ValuationListScreen(
    viewModel: ValuationListViewModel,
    onItemClick: (ticker: String) -> Unit,
    onBack: () -> Unit,
) {
    val items by viewModel.uiState.collectAsState()
    var filter by remember { mutableStateOf(ValuationFilter.ALL) }

    val filteredItems = items.filter {
        when (filter) {
            ValuationFilter.ALL -> true
            ValuationFilter.BELOW -> it.isBelowCeiling == true
            ValuationFilter.ABOVE -> it.isBelowCeiling == false
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(StockAppColors.surface1)) {
        Column(modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)).padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 16.dp)) {
            HeaderIconButton(icon = StockAppIcons.ArrowLeft, contentDescription = "Voltar", onClick = onBack)
            Text(
                "Valuation da carteira",
                style = StockAppTypography.titleLarge,
                color = StockAppColors.textPrimary,
                modifier = Modifier.padding(top = 22.dp),
            )
            Text(
                "Preço atual contra o preço-teto de cada posição.",
                style = StockAppTypography.bodyMedium,
                color = StockAppColors.textMuted,
                modifier = Modifier.padding(top = 6.dp),
            )
            StockAppSegmentedControl(
                options = listOf("Todos", "Abaixo do teto", "Acima do teto"),
                selectedIndex = filter.ordinal,
                onOptionSelected = { filter = ValuationFilter.entries[it] },
                modifier = Modifier.padding(top = 20.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredItems, key = { it.ticker }) { item ->
                ValuationListRow(item, onClick = { onItemClick(item.ticker) })
            }
        }
    }
}

@Composable
private fun HeaderIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(StockAppColors.surface2)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = contentDescription, tint = StockAppColors.textPrimary, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun ValuationListRow(item: ValuationListItem, onClick: () -> Unit) {
    val ceiling = item.primaryCeiling
    val price = item.currentPrice
    val below = item.isBelowCeiling
    val statusColor = when (below) {
        true -> StockAppColors.textSuccess
        false -> StockAppColors.textDanger
        null -> StockAppColors.textMuted
    }
    val marginPercent = if (ceiling != null && price != null && price > 0) {
        ((ceiling - price) / price) * 100
    } else null
    val progress = if (ceiling != null && price != null && ceiling > 0) {
        (price / ceiling).toFloat().coerceIn(0f, 1f)
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StockAppColors.surface2, shape = StockAppShapes.cardRadius)
            .clickable(onClick = onClick)
            .padding(15.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(item.ticker, style = StockAppTypography.titleMedium, color = StockAppColors.textPrimary)
            when (below) {
                true -> StockAppBadge("Abaixo do teto", StockAppBadgeStyle.Success)
                false -> StockAppBadge("Acima do teto", StockAppBadgeStyle.Danger)
                null -> StockAppBadge("Sem dado suficiente", StockAppBadgeStyle.Warning)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            ValuationStat(modifier = Modifier.weight(1f), label = "Atual", value = price?.let { "R$ ${it.toDecimalString()}" } ?: "—")
            ValuationStat(modifier = Modifier.weight(1f), label = "Teto", value = ceiling?.let { "R$ ${it.toDecimalString()}" } ?: "—")
            ValuationStat(
                modifier = Modifier.weight(1f),
                label = "Margem",
                value = marginPercent?.let { "${if (it >= 0) "+" else ""}${it.toDecimalString()}%" } ?: "—",
                valueColor = statusColor,
                alignEnd = true,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(7.dp)
                .background(progressTrackColor, shape = RoundedCornerShape(4.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxSize()
                    .background(statusColor, shape = RoundedCornerShape(4.dp)),
            )
        }
    }
}

@Composable
private fun ValuationStat(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = StockAppColors.textPrimary, alignEnd: Boolean = false) {
    Column(modifier = modifier, horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(label, style = StockAppTypography.labelSmall, color = StockAppColors.textMuted)
        Text(value, style = StockAppTypography.bodyMedium, color = valueColor, modifier = Modifier.padding(top = 2.dp))
    }
}