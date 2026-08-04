# stockapp-valuation

Módulo KMP (Kotlin Multiplatform) + Compose Multiplatform do [StockApp](https://github.com/dgbarreto/stockapp-app) — app de acompanhamento de investimentos (projeto de estudo).

Calculadora de valuation (Graham, Bazin, Gordon, PSR, PEG) pra ações e FIIs. **Diferente dos demais módulos do projeto, este é cálculo puro em Kotlin — sem camada `data`/cliente HTTP e sem endpoint novo no `stockapp-backend`.** Recebe o snapshot de cotação/FII que a tela de `stockapp-quotes` já buscou (LPA, VPA, dividend yield, `cagr_earnings_5y` etc.) e devolve os preços-teto, sem chamar a bolsai de novo. Decisão completa e motivação registradas em `docs/decisoes.md` do repo de planejamento.

Divisão de fórmulas por tipo de ativo:

- **Graham, PSR, PEG** — só ação (dependem de LPA/VPA/P&L, que FII não tem).
- **Bazin, Gordon** — ação e FII (baseados em dividendo: `ttm_per_share`, disponível nos dois endpoints da bolsai).

## Estrutura

- `valuation/` — único módulo do repo, alvo Android (via `com.android.kotlin.multiplatform.library`) + iOS (framework estático `Valuation`), código comum em `valuation/src/commonMain`. Sem pacote `data` (não é cliente de API).
- `sample/` + `sample-android/` — apps de exemplo, dev-only, pra validar o módulo isoladamente. Sem dependência de `stockapp-auth` nem rede — o sample só monta o módulo com dado de exemplo.

## Status

**Fase 7 — Valuation** (ver roadmap em `docs/roadmap.md` no repo de planejamento): escopo fechado, scaffold criado a partir do template `stockapp-portfolio`. Ainda sem domain/presentation de verdade (fórmulas, modelos de input/resultado, telas) implementados neste módulo.

## Stack

- Kotlin 2.4.0 · Compose Multiplatform 1.11.1 · AGP 9.0.1

## Rodando

```
./gradlew :valuation:build
./gradlew :valuation:testAndroidHostTest
./gradlew :valuation:iosSimulatorArm64Test
```

---

_Progresso mantido manualmente conforme o projeto avança._
