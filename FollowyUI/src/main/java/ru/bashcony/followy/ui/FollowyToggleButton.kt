package ru.bashcony.followy.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toRectF
import androidx.core.view.setPadding
import kotlin.math.min


class FollowyToggleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    val toggleTitle: TextView
    val toggleIcon: ImageView
    lateinit var rect: RectF

    var cornerRadius: Float = 15.dp.toFloat()
        set(value) {
            field = value
            invalidate()
        }

    val paint = Paint().apply {
        color =
            context.getColorFromAttr(com.google.android.material.R.attr.colorSurfaceContainerHigh)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    var isChecked: Boolean = false
        set(value) {
            val foregroundColor = if (value)
                context.getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)
            else
                context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurfaceVariant)

            val previousBackgroundColor = paint.color
            val backgroundColor = if (value)
                context.getColorFromAttr(com.google.android.material.R.attr.colorPrimaryContainer)
            else
                context.getColorFromAttr(com.google.android.material.R.attr.colorSurfaceContainerHigh)

            ObjectAnimator.ofObject(
                paint,
                "color",
                ArgbEvaluator(),
                previousBackgroundColor,
                backgroundColor
            ).apply {
                duration = 150
                addUpdateListener { invalidate() }
                start()
            }

            toggleTitle.setTextColor(foregroundColor)
            toggleIcon.imageTintList = ColorStateList.valueOf(foregroundColor)

            field = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("OptimizedFollowyToggleButton", "onMeasure")

        measureChild(toggleIcon, widthMeasureSpec, heightMeasureSpec)
        measureChild(toggleTitle, widthMeasureSpec, heightMeasureSpec)

        val desiredHeight =
            20.dp + toggleIcon.measuredHeight + toggleTitle.measuredHeight // Предполагаемая высота View

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = widthSize

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize // Задан конкретный размер для высоты
            MeasureSpec.AT_MOST -> min(
                desiredHeight,
                heightSize
            ) // Размер не должен превышать заданный размер
            else -> desiredHeight // Задать предпочтительный размер, если точного или максимального размера не задано
        }

        setMeasuredDimension(width, height) // Устанавливаем фактический размер View
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val iconLayoutParams = toggleIcon.layoutParams as MarginLayoutParams
        val titleLayoutParams = toggleTitle.layoutParams as MarginLayoutParams

        var x = paddingLeft + iconLayoutParams.leftMargin
        var y = paddingTop + iconLayoutParams.topMargin

        toggleIcon.layout(x, y, x + toggleIcon.measuredWidth, y + toggleIcon.measuredHeight)
        Log.d(
            "OptimizedFollowyToggleButton",
            "onLayout icon $x $y ${x + toggleIcon.measuredWidth} ${y + toggleIcon.measuredHeight}"
        )

        x = paddingLeft + titleLayoutParams.leftMargin
        y += toggleIcon.measuredHeight

        toggleTitle.layout(x, y, x + toggleTitle.measuredWidth, y + toggleTitle.measuredHeight)
        Log.d(
            "OptimizedFollowyToggleButton",
            "onLayout title $x $y ${x + toggleTitle.measuredWidth} ${y + toggleTitle.measuredHeight}"
        )
    }

//    override fun generateDefaultLayoutParams(): MarginLayoutParams {
//        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
        Log.d("OptimizedFollowyToggleButton", "onDraw $rect")
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateDimensions()
        invalidate()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        updateDimensions()
        invalidate()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        updateDimensions()
        invalidate()
    }

    private fun updateDimensions() {
        rect = calculateBounds()
    }

    private fun calculateBounds() =
        Rect(0, 0, measuredWidth, measuredHeight).toRectF()

    init {
        inflate(context, R.layout.layout_followy_toggle_button, this)

        val foregroundColor = context.getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)

        toggleTitle = TextView(context).also {
            it.layoutParams =
                MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            it.ellipsize = TextUtils.TruncateAt.MARQUEE
            it.marqueeRepeatLimit = -1
            it.isSingleLine = true
            it.isSelected = true
            it.typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
            it.setTextColor(foregroundColor)
        }

        toggleIcon = ImageView(context).also {
            it.layoutParams = MarginLayoutParams(24.dp, 24.dp)
            it.imageTintList = ColorStateList.valueOf(foregroundColor)
        }

        addView(toggleIcon)
        addView(toggleTitle)

        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.FollowyToggleButton).apply {
                toggleTitle.text = getText(R.styleable.FollowyToggleButton_title)

                getResourceId(R.styleable.FollowyToggleButton_icon, 0).let {
                    if (it != 0)
                        toggleIcon.setImageResource(it)
                }

                recycle()
            }
        }

        isChecked = false

        setPadding(10.dp)
        setWillNotDraw(false)
    }

    companion object {
        fun synchronizeButtons(buttons: List<Pair<FollowyToggleButton, () -> Unit>>) {
            buttons.forEach { pair ->
                pair.first.setOnClickListener {
                    pair.first.isChecked = true
                    buttons.filter { it != pair }.forEach { it.first.isChecked = false }
                    pair.second()
                }
            }
        }
    }
}