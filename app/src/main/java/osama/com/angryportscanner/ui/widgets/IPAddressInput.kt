package osama.com.angryportscanner.ui.widgets


import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import osama.com.angryportscanner.R


class IPAddressInput @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    private fun createView(segmentIndex: Int): EditText {

        return EditText(context).apply {
            hint = "000"
            id = segmentIndex
            minWidth = 48.toPx.toInt()
            inputType = InputType.TYPE_CLASS_NUMBER
            textAlignment = TEXT_ALIGNMENT_CENTER
            background = null
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
            setPadding(0, 0, 0, 0)
            this.addTextChangedListener(createWatcher(segmentIndex))
            nextFocusLeftId = segmentIndex + 1
            nextFocusDownId = segmentIndex + 1
            nextFocusUpId = segmentIndex + 1
            nextFocusRightId = segmentIndex + 1

        }
    }

    init {
        orientation = HORIZONTAL
        for (i in 0..3) {
            addView(createView(i))
        }
    }

    fun text(separator: String = "."): String =
        children.joinToString(separator) {
            val text = (it as EditText).text.toString()
            return@joinToString if (text.isBlank()) "0" else text
        }


    private fun createWatcher(segmentIndex: Int) = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (s.length > 3) {
                s.delete(3, s.length)
                return
            }
            val segment = s.toString().toIntOrNull() ?: 0

            if (segment > 255) {
                s.replace(0, 3, "255")
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            if (s.length > 3 && count >= 2) {
                val pastedText = s.toString()
                for (i in 0..3) {
                    val child = getChildAt(i) as EditText
                    val trimAt = i * 3 + 3
                    val text = if (trimAt > pastedText.length) "0" else pastedText.subSequence(
                        trimAt - 3,
                        trimAt
                    )
                    child.setText(text)
                }
                return
            }


            if (s.length == 3 && segmentIndex < 3) {
                getChildAt(segmentIndex + 1).requestFocus()
            } else if (s.isEmpty() && segmentIndex > 0) {
                getChildAt(segmentIndex - 1).requestFocus()
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }
    }


    private val dotPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (childCount > 0) {
            val viewWidth = getChildAt(0).width.toFloat()
            for (i in 1 until childCount) {
                canvas?.drawCircle(viewWidth * i, canvas.height * .6f, 1.5f.toPx, dotPaint)
            }
        }
    }
}


val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
