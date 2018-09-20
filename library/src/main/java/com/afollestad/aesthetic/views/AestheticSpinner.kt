/*
 * Licensed under Apache-2.0
 *
 * Designed and developed by Aidan Follestad (@afollestad)
 */
package com.afollestad.aesthetic.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSpinner
import com.afollestad.aesthetic.Aesthetic.Companion.get
import com.afollestad.aesthetic.ColorIsDarkState
import com.afollestad.aesthetic.utils.distinctToMainThread
import com.afollestad.aesthetic.utils.resId
import com.afollestad.aesthetic.utils.setTintAuto
import com.afollestad.aesthetic.utils.subscribeTo
import com.afollestad.aesthetic.utils.watchColor
import io.reactivex.Observable.combineLatest
import io.reactivex.disposables.Disposable

/** @author Aidan Follestad (afollestad) */
class AestheticSpinner(
  context: Context,
  attrs: AttributeSet? = null
) : AppCompatSpinner(context, attrs) {

  private var subscription: Disposable? = null
  private var backgroundResId: Int = 0

  init {
    if (attrs != null) {
      backgroundResId = context.resId(attrs, android.R.attr.background)
    }
  }

  private fun invalidateColors(state: ColorIsDarkState) =
    setTintAuto(state.color, true, state.isDark)

  override fun onAttachedToWindow() {
    super.onAttachedToWindow()

    subscription = combineLatest(
        watchColor(
            context,
            backgroundResId,
            get().colorAccent()
        ),
        get().isDark,
        ColorIsDarkState.creator()
    )
        .distinctToMainThread()
        .subscribeTo(::invalidateColors)
  }

  override fun onDetachedFromWindow() {
    subscription?.dispose()
    super.onDetachedFromWindow()
  }
}
