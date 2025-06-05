package com.example.iiitbazaar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.iiitbazaar.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val DancingScript = FontFamily(
    Font(R.font.dancing_script_medium, FontWeight.Normal),
)

val CustomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Poppins,
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Poppins,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF0077B6),
    onPrimary = Color.White,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onSurface = Color.Black,
    onSurfaceVariant = Color.Gray
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    onSurfaceVariant = Color.LightGray
)

@Composable
fun IIITBazaarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = content
    )
}
