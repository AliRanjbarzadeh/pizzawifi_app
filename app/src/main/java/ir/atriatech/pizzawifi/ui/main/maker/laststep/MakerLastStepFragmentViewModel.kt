package ir.atriatech.pizzawifi.ui.main.maker.laststep

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.priceFormat
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.home.maker.Maker

class MakerLastStepFragmentViewModel : BaseFragmentViewModel() {
	var mAdapter: MakerLastStepMaterialAdapter? = null
	var mRightAdapter: MakerLastStepMaterialAdapter? = null
	var mLeftAdapter: MakerLastStepMaterialAdapter? = null
	var mAdapter2: MakerLastStepProductlAdapter? = null
	var mListItems: MutableList<Product> = mutableListOf()
	var mItem = ObservableField<Maker>()
	var mQty = ObservableField(1)
	var mPrice = ObservableField(0)
	var isEmptyProducts = ObservableBoolean(true)
	var priceFormat = ObservableField("")
	var name: String = ""
	var isHalf: Boolean = false
	var isShowRight = ObservableBoolean(true)
	var isShowLeft = ObservableBoolean(true)

	init {
		component.inject(this)
	}

	fun calculatePrice() {
		var price = mItem.get()!!.basePrice
		mItem.get()!!.breads.find { it.isSelected }?.let { bread ->
			price += bread.price
			bread.steps.forEach { step ->
				var stepPrice = 0
				if (step.qty > 0) {
					step.materials.forEach { material ->
						when (material.qty) {
							1 -> {
								stepPrice += material.prices.get(step.qty.toString())?.price ?: 0
								d(material.prices.get(step.qty.toString())?.price ?: 0, "MaterialPriceNormal")
							}
							2 -> {
								stepPrice += material.prices.get("extra")?.price ?: 0
								d(material.prices.get("extra")?.price ?: 0, "MaterialPriceExtra")
							}
						}
					}
					stepPrice -= (stepPrice.toFloat() * step.percent[step.qty - 1] / 100).toInt()
					d(stepPrice, "MaterialPriceStep${step.id}")
				}

				var stepRightPrice = 0
				if (step.rightQty > 0) {
					step.rightMaterials.forEach { rightMaterial ->
						when (rightMaterial.qty) {
							1 -> stepRightPrice += rightMaterial.prices.get(step.rightQty.toString())?.price ?: 0
							2 -> stepRightPrice += rightMaterial.prices.get("extra")?.price ?: 0
						}
					}
					stepRightPrice -= (stepRightPrice.toFloat() * step.percent[step.rightQty - 1] / 100).toInt()
				}

				var stepLeftPrice = 0
				if (step.leftQty > 0) {
					step.leftMaterials.forEach { leftMaterial ->
						when (leftMaterial.qty) {
							1 -> stepLeftPrice += leftMaterial.prices.get(step.leftQty.toString())?.price ?: 0
							2 -> stepLeftPrice += leftMaterial.prices.get("extra")?.price ?: 0
						}
					}
					stepLeftPrice -= (stepLeftPrice.toFloat() * step.percent[step.leftQty - 1] / 100).toInt()
				}

				price += stepPrice + (stepRightPrice / 2) + (stepLeftPrice / 2)
			}
		}
		d(price, "MakerPriceLast")
		mPrice.set(price)
		priceFormat.set((mQty.get()!! * mPrice.get()!!).priceFormat())
	}
}
