package com.example.exchangerates.screens.currency

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangerates.R
import com.example.exchangerates.databinding.CurrencyItemLayoutBinding
import com.example.exchangerates.model.CurrencyItem

class CurrencyAdapter: RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private var currencyItems = emptyList<CurrencyItem>()

    class CurrencyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = CurrencyItemLayoutBinding.bind(view)
        fun bind(item: CurrencyItem) {
            binding.apply {
                tvName.text = item.Name
                tvCharCode.text = item.CharCode
                val difference = item.Value - item.Previous

                tvValue.text =  String.format("%.4f", item.Value / item.Nominal)

                val sign = if (difference >= 0) {
                    tvDifference.setTextColor(Color.GREEN)
                    "+"
                } else {
                    tvDifference.setTextColor(Color.RED)
                    ""
                }

                tvDifference.text = "$sign${String.format("%.4f", difference)}"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.currency_item_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currency = currencyItems[position]
        holder.bind(currency)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(currency) }
        }
    }

    override fun getItemCount(): Int {
        return currencyItems.size
    }

    fun setList(list: List<CurrencyItem>) {
        val diffUtil = CurrencyDiffUtil(currencyItems, list)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        currencyItems = list
        diffResult.dispatchUpdatesTo(this)
    }

    private var onItemClickListener: ((CurrencyItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (CurrencyItem) -> Unit) {
        onItemClickListener = listener
    }

    fun getList(): List<CurrencyItem> = currencyItems
}