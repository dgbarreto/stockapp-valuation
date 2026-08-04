package com.danilobarreto.stockapp.valuation

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
