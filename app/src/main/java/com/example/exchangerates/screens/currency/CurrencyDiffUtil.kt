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

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].ID != newList[newItemPosition].ID -> {
                false
            }
            oldList[oldItemPosition].Name != newList[newItemPosition].Name -> {
                false
            }
            oldList[oldItemPosition].CharCode != newList[newItemPosition].CharCode -> {
                false
            }
            oldList[oldItemPosition].Name != newList[newItemPosition].Name -> {
                false
            }
            oldList[oldItemPosition].Nominal != newList[newItemPosition].Nominal -> {
                false
            }
            oldList[oldItemPosition].Previous != newList[newItemPosition].Previous -> {
                false
            }
            oldList[oldItemPosition].Value != newList[newItemPosition].Value -> {
                false
            }
            else -> true
        }
    }

}