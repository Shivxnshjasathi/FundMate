package com.zincstate.fundmate.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.zincstate.fundmate.data.model.NavDataDto


@Composable
fun SimpleLineChart(
    data: List<NavDataDto>,
    lineColor: Color = Color(0xFF00D09C),
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    // Pre-calculate min/max for scaling
    val navValues = remember(data) { data.map { it.nav.toDouble() } }
    val minNav = navValues.minOrNull() ?: 0.0
    val maxNav = navValues.maxOrNull() ?: 100.0

    Canvas(modifier = modifier.fillMaxSize()) {
        if (navValues.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val range = maxNav - minNav

        // Path for the line
        val path = Path().apply {
            moveTo(0f, height - ((navValues.first() - minNav) / range * height).toFloat())
            navValues.forEachIndexed { index, nav ->
                val x = (index.toFloat() / (navValues.size - 1)) * width
                val y = height - ((nav - minNav) / range * height).toFloat()
                lineTo(x, y)
            }
        }

        // 1. Draw the gradient area below the line
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        // 2. Draw the main line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
