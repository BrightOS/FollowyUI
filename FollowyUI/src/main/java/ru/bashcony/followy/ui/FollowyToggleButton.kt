package ru.bashcony.followy.ui

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView

class FollowyToggleButton(
    context: Context,
    attrs: AttributeSet?
) : MaterialCardView(
    context,
    attrs,
    com.google.android.material.R.style.Widget_Material3_CardView_Filled
) {

    val toggleTitle: TextView
    val toggleIcon: ImageView

    override fun setChecked(checked: Boolean) {
        val previousForegroundColor = toggleTitle.currentTextColor
        val foregroundColor = if (checked)
            context.getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)
        else
            context.getColorFromAttr(com.google.android.material.R.attr.colorOnSurfaceVariant)

        val previousBackgroundColor = cardBackgroundColor.defaultColor
        val backgroundColor = if (checked)
            context.getColorFromAttr(com.google.android.material.R.attr.colorPrimaryContainer)
        else
            context.getColorFromAttr(com.google.android.material.R.attr.colorSurfaceContainerHigh)

        ObjectAnimator.ofObject(
            this,
            "cardBackgroundColor",
            ArgbEvaluator(),
            previousBackgroundColor,
            backgroundColor
        ).apply {
            duration = 150
            start()
        }

        toggleTitle.setTextColor(foregroundColor)
        toggleIcon.imageTintList = ColorStateList.valueOf(foregroundColor)

        super.setChecked(checked)
    }

    init {
        View.inflate(context, R.layout.layout_followy_toggle_button, this)

        elevation = 0f

        toggleTitle = findViewById(R.id.toggle_title)
        toggleIcon = findViewById(R.id.toggle_icon)

        radius = 15.dp

        toggleTitle.isSelected = true

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