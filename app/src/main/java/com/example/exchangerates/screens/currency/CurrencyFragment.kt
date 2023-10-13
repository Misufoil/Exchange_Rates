package com.example.exchangerates.screens.currency

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.exchangerates.R
import com.example.exchangerates.databinding.FragmentCurrencyBinding


class CurrencyFragment : Fragment() {
    private var mBinding: FragmentCurrencyBinding? = null
    private val binding get() = mBinding!!
    private val currencyAdapter by lazy { CurrencyAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCurrencyBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val viewModel = ViewModelProvider(this)[CurrencyViewModel::class.java]
        binding.rvCurrency.adapter = currencyAdapter
        viewModel.getCurrencies()
        viewModel.myCurrencyList.observe(viewLifecycleOwner) { list ->
            val sortedList = list.body()!!.Valute.values.toList().sortedBy { it.CharCode.lowercase() }
            currencyAdapter.setList(sortedList)
        }
        currencyAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("currency", it)

            findNavController().navigate(
                R.id.action_currencyFragment_to_detailFragment,
                bundle
            )
        }
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}