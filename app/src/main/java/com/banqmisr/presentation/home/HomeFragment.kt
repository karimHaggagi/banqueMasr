package com.banqmisr.presentation.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.banqmisr.R
import com.banqmisr.data.datasource.remote.api.network.NetworkState
import com.banqmisr.databinding.FragmentHomeBinding
import com.banqmisr.domain.model.LatestCurrencyModel
import com.banqmisr.presentation.details.DetailsViewModel
import com.banqmisr.utils.Alert
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val detailsViewModel: DetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)

        observeData()

        binding.btnDetails.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.homeFragment) {
                detailsViewModel.getData(
                    viewModel.getSelectedItem(),
                    "USD,AUD,CAD,PLN,MXN,JOD,JPY,KWD,KYD,LAK"
                )
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment())
            }
        }
        return binding.root
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.latestCurrencyStateFlow.collectLatest { state ->
                    when (state) {
                        NetworkState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is NetworkState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if (state.data.success) {
                                handleResponseData(state.data)
                            } else {
                                Alert.showDialog(requireContext(), "Invalid Data")

                            }
                        }

                        is NetworkState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Alert.showDialog(requireContext(), state.error ?: "Error Message")

                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.toCurrencyStateFlow.collectLatest {
                    binding.etTo.setText(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.fromCurrencyStateFlow.collectLatest {
                    binding.etFrom.setText(it)
                }
            }
        }
    }


    private fun handleResponseData(data: LatestCurrencyModel) {

        binding.tvDate.text = data.date

        val fromCurrencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            data.ratesNames.keys.toList()
        )


        binding.spinnerFrom.adapter = fromCurrencyAdapter
        binding.spinnerFrom.setSelection(viewModel.fromCurrencyPosition, false)
        viewModel.setSelectedItem(data.ratesNames.toList()[0])

        binding.spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                viewModel.setSelectedFromCurrency(data.ratesNames.toList()[position])
                viewModel.setSelectedItem(data.ratesNames.toList()[position])


            } // to close the onItemSelected

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val toCurrencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            data.ratesNames.keys.toList()
        )

        binding.spinnerTo.adapter = toCurrencyAdapter
        binding.spinnerTo.setSelection(viewModel.toCurrencyPosition, false)
        binding.spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setSelectedToCurrency(data.ratesNames.toList()[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}


        }


        binding.btnSwitch.setOnClickListener {
            val toSpinnerPosition = binding.spinnerTo.selectedItemPosition
            binding.spinnerTo.setSelection(binding.spinnerFrom.selectedItemPosition)
            binding.spinnerFrom.setSelection(toSpinnerPosition)

        }

        setTextChangedListener()
    }

    private fun setTextChangedListener() {

        callbackFlow {
            binding.etFrom.doOnTextChanged { text, _, _, _ ->
                if (binding.etFrom.hasFocus()) {
                    trySend(text.toString()).isSuccess
                }
            }
            awaitClose { binding.etFrom.addTextChangedListener(null) }
        }.debounce(500)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .onEach {
                // Check if the search view is visible first, so that the callback is not fired
                // if the user has already closed the search view.
                if (it.isNotEmpty()) {
                    viewModel.setAmountFromCurrency(it)
                }
            }
            .launchIn(lifecycleScope)

        callbackFlow {
            binding.etTo.doOnTextChanged { text, _, _, _ ->
                if (binding.etTo.hasFocus()) {
                    trySend(text.toString()).isSuccess
                }
            }
            awaitClose { binding.etTo.addTextChangedListener(null) }
        }.debounce(500)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
            .onEach {
                // Check if the search view is visible first, so that the callback is not fired
                // if the user has already closed the search view.
                if (it.isNotEmpty()) {
                    viewModel.onConvertedValueChanged(it)
                }
            }
            .launchIn(lifecycleScope)

    }

}