package com.edwardmcgrath.pyrexia.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ThermostatView(
    currentTemp: String = "---",
    setPoint: String = "---",
    message: String = "",
    centerColor: Color = Color.Black,
    setPointTickColor: Color = Color.DarkGray,
    bezelWidth: Dp = 16.dp,
    tickGapDegrees: Float = 5f,
    onClickIncrease: () -> Unit = {},
    onClickDecrease: () -> Unit = {},
    onClickCenter: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var touchTheta by remember { mutableStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .size(300.dp) // Default size, can be adjusted via modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val xCenter = size.width / 2f
                    val yCenter = size.height / 2f
                    val center = Offset(xCenter, yCenter)
                    val dist = offset.distanceFrom(center)
                    val centerRadius = xCenter - bezelWidth.toPx()
                    val rimRadius = xCenter

                    when {
                        dist in centerRadius..rimRadius -> {
                            val theta = offset.angleAroundCenter(center).toFloat() - 90f
                            touchTheta = if (theta < 0f) theta + 360f else theta
                            println("touch theta = ${touchTheta}")
                            val touchTemperature = touchTheta.degreesAroundCircleToTemperature()
                            println("touch temp = ${touchTemperature}")
                            val setPointFloat = setPoint.replace("°", "").toFloatOrNull() ?: 0f
                            when {
                                touchTemperature > setPointFloat -> onClickIncrease()
                                touchTemperature < setPointFloat -> onClickDecrease()
                            }
                        }
                        dist < centerRadius -> {
                            // Handle center click if needed
                            onClickCenter.invoke()
                        }
                    }
                }
            }
    ) {
        val xCenter = size.width / 2f
        val yCenter = size.height / 2f
        val centerRadius = xCenter - bezelWidth.toPx()
        val rimRadius = xCenter
        val markLength = (bezelWidth.toPx() * 2f) / 8

        // Draw background circle
        drawCircle(
            color = centerColor,
            radius = rimRadius,
            center = Offset(xCenter, yCenter)
        )

        // Draw tick marks
        for (d in (DIAL_START_TEMP*10).toInt()..(DIAL_END_TEMP*10).toInt() step (tickGapDegrees*10).toInt()) {
            val a = (d.toFloat()/10f).temperatureToDegreesAroundCircle()
            val r1 = centerRadius
            val t1 = toRadians(a.toDouble())
            val x1 = (r1 * cos(t1) + xCenter).toFloat()
            val y1 = (r1 * sin(t1) + yCenter).toFloat()
            val r2 = rimRadius
            val x2 = (r2 * cos(t1) + xCenter).toFloat()
            val y2 = (r2 * sin(t1) + yCenter).toFloat()
            drawLine(
                color = Color.LightGray,
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw center circle
        drawCircle(
            color = centerColor,
            radius = centerRadius,
            center = Offset(xCenter, yCenter)
        )

        // Draw set point tick
        val setPointFloat = setPoint.replace("°", "").toFloatOrNull() ?: 0f
        val setPointAngle = setPointFloat.temperatureToDegreesAroundCircle()
        val p1 = computeDestPoint(xCenter, yCenter, setPointAngle, centerRadius - markLength)
        val p2 = computeDestPoint(xCenter, yCenter, setPointAngle, rimRadius)
        val p3 = computeDestPoint(xCenter, yCenter, setPointAngle, centerRadius - (xCenter / 5f))
        drawLine(
            color = setPointTickColor,
            start = Offset(p1.x, p1.y),
            end = Offset(p2.x, p2.y),
            strokeWidth = 3.dp.toPx()
        )

        // Draw text
        drawIntoCanvas { canvas ->
            val temperatureTextSizeSp = xCenter.toSp() / 2.2f
            val setPointTextSizeSp = temperatureTextSizeSp * 0.4f
            val messageTextSizeSp = temperatureTextSizeSp * 0.2f

            // Current temperature
            val tempText = textMeasurer.measure(
                text = currentTemp,
                style = TextStyle(fontSize = temperatureTextSizeSp, color = Color.White)
            )
            drawText(
                textLayoutResult = tempText,
                topLeft = Offset(
                    xCenter - tempText.size.width / 2f,
                    yCenter - tempText.size.height / 2f
                )
            )

            // Set point
            val setPointText = textMeasurer.measure(
                text = setPoint,
                style = TextStyle(fontSize = setPointTextSizeSp, color = Color.White)
            )
            drawText(
                textLayoutResult = setPointText,
                topLeft = Offset(
                    p3.x - setPointText.size.width / 2f,
                    p3.y - setPointText.size.height / 2f
                )
            )

            // Message
            if (message.isNotEmpty()) {
                val messageText = textMeasurer.measure(
                    text = message,
                    style = TextStyle(fontSize = messageTextSizeSp, color = Color.White)
                )
                drawText(
                    textLayoutResult = messageText,
                    topLeft = Offset(
                        xCenter - messageText.size.width / 2f,
                        yCenter + tempText.size.height / 2f
                    )
                )
            }
        }
    }
}

// Utility functions (unchanged from original where possible)
private const val DIAL_START_TEMP = 0f
private const val DIAL_END_TEMP = 110f
private const val DIAL_START_DEG = 92
private const val DIAL_END_DEG = 88
private const val DIAL_TOTAL_DEG = 360f - (DIAL_START_DEG - DIAL_END_DEG)
private const val DIAL_BUTTON_SWEEP = (DIAL_START_DEG - DIAL_END_DEG - 5) / 2f
private const val DIAL_BUTTON_1_DEG = DIAL_START_DEG - DIAL_BUTTON_SWEEP
private const val DIAL_BUTTON_2_DEG = DIAL_END_DEG
private const val DIAL_SWEEP_PER_ONE_DEGREE = DIAL_TOTAL_DEG / (DIAL_END_TEMP - DIAL_START_TEMP)

fun Float.degreesAroundCircleToTemperature(): Float {
    var d = this
    if (d < 0f) d += 360f
    if (d > 360f) d -= 360f
    return ((this - DIAL_START_DEG) / DIAL_SWEEP_PER_ONE_DEGREE) + DIAL_START_TEMP
}

fun Float.temperatureToDegreesAroundCircle(): Float {
    var d = ((this - DIAL_START_TEMP) * DIAL_SWEEP_PER_ONE_DEGREE + DIAL_START_DEG)
    if (d > 360f) d -= 360f
    return d
}

fun computeDestPoint(startX: Float, startY: Float, angle: Float, r: Float): Offset {
    val theta = toRadians(angle.toDouble())
    val x = r * cos(theta) + startX
    val y = r * sin(theta) + startY
    return Offset(x.toFloat(), y.toFloat())
}

fun Offset.distanceFrom(center: Offset): Float {
    return sqrt((center.x - this.x) * (center.x - this.x) + (center.y - this.y) * (center.y - this.y))
}

fun Offset.angleAroundCenter(center: Offset): Double {
    val dx = this.x - center.x
    val dy = this.y - center.y
    var theta = toDegrees(atan2(dy, dx).toDouble()) + 90
    if (theta < 0) theta += 360
    if (theta > 360) theta -= 360
    return theta
}

fun toRadians(deg: Double): Double = deg / 180.0 * PI

fun toDegrees(rad: Double): Double = rad * 180.0 / PI

fun Float.formatTemp(): String {
    return this.toInt().toString()
}