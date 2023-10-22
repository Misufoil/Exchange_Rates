package com.example.exchangerates.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangerates.R
import com.example.exchangerates.databinding.CurrencyItemLayoutBinding
import com.example.exchangerates.model.CurrencyItem

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    class CurrencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = CurrencyItemLayoutBinding.bind(view)
        fun bind(item: CurrencyItem) {
            binding.apply {
                tvName.text = item.Name
                tvCharCode.text = item.CharCode
                val difference = item.Value - item.Previous

                tvValue.text = String.format("%.4f", item.Value / item.Nominal) + " ₽"

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

    private val differCallBack = object : DiffUtil.ItemCallback<CurrencyItem>() {
        override fun areItemsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
            return oldItem.ID == newItem.ID
        }

        override fun areContentsTheSame(oldItem: CurrencyItem, newItem: CurrencyItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

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
        val currency = differ.currentList[position]
        holder.bind(currency)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(currency) }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((CurrencyItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (CurrencyItem) -> Unit) {
        onItemClickListener = listener
    }
}