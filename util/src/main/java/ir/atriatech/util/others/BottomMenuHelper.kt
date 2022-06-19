package ir.atriatech.util.others

import android.graphics.Typeface
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomMenuHelper(private var bottomNavigationView: BottomNavigationView) {
    fun changeFont(typeface: Typeface) {
        for (i in 0 until bottomNavigationView.menu.size()) {
            val menuItemView =
                    bottomNavigationView.findViewById<View?>(bottomNavigationView.menu.getItem(i).itemId)

            val menuLabelSmall =
                    menuItemView?.findViewById<AppCompatTextView?>(com.google.android.material.R.id.smallLabel)
            menuLabelSmall?.typeface = typeface

            val menuLabelLarge =
                    menuItemView?.findViewById<AppCompatTextView?>(com.google.android.material.R.id.largeLabel)
            menuLabelLarge?.typeface = typeface
        }
    }
}