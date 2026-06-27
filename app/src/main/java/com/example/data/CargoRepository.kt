package com.example.data

import kotlinx.coroutines.flow.Flow

class CargoRepository(private val cargoDao: CargoDao) {
    val activeCargos: Flow<List<CargoItem>> = cargoDao.getActiveCargos()
    val arrivedCargos: Flow<List<CargoItem>> = cargoDao.getArrivedCargos()

    suspend fun insertCargo(cargo: CargoItem) {
        cargoDao.insertCargo(cargo)
    }

    suspend fun updateCargo(cargo: CargoItem) {
        cargoDao.updateCargo(cargo)
    }

    suspend fun deleteCargo(cargo: CargoItem) {
        cargoDao.deleteCargo(cargo)
    }

    suspend fun deleteCargoById(id: Int) {
        cargoDao.deleteCargoById(id)
    }

    // Marketplace Presets
    val allMarketplaces: Flow<List<MarketplacePreset>> = cargoDao.getAllMarketplaces()

    suspend fun insertMarketplace(preset: MarketplacePreset) {
        cargoDao.insertMarketplace(preset)
    }

    suspend fun deleteMarketplace(preset: MarketplacePreset) {
        cargoDao.deleteMarketplace(preset)
    }

    // Address Presets
    val allAddresses: Flow<List<AddressPreset>> = cargoDao.getAllAddresses()

    suspend fun insertAddress(preset: AddressPreset) {
        cargoDao.insertAddress(preset)
    }

    suspend fun deleteAddress(preset: AddressPreset) {
        cargoDao.deleteAddress(preset)
    }

    // Carrier Presets
    val allCarriers: Flow<List<CarrierPreset>> = cargoDao.getAllCarriers()

    suspend fun insertCarrier(preset: CarrierPreset) {
        cargoDao.insertCarrier(preset)
    }

    suspend fun deleteCarrier(preset: CarrierPreset) {
        cargoDao.deleteCarrier(preset)
    }
}
