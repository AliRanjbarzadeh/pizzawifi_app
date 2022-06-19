package ir.atriatech.pizzawifi.ui.main.profile.wallet

import android.view.ViewGroup
import ir.atriatech.core.base.BaseHolder
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.kotlin.inflate
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseAdapter
import ir.atriatech.pizzawifi.databinding.ItemWallet2Binding
import ir.atriatech.pizzawifi.databinding.ItemWalletBinding
import ir.atriatech.pizzawifi.databinding.ItemWarningBinding
import ir.atriatech.pizzawifi.entities.profile.wallet.Wallet

class WalletAdapter(val recyclerViewTools: RecyclerViewTools) : BaseAdapter<Wallet>() {

    override fun getItemViewType(position: Int): Int {
        return when (list[position].type) {
            1 -> { //Increase
                R.layout.item_wallet
            }

            0 -> { //Decrease
                R.layout.item_wallet2
            }

            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<Wallet> {
        return when (viewType) {
            R.layout.item_wallet -> {
                val binding = inflate<ItemWalletBinding>(parent, viewType)

                object : BaseHolder<Wallet>(binding) {
                    override fun bindUI(item: Wallet) {
                        binding.item = item
                        binding.executePendingBindings()
                    }
                }
            }

            R.layout.item_wallet2 -> {
                val binding = inflate<ItemWallet2Binding>(parent, viewType)

                object : BaseHolder<Wallet>(binding) {
                    override fun bindUI(item: Wallet) {
                        binding.item = item
                        binding.executePendingBindings()
                    }
                }
            }

            else -> {
                val warningView = inflate<ItemWarningBinding>(parent, R.layout.item_warning)
                object : BaseHolder<Wallet>(warningView) {
                    override fun bindUI(item: Wallet) {}
                }
            }
        }
    }
}