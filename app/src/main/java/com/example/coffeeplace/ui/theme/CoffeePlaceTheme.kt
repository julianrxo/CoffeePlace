package com.example.coffeeplace.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CoffeePrimary,
    secondary = CoffeeSecondary,
    tertiary = CoffeeTertiary
)

private val LightColorScheme = lightColorScheme(
    primary = CoffeePrimary,
    secondary = CoffeeSecondary,
    tertiary = CoffeeTertiary
)

@Composable
fun CoffeePlaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
