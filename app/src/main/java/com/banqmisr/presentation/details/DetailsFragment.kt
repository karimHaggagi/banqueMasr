package com.banqmisr.presentation.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.banqmisr.data.datasource.remote.api.network.NetworkState
import com.banqmisr.databinding.FragmentDetailsBinding
import com.banqmisr.domain.model.Currency
import com.banqmisr.domain.model.HistoricalDataModel
import com.banqmisr.presentation.details.adapter.HistoricalDataAdapter
import com.banqmisr.utils.Alert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private val viewModel: DetailsViewModel by activityViewModels()
    private lateinit var adapter: HistoricalDataAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDetailsBinding.inflate(inflater)

        adapter = HistoricalDataAdapter()
        binding.rvHistoricalData.adapter = adapter

        observeData()
        return binding.root
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.historicalDataStateFlow.collectLatest { response ->
                    when (response) {
                        is NetworkState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Alert.showDialog(requireContext(), response.error ?: "Error Message")

                        }

                        NetworkState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE

                        }

                        is NetworkState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            handleResponse(response.data)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.dayStateFlow.collectLatest {
                    binding.tvFromCurrency.text = it
                }
            }
        }
    }

    private fun handleResponse(data: List<HistoricalDataModel>) {
        adapter.submitList(data)
    }

}