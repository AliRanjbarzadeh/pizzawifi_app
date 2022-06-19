package ir.atriatech.pizzawifi.ui.main.category

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ir.atriatech.pizzawifi.entities.Category
import ir.atriatech.pizzawifi.ui.main.category.product.ProductFragment


class CategoryAdapter(f: Fragment, private val dataList: ArrayList<Category>) :
	FragmentStateAdapter(f) {

	//    override fun getItem(position: Int): Fragment {
//        return ProductFragment.newInstance(dataList[position])
//    }
//
//    override fun getCount(): Int {
//        return dataList.size
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        return dataList[position].name
//    }
	override fun getItemCount(): Int {
		return dataList.size
	}

	override fun createFragment(position: Int): Fragment {
		return ProductFragment.newInstance(dataList[position])
	}
}
