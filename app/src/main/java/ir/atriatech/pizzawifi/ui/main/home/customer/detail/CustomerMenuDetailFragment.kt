package ir.atriatech.pizzawifi.ui.main.home.customer.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.app.Ext
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.base.fragment.baseListObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MyItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentCustomerMenuDetailBinding
import ir.atriatech.pizzawifi.entities.customerMenu.Comment
import ir.atriatech.pizzawifi.entities.home.maker.Bread
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.entities.pizza.PizzaMaterial
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import ir.atriatech.pizzawifi.ui.main.profile.pizza.detail.PizzaDetailMaterialAdapter


class CustomerMenuDetailFragment : BaseFragment() {
    lateinit var binding: FragmentCustomerMenuDetailBinding
    private lateinit var mViewModel: CustomerMenuDetailFragmentViewModel
    private var mTransitionName = ""

    init {
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(CustomerMenuDetailFragmentViewModel::class.java)

        try {
            arguments?.getParcelable<Pizza>(ARG_STRING_1)?.let { mViewModel.mItem = it }
            arguments?.getString(ARG_STRING_2)?.let { mTransitionName = it }
            mViewModel.mItem.pizzaMaterials =
                gson.fromJson<Bread>(mViewModel.mItem.serverMaterials, Bread::class.java)
        } catch (e: Exception) {
        }
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_customer_menu_detail,
            container,
            false
        )

        binding.lifecycleOwner = this
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			binding.imgProduct.transitionName = mTransitionName
//			sharedElementEnterTransition =
//				TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
//		}

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDataBase.shopDao().findPizzaById(mViewModel.mItem.id).observeForever {
            if (it != null) {
                if (mViewModel.mItem.shopCount != it.qty)
                    mViewModel.mItem.shopCount = it.qty
            } else {
                mViewModel.mItem.shopCount = 0
            }
        }

        if (mViewModel.mItem.image.isNotEmpty()) {
            imageLoader.load(
                mViewModel.mItem.image.getImageUri(isCustomSize = true, size = "2x"),
                binding.imgProduct
            )
        }

        //Set text animation
        binding.txtCount.inAnimation = AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_in)
        binding.txtCount.outAnimation = AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_out)

        //Increase & Decrease
        binding.btnAdd.setOnClickListener {
            if (mViewModel.mItem.entity == 0)
                return@setOnClickListener

            val shopCartItem = ShopCartItem()
            shopCartItem.apply {
                productID = mViewModel.mItem.id
                name = mViewModel.mItem.name
                price = mViewModel.mItem.price
                pizza = 1
                materials = gson.toJson(mViewModel.mItem.serverMaterials)
                qty = 1
            }
            appDataBase.shopDao().saveItem(shopCartItem)
        }
        binding.btnIncrease.setOnClickListener {
            if (mViewModel.mItem.entity == 0)
                return@setOnClickListener

            val shopCartItem = appDataBase.shopDao().findPizzaByIdMainThread(mViewModel.mItem.id)
            shopCartItem.qty++
            appDataBase.shopDao().updateItem(shopCartItem)
        }
        binding.btnDecrease.setOnClickListener {
            if (mViewModel.mItem.shopCount == 1) {
                appDataBase.shopDao().deletePizzaById(mViewModel.mItem.id)
                return@setOnClickListener
            }
            val shopCartItem = appDataBase.shopDao().findPizzaByIdMainThread(mViewModel.mItem.id)
            shopCartItem.qty--
            appDataBase.shopDao().updateItem(shopCartItem)
        }

        binding.fabComment.setOnClickListener {
            val arg = extraArguments(mViewModel.mItem.id, ARG_STRING_1)
            navTo(R.id.addCommentFragment, arg = arg)
        }
        setMaterialAdapter()

        if (mViewModel.getData(limit, offset)) {
            setCommentAdapter()
        }

        binding.mainNested.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val mLayoutManager = binding.rvComments.layoutManager as LinearLayoutManager?
            v?.let {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1)
                            .getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY
                    ) {
                        val visibleItemCount = mLayoutManager?.getChildCount() ?: 0;
                        val totalItemCount = mLayoutManager?.getItemCount() ?: 0;
                        val pastVisiblesItems = mLayoutManager?.findFirstVisibleItemPosition() ?: 0;
                        if (!mViewModel.isEnd && !mViewModel.isLoadMore) {
                            if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                                mViewModel.isLoadMore = true
                                offset += limit
                                mViewModel.getData(limit, offset)
                            }
                        }
                    }
                }
            }
        }
    }

    fun setMaterialAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = PizzaDetailMaterialAdapter(object : RecyclerViewTools {})

            val pizzaMaterials = mutableListOf<PizzaMaterial>()
            pizzaMaterials.add(
                PizzaMaterial(
                    id = mViewModel.mItem.pizzaMaterials.materialId,
                    name = mViewModel.mItem.pizzaMaterials.name,
                    image = mViewModel.mItem.pizzaMaterials.icon
                )
            )
            mViewModel.mItem.pizzaMaterials.steps.forEach { step ->
                step.materials.forEach { material ->
                    pizzaMaterials.add(
                        PizzaMaterial(
                            id = material.materialId,
                            name = material.name,
                            image = material.icon
                        )
                    )
                }
            }
            mViewModel.mAdapter!!.addToList(pizzaMaterials)
        }
        binding.rvMaterials.setHasFixedSize(true)
        binding.rvMaterials.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvMaterials.adapter = mViewModel.mAdapter
    }

    private fun setCommentAdapter() {
        if (mViewModel.mAdapter2 == null) {
            mViewModel.mAdapter2 = CommentAdapter()
            mViewModel.mAdapter2!!.addToList(mViewModel.mListItems)
        } else if (mViewModel.isLoadMore) {
            mViewModel.isLoadMore = false
            mViewModel.mListItems.addAll(mViewModel.mLoadMoreItems)
            mViewModel.mAdapter2!!.addToList(mViewModel.mLoadMoreItems)
            mViewModel.mLoadMoreItems.clear()
        }

        try {
            binding.rvComments.removeItemDecorationAt(0)
        } catch (e: Exception) {
        }

        binding.rvComments.setHasFixedSize(false)
        binding.rvComments.isNestedScrollingEnabled = false
        binding.rvComments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.addItemDecoration(
            MyItemDecoration(
                findColor(R.color.colorAA53),
                dp2px(1)
            )
        )
        binding.rvComments.adapter = mViewModel.mAdapter2
    }

    private fun mObserver() {
        baseListObserver(
            this,
            mViewModel.mObserver,
            object : RequestConnectionResult<MutableList<Comment>> {
                override fun onProgress(loading: Boolean) {
                    if (!mViewModel.isLoadMore) {
                        setProgressView(binding.mainView, loading, 2)
                    }
                }

                override fun onSuccess(data: MutableList<Comment>) {
                    if (!mViewModel.isLoadMore) {
                        mViewModel.mListItems = data
                        mViewModel.isEmptyView.set(data.size == 0)
                        mViewModel.isShowContent.set(data.size != 0)
                        mViewModel.isComment.set(data.size != 0)
                    } else if (data.isNotEmpty()) {
                        mViewModel.mLoadMoreItems = data
                    }
                    mViewModel.isEnd = limit > data.size
                    setCommentAdapter()
                }

                override fun onFailed(errorMessage: String) {
                    if (mViewModel.isLoadMore) {
                        offset -= limit
                        mViewModel.isLoadMore = false
                    }
                    mViewModel.getData(limit, offset)
                }
            },
            4
        )
    }
}