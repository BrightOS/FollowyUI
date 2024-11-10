package ru.bashcony.followy.ui

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.Insets
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

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
     */
    var text: CharSequence
        get() = findViewById<TextView>(R.id.appbar_title).text
        set(value) {
            findViewById<TextView>(R.id.appbar_title).text = value
        }

    /**
     * Height difference between the maximum and minimum FollowyAppBar size value
     */
    var toolbarPadding: Int = 40.dp

    /**
     * Does this view need to handle window insets properly?
     */
    var configureInsets: Boolean = true

    private val collapsingToolbar: CollapsingToolbarLayout
        get() = findViewById(R.id.followy_collapsing_toolbar)

    private val toolbar: Toolbar
        get() = findViewById(R.id.followy_toolbar)

    init {
        View.inflate(context, R.layout.layout_followy_appbar, this)

        elevation = 0f
        background = ColorDrawable("#00000000".toColorInt())

        val text = findViewById<View>(R.id.appbar_title) as TextView
        text.isSelected = true

        attrs?.let { attributes ->
            val typedArray = context.obtainStyledAttributes(attributes, R.styleable.FollowyAppBar)

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

            typedArray.getBoolean(
                R.styleable.FollowyAppBar_configureInsets,
                true,
            ).let {
                this.configureInsets = it
            }

            typedArray.getDimensionPixelSize(
                R.styleable.FollowyAppBar_toolbarPadding,
                40.dp,
            ).let {
                this.toolbarPadding = it
            }

            typedArray.recycle()
        }

        configureInsets()

        addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            text.scaleX =
                1f + (verticalOffset / (appBarLayout.totalScrollRange.toFloat() / 0.15f)).let { if (it.isNaN()) 0f else it }
            text.scaleY =
                1f + (verticalOffset / (appBarLayout.totalScrollRange.toFloat() / 0.15f)).let { if (it.isNaN()) 0f else it }
        }
    }

    private fun configureInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
            val insets =
                if (configureInsets)
                    windowInsets.getInsets(
                        WindowInsetsCompat.Type.systemBars()
                                or WindowInsetsCompat.Type.ime()
                                or WindowInsetsCompat.Type.displayCutout()
                    )
                else
                    Insets.of(0, 0, 0, 0)

            handleUpdatedInsets(insets)

            windowInsets
        }

        if (!configureInsets)
            handleUpdatedInsets(Insets.of(0, 0, 0, 0))
    }

    private fun handleUpdatedInsets(insets: Insets) {
        val defaultToolbarSize = obtainDefaultToolbarSize()

        toolbar.updatePadding(
            left = insets.left,
            top = insets.top,
            right = insets.right,
        )

        collapsingToolbar.scrimVisibleHeightTrigger =
            defaultToolbarSize + insets.top + toolbarPadding / 2

        collapsingToolbar.updateLayoutParams<MarginLayoutParams> {
            this.height = defaultToolbarSize + insets.top + toolbarPadding
        }

        toolbar.updateLayoutParams<MarginLayoutParams> {
            this.height = defaultToolbarSize + insets.top
        }
    }

    private fun obtainDefaultToolbarSize() =
        TypedValue.complexToDimensionPixelSize(
            TypedValue().apply {
                context.theme.resolveAttribute(android.R.attr.actionBarSize, this, true)
            }.data,
            context.resources.displayMetrics
        )
}