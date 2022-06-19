package ir.atriatech.extensions.base.fragment

import android.view.View
import com.google.android.material.appbar.AppBarLayout
import ir.atriatech.core.base.BaseCoreFragment

fun BaseCoreFragment.fadeColapsing(appBarLayout: AppBarLayout,viewToFade:View) {
    appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, p1 ->
        when {
            p1 >= -100 -> viewToFade.alpha = 1f
            p1 < -100 && p1 > -200 -> viewToFade.alpha = 0.8f
            p1 < -200 && p1 > -300 -> viewToFade.alpha = 0.6f
            p1 < -300 && p1 > -500 -> viewToFade.alpha = 0.45f
            p1 < -500 -> viewToFade.alpha = 0.2f
        }
    })
}