package com.example.exchangerates.screens.currency

import androidx.recyclerview.widget.DiffUtil
import com.example.exchangerates.model.CurrencyItem

class CurrencyDiffUtil (
    private val oldList: List<CurrencyItem>,
    private val newList: List<CurrencyItem>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].ID == newList[newItemPosition].ID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]

}