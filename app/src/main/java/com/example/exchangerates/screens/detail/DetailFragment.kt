package com.example.exchangerates.screens.detail

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.exchangerates.R
import com.example.exchangerates.databinding.FragmentDetailBinding
import com.example.exchangerates.model.CurrencyItem
import com.example.exchangerates.model.GraphPoint
import com.example.exchangerates.screens.detail.util.DateAxisValueFormatter
import com.example.exchangerates.util.PARSING_URL
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DetailFragment : Fragment(), MenuProvider {
    private var mBinding: FragmentDetailBinding? = null
    private val binding get() = mBinding!!
    private lateinit var currentCurrency: CurrencyItem
    private lateinit var viewModel: DetailViewModel
    private val myGraphPoints = mutableListOf<GraphPoint>()
    private var mChart: LineChart? = null

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
        configureChart()
    }

    private fun init() {
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        binding.apply {
            tvName.text = currentCurrency.Name
            tvCharCode.text = currentCurrency.CharCode
            tvValue.text = "%.4f ₽".format(currentCurrency.Value / currentCurrency.Nominal)
            tilSecond.hint = "Сумма ${currentCurrency.CharCode}"



            mChart = idGraphView

            val selectedData = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            btnFromDate.text = selectedData
            btnToDate.text = selectedData

            etFirst.setOnKeyListener { _, _, _ ->
                convertToAnother(currentCurrency.Value, currentCurrency.Nominal)
                false
            }

            etSecond.setOnKeyListener { _, _, _ ->
                convertToRub(currentCurrency.Value, currentCurrency.Nominal)
                false
            }

            btnFromDate.setOnClickListener {
                //create new instance
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
                //create new instance
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
                lifecycleScope.launch(Dispatchers.IO) {
                    getGraphPoints(
                        currentCurrency.ID,
                        btnFromDate.text.toString(),
                        btnToDate.text.toString()
                    )
                }
            }
        }

        val menuHost: MenuHost = requireActivity()
        requireActivity().title = "Подробнее"
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configureChart() {
        mChart?.apply {
            // Настройка внешнего вида графика
            description.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(true)
            isDragEnabled = true
            setScaleEnabled(true)

            val textColor = MaterialColors.getColor(
                this.context, com.google.android.material.R.attr.colorOnSecondary, Color.BLACK
            )

            // Настройка оси X
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = DateAxisValueFormatter()
            xAxis.labelRotationAngle = -45f
            xAxis.setDrawGridLines(true)
            xAxis.textColor = textColor

            val oneDayInMillis = 24 * 60 * 60 * 1000
            xAxis.granularity = oneDayInMillis.toFloat()

            // Настройка оси Y
            axisRight.isEnabled = false
            axisLeft.setDrawGridLines(true)
            axisLeft.textColor = textColor

            // Отключение легенды
            legend.isEnabled = false

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e != null) {
                        val inputDate = e.data.toString()
                        val inputFormatter = DateTimeFormatter.ofPattern(
                            "EEE MMM dd HH:mm:ss 'GMT'xxx yyyy",
                            Locale.US
                        )
                        val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.US)

                        try {
                            val dateTime = LocalDateTime.parse(inputDate, inputFormatter)
                            val outputDate = dateTime.format(outputFormatter)

                            //включить описание
                            description.isEnabled = true
                            description.textSize = 12f
                            description.text = "Date: $outputDate\nValue: ${e.y}"
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onNothingSelected() {
                    // Вызывается, когда нет выбранных точек
                }
            })
        }
    }

    private suspend fun getGraphPoints(id: String, fromDate: String, toDate: String) {
        try {
            val url =
                "$PARSING_URL&UniDbQuery.VAL_NM_RQ=$id&UniDbQuery.From=$fromDate&UniDbQuery.To=$toDate"
            val document = Jsoup.connect(url).get()
            val trElements = document.select("table[class=data]").select("tr")

            myGraphPoints.clear()
            if (trElements != null) {
                for (i in 2 until trElements.size) {
                    val row = trElements[i]
                    val tdElements = row.select("td")
                    val date = tdElements[0].text()
                    val nominal = tdElements[1].text().toDouble()
                    val value = String.format(
                        Locale.US,
                        "%.4f",
                        tdElements[2].text().replace(",", ".").toDouble() / nominal
                    ).toDouble()
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

                    myGraphPoints.add(0, (GraphPoint(dateFormat.parse(date) as Date, value)))
                }

                withContext(Dispatchers.Main) {
                    buildGraph()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "За данный период нет данных.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildGraph() {
        if (myGraphPoints.isNotEmpty()) {
            val values =
                myGraphPoints.map { Entry(it.date.time.toFloat(), it.value.toFloat(), it.date) }
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