package ru.bashcony.followy.ui

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import kotlin.math.roundToInt

@ColorInt
internal fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

internal val Number.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).roundToInt()

internal val Number.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics
    ).roundToInt()

internal val Number.px
    get() = (this.toFloat() / Resources.getSystem().displayMetrics.density)