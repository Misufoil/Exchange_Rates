package com.example.exchangerates.screens.currency

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.exchangerates.R
import com.example.exchangerates.adapter.CurrencyAdapter
import com.example.exchangerates.databinding.FragmentCurrencyBinding
import com.example.exchangerates.util.SEARCH_NEWS_TAME_DELAY
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CurrencyFragment : Fragment(), MenuProvider {
    private var mBinding: FragmentCurrencyBinding? = null
    private val binding get() = mBinding!!
    private lateinit var viewModel: CurrencyViewModel
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
        viewModel = ViewModelProvider(this)[CurrencyViewModel::class.java]
        viewModel.initDatabase()
        binding.rvCurrency.adapter = currencyAdapter

        try {
            viewModel.getCurrenciesRetrofit()
            viewModel.myCurrencyList.observe(viewLifecycleOwner) { response ->
                val sortedList =
                    response.body()!!.Valute.values.sortedBy { it.CharCode.lowercase() }
                currencyAdapter.differ.submitList(sortedList)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

        currencyAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("currency", it)

            findNavController().navigate(
                R.id.action_currencyFragment_to_detailFragment,
                bundle
            )
        }

        val menuHost: MenuHost = requireActivity()
        requireActivity().title = "КУРС (RUB)"
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.options_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.item_favorite -> {
                findNavController().navigate(R.id.action_currencyFragment_to_favoriteFragment)
                true
            }
            R.id.search -> {
                Log.i("HOME_FRAGMENT", "NOW IN SEARCH MENU ITEM BRANCH")
                val searchView = menuItem.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    var job: Job? = null
                    override fun onQueryTextChange(query: String?): Boolean {
                        if (query != null) {
                            job?.cancel()
                            job = MainScope().launch {
                                delay(SEARCH_NEWS_TAME_DELAY)
                                searchCurrency(query)
                            }
                        }
                        return true
                    }
                })
                true
            }
            else -> false
        }
    }

    private fun searchCurrency(query: String) {
        val filteredAndSortedCurrencies = if (query.isEmpty()) {
            viewModel.myCurrencyList.value?.body()?.Valute?.values?.sortedBy { it.CharCode.lowercase() }
        } else {
            viewModel.filterAndSortCurrencies(query)
        }
        currencyAdapter.differ.submitList(filteredAndSortedCurrencies)
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}
