package com.danilobarreto.stockapp.valuation.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.danilobarreto.stockapp.designsystem.theme.StockAppTheme

// Sample isolado do módulo valuation: diferente dos outros samples, este não depende de
// stockapp-auth nem faz chamada de rede — o módulo é cálculo puro em Kotlin (ver decisão
// registrada em docs/decisoes.md do repo de planejamento), então não existe backend pra logar.
// Assim que domain/presentation de verdade existirem, a tela de placeholder abaixo vira a
// calculadora de valuation (recebendo um snapshot de cotação/FII de exemplo, sem rede).
@Composable
fun SampleApp() {
    StockAppTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Valuation — em construção", style = MaterialTheme.typography.titleMedium)
        }
    }
}
