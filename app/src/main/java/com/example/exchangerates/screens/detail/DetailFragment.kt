package com.example.exchangerates.screens.detail

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
import com.example.exchangerates.util.PARSING_URL
import com.google.android.material.snackbar.Snackbar
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DetailFragment : Fragment(), MenuProvider {
    private var mBinding: FragmentDetailBinding? = null
    private val binding get() = mBinding!!
    private lateinit var currentCurrency: CurrencyItem
    lateinit var viewModel: DetailViewModel
    private val myGraphPoints = mutableListOf<GraphPoint>()
    private val graphPointsLock = Object()

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
        init(view)
    }

    private fun init(view: View) {
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        binding.apply {
            tvName.text = currentCurrency.Name
            tvCharCode.text = currentCurrency.CharCode
            tvValue.text =
                String.format("%.4f", currentCurrency.Value / currentCurrency.Nominal) + " ₽"
            tilSecond.hint = "Сумма ${currentCurrency.CharCode}"

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

    private suspend fun getGraphPoints(id: String, fromDate: String, toDate: String) {
        try {
            val url =
                "$PARSING_URL&UniDbQuery.VAL_NM_RQ=$id&UniDbQuery.From=$fromDate&UniDbQuery.To=$toDate"
            // "https://www.cbr.ru/currency_base/dynamics/?UniDbQuery.Posted=True&UniDbQuery.so=1&UniDbQuery.mode=1&UniDbQuery.date_req1=&UniDbQuery.date_req2=&UniDbQuery.VAL_NM_RQ=R01060&UniDbQuery.From=10.08.2023&UniDbQuery.To=19.08.2023"

            val document = Jsoup.connect(url).get()
            val trElements = document.select("table[class=data]")
                .select("tr")


            myGraphPoints.clear()
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

                myGraphPoints.add(GraphPoint(dateFormat.parse(date) as Date, value))
            }
            withContext(Dispatchers.Main) {
                buildGraph()
            }

        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
            }
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

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
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

    private fun buildGraph() {
//            Array(100) { i ->
//                DataPoint(i.toDouble(), (i * i % 10).toDouble())
//            }
        if (myGraphPoints.isNotEmpty()) {
            myGraphPoints.sortBy { it.date }
            val series: LineGraphSeries<DataPoint> = LineGraphSeries(myGraphPoints.map {
                DataPoint(it.date, it.value)
            }.toTypedArray())

            // on below line we are adding
            // data series to our graph view.
            binding.idGraphView.addSeries(series)


            // set date label formatter
            binding.idGraphView.gridLabelRenderer.labelFormatter =
                DateAsXAxisLabelFormatter(activity);
            //binding.idGraphView.gridLabelRenderer.numHorizontalLabels = 3; // only 4 because of the space


            binding.idGraphView.animate()

            binding.idGraphView.viewport.setMinX(myGraphPoints[0].date.time.toDouble());
            binding.idGraphView.viewport.setMaxX(myGraphPoints.last().date.time.toDouble());
            binding.idGraphView.viewport.isXAxisBoundsManual = true;


// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary
            binding.idGraphView.gridLabelRenderer.setHumanRounding(false);
//
        }

//        // on below line we are setting scrollable
//        // for point graph view
//        binding.idGraphView.viewport.isScrollable = true
//
//        // on below line we are setting scalable.
//        binding.idGraphView.viewport.isScalable = true
//
//        // on below line we are setting scalable y
//        binding.idGraphView.viewport.setScalableY(true)
//
//        // on below line we are setting scrollable y
//        binding.idGraphView.viewport.setScrollableY(true)
//
//        // on below line we are setting color for series.
//        series.color = R.color.purple_200
//

    }
}