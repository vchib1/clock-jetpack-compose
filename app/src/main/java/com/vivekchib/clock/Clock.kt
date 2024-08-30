package com.vivekchib.clock

import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivekchib.clock.ui.theme.ClockTheme
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

enum class ClockShape {
    Circle, Rectangle, Dodecagon, Butterfly
}

@Preview
@Composable
fun ComposablePreview() {
    ClockTheme(false) {
        Scaffold(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it), contentAlignment = Alignment.Center
            ) {
                Clock()
            }
        }
    }
}

@Composable
fun Clock(
    modifier: Modifier = Modifier,
    showHourNumbers: Boolean = true,
    clockShape: ClockShape = ClockShape.Circle,
    showDay: Boolean = false,
) {

    // COLORS
    val bgColor = MaterialTheme.colorScheme.primary
    val hourHandColor = MaterialTheme.colorScheme.onPrimary
    val minuteHandColor = MaterialTheme.colorScheme.onSecondary
    val secondsHandColor = MaterialTheme.colorScheme.onBackground
    val clockHourNumColor = MaterialTheme.colorScheme.onPrimaryContainer

    // TIME
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(key1 = "clockKey") {
        while (true) {
            delay(1000)
            currentTime = Calendar.getInstance()

            Log.d(
                "Time", "${currentTime.get(Calendar.HOUR)}:${currentTime.get(Calendar.MINUTE)}:${
                    currentTime.get(Calendar.SECOND)
                }"
            )

        }
    }

    val hours = currentTime.get(Calendar.HOUR)
    val minutes = currentTime.get(Calendar.MINUTE)
    val seconds = currentTime.get(Calendar.SECOND)

    val secondsRotation = (seconds * 6f) % 360f
    val minutesRotation = (minutes * 6f + seconds / 10f) % 360f
    val hoursRotation = (hours * 30f + minutes / 2f) % 360f

    val textStyle = TextStyle(
        fontSize = 100.sp,
        fontWeight = FontWeight.W900,
        color = clockHourNumColor,
        fontFamily = FontFamily.Default
    )

    Canvas(
        modifier = modifier
            .requiredSize(size = 300.dp)
            .rotate(0f)
    ) {

        val (width, height) = Pair(size.width, size.height)

        val radius = min(width, height) / 2
        val padding = 20

        when (clockShape) {
            ClockShape.Circle -> {
                drawCircle(color = bgColor, radius = width / 2)
            }

            ClockShape.Rectangle -> {
                drawRoundRect(
                    color = bgColor,
                    size = Size(width, height),
                    cornerRadius = CornerRadius(100f, 100f)
                )
            }

            ClockShape.Dodecagon -> {
                val path = Path().apply {
                    val n = 12
                    val angleStep = 2 * Math.PI / n
                    val radius = min(width, height) / 2
                    val cornerRadius = 20f // Adjust this value to set the border radius

                    // Start from the first point
                    val startX = (width / 2 + radius * cos(0.0)).toFloat()
                    val startY = (height / 2 + radius * sin(0.0)).toFloat()
                    moveTo(startX, startY)

                    (1..n).forEach { i ->
                        // Calculate the next point
                        val nextX = (width / 2 + radius * cos(i * angleStep)).toFloat()
                        val nextY = (height / 2 + radius * sin(i * angleStep)).toFloat()

                        // Calculate the control point for the curve
                        val controlX = (width / 2 + radius * cos((i - 0.5) * angleStep)).toFloat()
                        val controlY = (height / 2 + radius * sin((i - 0.5) * angleStep)).toFloat()

                        // Create a curve from the current point to the next
                        quadraticBezierTo(controlX, controlY, nextX, nextY)
                    }

                    // Close the path
                    close()
                }

                drawPath(path, bgColor)
            }

            ClockShape.Butterfly -> {
                drawCircle(bgColor, 125.dp.toPx(), center = center)
                drawCircle(bgColor, 100.dp.toPx(), center = Offset(center.x / 2, center.y / 2))
                drawCircle(
                    bgColor, 100.dp.toPx(), center = Offset(center.x * 1.5f, center.y / 2)
                )
                drawCircle(
                    bgColor, 100.dp.toPx(), center = Offset(center.x / 2, center.y * 1.5f)
                )
                drawCircle(
                    bgColor, 100.dp.toPx(), center = Offset(center.x * 1.5f, center.y * 1.5f)
                )
            }
        }


        if (showHourNumbers) {
            val text12 = textMeasurer.measure("12", style = textStyle)

            drawText(text12, topLeft = Offset(center.x - text12.size.width / 2, 0f))

            val text3 = textMeasurer.measure("3", style = textStyle)

            drawText(
                text3, topLeft = Offset(
                    size.width - text3.size.width - (text3.size.width / 4),
                    center.y - text3.size.height / 2
                )
            )

            val text6 = textMeasurer.measure("6", style = textStyle)

            drawText(
                text6,
                topLeft = Offset(center.x - text12.size.width / 4, size.height - text6.size.height)
            )

            val text9 = textMeasurer.measure("9", style = textStyle)

            drawText(
                text9,  // 9
                topLeft = Offset(0f + text9.size.width / 4, center.y - text9.size.height / 2)
            )
        }

        // Draw hour hand
        rotate(hoursRotation - 90f) {
            drawLine(
                hourHandColor,
                center,
                Offset(width * .65f, height / 2),
                strokeWidth = 75f,
                cap = StrokeCap.Round
            )
        }

        // Draw minute hand
        rotate(minutesRotation - 90f) {
            drawLine(
                minuteHandColor,
                center,
                Offset(width * .85f, height / 2),
                strokeWidth = 75f,
                cap = StrokeCap.Round
            )
        }

        // Draw second hand
        rotate(secondsRotation + 90f) {
            drawCircle(secondsHandColor, radius = 30f, center = Offset(width / 10, height / 2))
        }

        if (showDay) {
            rotate(secondsRotation + 180f) {
                val pos1 = Offset(radius / 2, center.y - radius)
                val pos2 = Offset(width - radius / 2, center.y - radius)

                val textPath = Path().apply {
                    moveTo(padding.toFloat(), center.y)
                    cubicTo(
                        pos1.x, pos1.y, pos2.x, pos2.y, width - padding, center.y
                    )
                }

                val dayName =
                    android.text.format.DateFormat.format("EEE", currentTime).toString().uppercase()

                this.drawContext.canvas.nativeCanvas.apply {
                    drawTextOnPath(dayName.toString(),
                        textPath.asAndroidPath(),
                        0f,
                        0f,
                        Paint().apply {
                            this.color = secondsHandColor.toArgb()
                            this.textSize = 75f
                            this.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            this.textAlign = Paint.Align.CENTER
                        })
                }
            }
        }



        drawCircle(bgColor, radius = 20f, center = center)
    }
}
