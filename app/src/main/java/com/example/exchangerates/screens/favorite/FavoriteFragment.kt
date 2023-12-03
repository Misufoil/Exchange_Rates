package com.example.exchangerates.screens.favorite

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangerates.R
import com.example.exchangerates.adapter.CurrencyAdapter
import com.example.exchangerates.databinding.FragmentFavoriteBinding
import com.google.android.material.snackbar.Snackbar

class FavoriteFragment : Fragment(), MenuProvider {
    private var mBinding: FragmentFavoriteBinding? = null
    private val binding get() = mBinding!!
    private val favoriteAdapter by lazy { CurrencyAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        val viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]
        binding.rvFavorite.adapter = favoriteAdapter

        try {
            viewModel.getFavoriteCurrency().observe(this) { list ->
                favoriteAdapter.differ.submitList(list.asReversed())
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
        }

        val menuHost: MenuHost = requireActivity()
        requireActivity().title = "ИЗБРАННОЕ (RUB)"
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        favoriteAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putSerializable("currency", it)

            findNavController().navigate(
                R.id.action_favoriteFragment_to_detailFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.START or ItemTouchHelper.END,
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currency = favoriteAdapter.differ.currentList[position]
                viewModel.deleteFavoriteCurrency(currency) {}
                Snackbar.make(view, "Successfully deleted currency", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.addFavoriteCurrency(currency) {}
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvFavorite)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.favorite_options_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.item_currency -> {
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