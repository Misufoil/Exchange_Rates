package com.example.exchangerates.screens.detail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.exchangerates.R
import com.example.exchangerates.databinding.FragmentDetailBinding
import com.example.exchangerates.model.CurrencyItem

class DetailFragment : Fragment(), MenuProvider {
    private var mBinding: FragmentDetailBinding? = null
    private val binding get() = mBinding!!
    lateinit var currentCurrency: CurrencyItem


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentDetailBinding.inflate(layoutInflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentCurrency = arguments?.getSerializable("currency", CurrencyItem::class.java)!!
        init()
    }

    private fun init() {
        binding.apply {
            tvName.text = currentCurrency.Name
            tvCharCode.text = currentCurrency.CharCode
            tvValue.text = currentCurrency.Value.toString()
            tilSecond.hint = "Сумма ${currentCurrency.CharCode}"

            etFirst.setOnKeyListener { _, _, _ ->
                convertToAnother(currentCurrency.Value, currentCurrency.Nominal)
                false
            }

            etSecond.setOnKeyListener { _, _, _ ->
                convertToRub(currentCurrency.Value, currentCurrency.Nominal)
                false
            }
        }

        val menuHost: MenuHost = requireActivity()
        requireActivity().title = "Подробнее"
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun convertToAnother(value: Double, nominal: Int) {
        val rubleAmount = binding.etFirst.text.toString().toDoubleOrNull()
        if (rubleAmount != null) {
            val anotherAmount = rubleAmount / (value / nominal)
            binding.etSecond.setText(String.format("%.4f", anotherAmount))
        }

    }

    private fun convertToRub(value: Double, nominal: Int) {
        val anotherAmount = binding.etSecond.text.toString().toDoubleOrNull()
        if (anotherAmount != null) {
            val rubleAmount = anotherAmount * (value / nominal)
            binding.etFirst.setText(String.format("%.4f", rubleAmount))
        }

    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.detail_options_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.item_close -> {
                findNavController().popBackStack()
                true
            }

            else -> false
        }
    }


}