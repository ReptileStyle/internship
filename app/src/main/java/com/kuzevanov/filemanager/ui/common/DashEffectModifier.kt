package com.kuzevanov.filemanager.ui.common

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Density

fun Modifier.dashBorder(
    intervals: Density.() -> FloatArray,
    strokeLineWidth: Density.() -> Float,
    strokeColor: Color,
    cornerRadius: CornerSize
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "dashBorder"
        properties["intervals"] = intervals
    },
    factory = {
        this.then(
            DashEffectModifier(
                intervals = with(LocalDensity.current) { intervals.invoke(this) },
                strokeLineWidth = with(LocalDensity.current) { strokeLineWidth.invoke(this) },
                strokeColor = strokeColor,
                cornerRadius = cornerRadius
            )
        )
    })


private class DashEffectModifier(
    intervals: FloatArray,
    strokeLineWidth: Float,
    private val strokeColor: Color,
    private val cornerRadius: CornerSize
) : DrawModifier {

    private val paint: Paint = Paint().apply {
        style = PaintingStyle.Stroke
        strokeWidth = strokeLineWidth
        color = strokeColor
        pathEffect = PathEffect.dashPathEffect(intervals)
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        drawIntoCanvas { canvas ->
            canvas.drawRoundRect(
                left = 0f,
                top = 0f,
                right = size.width,
                bottom = size.height,
                radiusX = cornerRadius.toPx(size, this),
                radiusY = cornerRadius.toPx(size, this),
                paint = paint
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DashEffectModifier

        if (strokeColor != other.strokeColor) return false
        if (cornerRadius != other.cornerRadius) return false
        if (paint != other.paint) return false

        return true
    }

    override fun hashCode(): Int {
        var result = strokeColor.hashCode()
        result = 31 * result + cornerRadius.hashCode()
        result = 31 * result + paint.hashCode()
        return result
    }


}