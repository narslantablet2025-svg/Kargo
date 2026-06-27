package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CargoItem
import com.example.data.CargoRepository
import com.example.data.MarketplacePreset
import com.example.data.AddressPreset
import com.example.data.CarrierPreset
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CargoViewModel(private val repository: CargoRepository) : ViewModel() {

    val activeCargos: StateFlow<List<CargoItem>> = repository.activeCargos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val arrivedCargos: StateFlow<List<CargoItem>> = repository.arrivedCargos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val marketplaces: StateFlow<List<MarketplacePreset>> = repository.allMarketplaces
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val addresses: StateFlow<List<AddressPreset>> = repository.allAddresses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val carriers: StateFlow<List<CarrierPreset>> = repository.allCarriers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            try {
                // marketplaces.first() retrieves the first emitted list
                val listMarketplaces = repository.allMarketplaces.first()
                if (listMarketplaces.isEmpty()) {
                    val defaults = listOf("Trendyol", "Hepsiburada", "Amazon", "n11", "Dolap", "Gardrops")
                    defaults.forEach { repository.insertMarketplace(MarketplacePreset(name = it)) }
                }
            } catch (e: Exception) {
                // Ignore
            }
            try {
                val listAddresses = repository.allAddresses.first()
                if (listAddresses.isEmpty()) {
                    val defaults = listOf("Ev", "İş / Ofis", "Okul", "Yazlık")
                    defaults.forEach { repository.insertAddress(AddressPreset(name = it)) }
                }
            } catch (e: Exception) {
                // Ignore
            }
            try {
                val listCarriers = repository.allCarriers.first()
                if (listCarriers.isEmpty()) {
                    val defaults = listOf("Trendyol Express", "Aras Kargo", "Yurtiçi Kargo", "MNG Kargo", "Sürat Kargo", "Kolay Gelsin", "PTT Kargo")
                    defaults.forEach { repository.insertCarrier(CarrierPreset(name = it)) }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun addMarketplacePreset(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.insertMarketplace(MarketplacePreset(name = name.trim()))
            }
        }
    }

    fun deleteMarketplacePreset(preset: MarketplacePreset) {
        viewModelScope.launch {
            repository.deleteMarketplace(preset)
        }
    }

    fun addAddressPreset(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.insertAddress(AddressPreset(name = name.trim()))
            }
        }
    }

    fun deleteAddressPreset(preset: AddressPreset) {
        viewModelScope.launch {
            repository.deleteAddress(preset)
        }
    }

    fun addCarrierPreset(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.insertCarrier(CarrierPreset(name = name.trim()))
            }
        }
    }

    fun deleteCarrierPreset(preset: CarrierPreset) {
        viewModelScope.launch {
            repository.deleteCarrier(preset)
        }
    }

    fun addCargo(
        productName: String,
        address: String,
        cargoCarrier: String,
        marketplace: String
    ) {
        viewModelScope.launch {
            val item = CargoItem(
                productName = productName.trim(),
                address = address.trim(),
                cargoCarrier = cargoCarrier.trim(),
                marketplace = marketplace.trim(),
                isArrived = false
            )
            repository.insertCargo(item)
        }
    }

    fun toggleArrivalStatus(cargoItem: CargoItem) {
        viewModelScope.launch {
            val updated = cargoItem.copy(
                isArrived = !cargoItem.isArrived,
                arrivedTimestamp = if (!cargoItem.isArrived) System.currentTimeMillis() else null
            )
            repository.updateCargo(updated)
        }
    }

    fun deleteCargo(cargoItem: CargoItem) {
        viewModelScope.launch {
            repository.deleteCargo(cargoItem)
        }
    }
}

class CargoViewModelFactory(private val repository: CargoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CargoViewModel::class.java)) {
            return CargoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
