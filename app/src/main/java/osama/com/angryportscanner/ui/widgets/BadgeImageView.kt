package osama.com.angryportscanner.ui.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet


class BadgeImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val borderPaint = Paint().apply {
        color = Color.WHITE
    }

    var badgeColor = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.run {
            val radius = 4.toPx
            val borderRadius = 5.toPx
            drawCircle(
                width.toFloat() - borderRadius,
                height.toFloat() - borderRadius,
                borderRadius,
                borderPaint
            )
            drawCircle(
                width.toFloat() - radius,
                height.toFloat() - radius,
                radius,
                paint.apply { color = badgeColor })
        }
    }
}