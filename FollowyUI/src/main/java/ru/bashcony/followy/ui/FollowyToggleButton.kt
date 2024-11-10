package ru.bashcony.followy.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
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
import com.google.android.material.color.MaterialColors
import kotlin.math.min


class FollowyToggleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private val toggleTitle: TextView
    private val toggleIcon: ImageView
    private var toggleIconTintable: Boolean = true
    private lateinit var rect: RectF

    private val placeholderBackgroundColor = Color.TRANSPARENT

    private val placeholderForegroundColor =
        context.getColorFromAttr(androidx.appcompat.R.attr.colorAccent)

    val defaultBackgroundColor = MaterialColors.getColor(
        context,
        com.google.android.material.R.attr.colorSurfaceContainerHigh,
        placeholderBackgroundColor
    )

    val selectedBackgroundColor = MaterialColors.getColor(
        context,
        com.google.android.material.R.attr.colorPrimaryContainer,
        placeholderBackgroundColor
    )

    val defaultForegroundColor = MaterialColors.getColor(
        context,
        com.google.android.material.R.attr.colorOnSurfaceVariant,
        placeholderForegroundColor
    )

    val selectedForegroundColor = MaterialColors.getColor(
        context,
        com.google.android.material.R.attr.colorOnPrimaryContainer,
        placeholderForegroundColor
    )

    var cornerRadius: Float = 15.dp.toFloat()
        set(value) {
            field = value
            invalidate()
        }

    val backgroundPaint = Paint().apply {
        color = defaultBackgroundColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    val foregroundPaint = Paint().apply {
        color = defaultForegroundColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun isClickable(): Boolean {
        return true
    }

    var isChecked: Boolean = false
        set(value) {
            val previousForegroundColor = toggleTitle.currentTextColor
            val foregroundColor = if (value) selectedForegroundColor else defaultForegroundColor
            val previousBackgroundColor = backgroundPaint.color
            val backgroundColor = if (value) selectedBackgroundColor else defaultBackgroundColor

            ObjectAnimator.ofObject(
                backgroundPaint,
                "color",
                ArgbEvaluator(),
                previousBackgroundColor,
                backgroundColor
            ).apply {
                duration = 150
                addUpdateListener { invalidate() }
                start()
            }

            ValueAnimator.ofArgb(
                previousForegroundColor,
                foregroundColor
            ).apply {
                duration = 150
                addUpdateListener {
                    toggleTitle.setTextColor(it.getAnimatedValue() as Int)
                    if (toggleIconTintable)
                        toggleIcon.imageTintList = ColorStateList.valueOf(it.getAnimatedValue() as Int)
                }
                start()
            }

            field = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChild(toggleIcon, widthMeasureSpec, heightMeasureSpec)
        measureChild(toggleTitle, widthMeasureSpec, heightMeasureSpec)

        val desiredHeight =
            20.dp + toggleIcon.measuredHeight + toggleTitle.measuredHeight

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = widthSize

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(
                desiredHeight,
                heightSize
            )

            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val iconLayoutParams = toggleIcon.layoutParams as MarginLayoutParams
        val titleLayoutParams = toggleTitle.layoutParams as MarginLayoutParams

        var x = paddingLeft + iconLayoutParams.leftMargin
        var y = paddingTop + iconLayoutParams.topMargin

        toggleIcon.layout(x, y, x + toggleIcon.measuredWidth, y + toggleIcon.measuredHeight)

        x = paddingLeft + titleLayoutParams.leftMargin
        y += toggleIcon.measuredHeight

        toggleTitle.layout(x, y, x + toggleTitle.measuredWidth, y + toggleTitle.measuredHeight)
    }

    override fun generateDefaultLayoutParams() =
        MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, backgroundPaint)
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

        toggleTitle = TextView(context).also {
            it.layoutParams =
                MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            it.ellipsize = TextUtils.TruncateAt.MARQUEE
            it.marqueeRepeatLimit = -1
            it.isSingleLine = true
            it.isSelected = true
            it.typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
            it.setTextColor(defaultForegroundColor)
        }

        toggleIcon = ImageView(context).also {
            it.layoutParams = MarginLayoutParams(24.dp, 24.dp)
        }

        addView(toggleIcon)
        addView(toggleTitle)

        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.FollowyToggleButton).apply {
                toggleTitle.text = getText(R.styleable.FollowyToggleButton_text)

                getResourceId(R.styleable.FollowyToggleButton_icon, 0).let {
                    if (it != 0)
                        toggleIcon.setImageResource(it)
                }

                (getInt(R.styleable.FollowyToggleButton_iconTinting, 0) == 0).let {
                    toggleIconTintable = it
                    if (it) toggleIcon.imageTintList =
                        ColorStateList.valueOf(defaultForegroundColor)
                }

                getResourceId(R.styleable.FollowyToggleButton_font, 0).let {
                    try {
                        toggleTitle.typeface = ResourcesCompat.getFont(
                            context, if (it != 0)
                                it
                            else
                                R.font.inter_medium
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
            Log.d("AAA", "synchronizeButtons ${buttons.joinToString(" ")}")
            buttons.forEach { pair ->
                Log.d("AAA", "First set click listener")
                pair.first.isClickable = true
                pair.first.setOnClickListener {
                    Log.d("AAA", "Clicked")
                    pair.first.isChecked = true
                    buttons.filter { it.first != pair.first }.forEach { it.first.isChecked = false }
                    pair.second()
                }
            }
        }
    }
}