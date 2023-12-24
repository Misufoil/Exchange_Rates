package com.example.exchangerates.screens.detail

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.exchangerates.R
import com.example.exchangerates.databinding.FragmentDetailBinding
import com.example.exchangerates.model.CurrencyItem
import com.example.exchangerates.screens.detail.util.ChartConfigurator
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class DetailFragment : Fragment(), MenuProvider {
    private var mBinding: FragmentDetailBinding? = null
    private val binding get() = mBinding!!
    private lateinit var currentCurrency: CurrencyItem
    private var mChart: LineChart? = null
    private val viewModel by viewModels<DetailViewModel> {
        DetailViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentCurrency = arguments?.getSerializable("currency", CurrencyItem::class.java)!!
        init()
        initListeners()
        initObserves()
    }

    private fun init() {
        binding.apply {
            tvName.text = currentCurrency.Name
            tvCharCode.text = currentCurrency.CharCode
            tvValue.text = "%.4f ₽".format(currentCurrency.Value / currentCurrency.Nominal)
            tilSecond.hint = "Сумма ${currentCurrency.CharCode}"

            mChart = idGraphView
            val chartConfigurator = mChart?.let { ChartConfigurator(it) }
            chartConfigurator?.configureChart()

            val selectedData = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault()))
            btnFromDate.text = selectedData
            btnToDate.text = selectedData


        }

        val menuHost: MenuHost = requireActivity()
        requireActivity().title = "Подробнее"
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initListeners() {
        binding.apply {
            etFirst.setOnKeyListener { _, _, _ ->
                val text = etFirst.text.toString()
                viewModel.convertToAnother(currentCurrency.Value, currentCurrency.Nominal, text)
                false
            }

            etSecond.setOnKeyListener { _, _, _ ->
                val text = etSecond.text.toString()
                viewModel.convertToRub(currentCurrency.Value, currentCurrency.Nominal, text)
                false
            }

            btnFromDate.setOnClickListener {
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {
                        val date = bundle.getString("SELECTED_DATE")
                        btnFromDate.text = date
                    }
                }

                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
            }

            btnToDate.setOnClickListener {
                val datePickerFragment = DatePickerFragment()
                val supportFragmentManager = requireActivity().supportFragmentManager

                supportFragmentManager.setFragmentResultListener(
                    "REQUEST_KEY",
                    viewLifecycleOwner
                ) { resultKey, bundle ->
                    if (resultKey == "REQUEST_KEY") {
                        val date = bundle.getString("SELECTED_DATE")
                        btnToDate.text = date
                    }
                }

                datePickerFragment.show(supportFragmentManager, "DatePickerFragment")
            }

            btnSetGraph.setOnClickListener {
                viewModel.loadGraphPoints(
                    currentCurrency.ID,
                    btnFromDate.text.toString(),
                    btnToDate.text.toString()
                )
            }
        }
    }

    private fun initObserves() {
        viewModel.convertToAnotherResult.observe(this) {
            binding.etSecond.setText(it)
        }

        viewModel.convertToAnotherRub.observe(this) {
            binding.etFirst.setText(it)
        }

        viewModel.graphPoints.observe(this) { GraphPoints ->
            if (GraphPoints.isNotEmpty()) {
                val values =
                    GraphPoints.map {
                        Entry(
                            it.date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                                .toFloat(), it.value.toFloat(), it.date
                        )
                    }
                val set1 = LineDataSet(values, "DataSet 1")
                set1.color = Color.rgb(95, 226, 156)
                set1.lineWidth = 2f
                set1.setDrawCircles(true)
                set1.setDrawValues(true)

                set1.valueTextSize = 8f

                val dataSets: ArrayList<ILineDataSet> = ArrayList()
                dataSets.add(set1)

                val data = LineData(dataSets)

                mChart?.data = data
                mChart?.invalidate()
            }
        }
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.detail_options_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.item_save -> {
                viewModel.addFavoriteCurrency(currentCurrency) {}
                view?.let {
                    Snackbar.make(it, "Currency saved successfully", Snackbar.LENGTH_SHORT).show()
                }
                true
            }

            R.id.item_close -> {
                findNavController().popBackStack()
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}