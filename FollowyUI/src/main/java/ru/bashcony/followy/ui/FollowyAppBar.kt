package ru.bashcony.followy.ui

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import com.google.android.material.appbar.AppBarLayout

/**
 * A class that represents an already configured AppBarLayout with
 * scrolling animation. Use in CoordinatorLayout for best effect.
 */
class FollowyAppBar constructor(
    cont: Context,
    attrs: AttributeSet?
) : AppBarLayout(cont, attrs) {

    /**
     * Function for setting the action when clicking on the left icon
     * @param action action that will happen when clicked
     * @return Nothing
     */
    fun setStartButtonOnClickListener(action: (View) -> Unit) {
        findViewById<ImageView>(R.id.start_button).setOnClickListener(action)
    }

    /**
     * Function for setting the action when clicking on the right icon
     * @param action action that will happen when clicked
     * @return Nothing
     */
    fun setEndButtonOnClickListener(action: (View) -> Unit) {
        findViewById<ImageView>(R.id.end_button).setOnClickListener(action)
    }

    /**
     * Function to hide the right button
     * @return Nothing
     */
    fun hideEndButton() {
        findViewById<ImageView>(R.id.end_button).visibility = View.INVISIBLE
    }

    /**
     * Function to show the right button
     * @return Nothing
     */
    fun showEndButton() {
        findViewById<ImageView>(R.id.end_button).visibility = View.VISIBLE
    }

    /**
     * Button at the left of AppBar
     * @return button view - ImageView
     */
    val startButton: ImageView
        get() = findViewById(R.id.start_button)

    /**
     * Button at the right of AppBar
     * @return button view - ImageView
     */
    val endButton: ImageView
        get() = findViewById(R.id.end_button)

    /**
     * Text in the center of AppBar
     * @param value text to be set in the appbar
     * @return text set in appbar
     */
    var text: CharSequence
        get() = findViewById<TextView>(R.id.appbar_title).text
        set(value) {
            findViewById<TextView>(R.id.appbar_title).text = value
        }

    init {
        View.inflate(context, R.layout.layout_followy_appbar, this)

        elevation = 0f
        background = ColorDrawable("#00000000".toColorInt())

        val text = findViewById<View>(R.id.appbar_title) as TextView
        text.isSelected = true

        addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            text.scaleX = 1f + verticalOffset / (appBarLayout.totalScrollRange.toFloat() / 0.15f)
            text.scaleY = 1f + verticalOffset / (appBarLayout.totalScrollRange.toFloat() / 0.15f)
//            val vaX = ObjectAnimator.ofFloat(
//                text,
//                SCALE_X,
//                1f + verticalOffset / (appBarLayout.totalScrollRange.toFloat() / 0.15f)
//            )
//            val vaY = ObjectAnimator.ofFloat(
//                text,
//                SCALE_Y,
//                1f + verticalOffset / (appBarLayout.totalScrollRange.toFloat() / 0.15f)
//            )
//            vaX.duration = 300
//            vaY.duration = 300
//            vaX.start()
//            vaY.start()
        }

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.FollowyAppBar)

            typedArray.getResourceId(
                R.styleable.FollowyAppBar_startIcon,
                0
            ).let {
                if (it != 0)
                    findViewById<ImageView>(R.id.start_button).setImageResource(it)
            }

            typedArray.getResourceId(
                R.styleable.FollowyAppBar_endIcon,
                0
            ).let {
                if (it != 0)
                    findViewById<ImageView>(R.id.end_button).setImageResource(it)
            }

            text.text = typedArray.getText(
                R.styleable.FollowyAppBar_text
            )

            typedArray.getResourceId(
                R.styleable.FollowyAppBar_font,
                0
            ).let {
                try {
                    text.typeface = ResourcesCompat.getFont(
                        context, if (it != 0)
                            it
                        else
                            R.font.inter_semibold
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            typedArray.recycle()
        }
    }
}