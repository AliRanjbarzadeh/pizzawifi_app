package ir.atriatech.util.others

import android.os.Build
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.d

class FakeAdapter(
	@LayoutRes val item_layout: Int, size: Int = 1,
	private val recyclerViewItemClick: RecyclerViewTools,
	val transitionName: String = "",
	@IdRes val transitionId: Int? = null
) : RecyclerView.Adapter<FakeAdapter.Holder>() {
	val list = mutableListOf<Int>()

	init {
		var i = 0

		while (i < size) {
			i++
			list.add(i)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
		val layoutInflater = LayoutInflater.from(parent.context)
		val view =
			layoutInflater.inflate(item_layout, parent, false)
		return Holder(
			view, recyclerViewItemClick
		)
	}

	override fun getItemCount(): Int {
		return list.size
	}

	override fun onBindViewHolder(holder: Holder, position: Int) {
		holder.bindUI(list[position])
	}

	inner class Holder(val view: View, private val recyclerViewItemClick: RecyclerViewTools) :
		RecyclerView.ViewHolder(view) {

		fun bindUI(item: Int) {
			view.setOnClickListener {
				recyclerViewItemClick.onItemClick(adapterPosition, view, item)
			}
			if (transitionName.isNotEmpty()) {
				transitionId?.let {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						try {
							view.findViewById<AppCompatImageView>(transitionId).transitionName = transitionName + adapterPosition
							d(transitionName + adapterPosition, "test1")
						} catch (e: Exception) {
						}
					}
				}

			}
		}
	}
}