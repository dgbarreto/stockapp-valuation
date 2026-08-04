package com.danilobarreto.stockapp.valuation.sample

import androidx.compose.runtime.Composable
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme
import com.danilobarreto.stockapp.valuation.domain.AssetFundamentals
import com.danilobarreto.stockapp.valuation.domain.AssetValuationInput
import com.danilobarreto.stockapp.valuation.presentation.ValuationListScreen
import com.danilobarreto.stockapp.valuation.presentation.ValuationListViewModel

// Sample isolado do módulo valuation: sem stockapp-auth nem chamada de rede — o módulo é
// cálculo puro em Kotlin (ver decisão em docs/decisoes.md do repo de planejamento), então os
// dados abaixo são só pra exercitar as combinações reais que a lista precisa tratar.
@Composable
fun SampleApp() {
    val items = listOf(
        // Ação "normal": tudo presente, crescimento abaixo de 6% — os três badges aparecem.
        AssetValuationInput(
            ticker = "PETR4",
            name = "Petrobras",
            fundamentals = AssetFundamentals(
                currentPrice = 38.0,
                eps = 2.5,
                bookValuePerShare = 25.0,
                dividendPerShareTtm = 2.0,
                dividendYieldTtm = 0.0526,
                earningsCagr5y = 0.04,
                priceToSalesRatio = 1.2,
            ),
        ),
        // Ação de alto crescimento: CAGR (12%) acima do k default (6%) — Gordon vira
        // Unavailable e some da linha, sem preço-teto negativo (mesmo problema achado na
        // mira.xlsx, aqui tratado em vez de ignorado).
        AssetValuationInput(
            ticker = "WEGE3",
            name = "WEG",
            fundamentals = AssetFundamentals(
                currentPrice = 45.0,
                eps = 1.0,
                bookValuePerShare = 8.0,
                dividendPerShareTtm = 0.3,
                dividendYieldTtm = 0.0067,
                earningsCagr5y = 0.12,
                priceToSalesRatio = 6.5,
            ),
        ),
        // FII: sem eps/bookValuePerShare (Graham some) nem earningsCagr5y (Gordon some,
        // já que hoje só sabemos "g" via CAGR de lucro — conceito de ação). Só Bazin aparece.
        AssetValuationInput(
            ticker = "HGLG11",
            name = "CSHG Logística",
            fundamentals = AssetFundamentals(
                currentPrice = 160.0,
                dividendPerShareTtm = 9.0,
                dividendYieldTtm = 0.0563,
                priceToSalesRatio = null,
            ),
        ),
        // Ação sem dividendo: Bazin e Gordon somem (dependem de dividendPerShareTtm),
        // só Graham aparece.
        AssetValuationInput(
            ticker = "MGLU3",
            name = "Magazine Luiza",
            fundamentals = AssetFundamentals(
                currentPrice = 2.10,
                eps = 0.05,
                bookValuePerShare = 1.80,
                dividendPerShareTtm = null,
                dividendYieldTtm = null,
                earningsCagr5y = 0.02,
                priceToSalesRatio = 0.8,
            ),
        ),
    )

    val viewModel = ValuationListViewModel(items)

    StockAppTheme {
        ValuationListScreen(
            viewModel = viewModel,
            onItemClick = { /* navegação real fica pro stockapp-app; sample só lista */ },
            onBack = { },
        )
    }
}
