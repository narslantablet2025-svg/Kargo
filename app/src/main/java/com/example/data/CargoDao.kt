package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CargoDao {
    @Query("SELECT * FROM cargo_items ORDER BY addedTimestamp DESC")
    fun getAllCargos(): Flow<List<CargoItem>>

    @Query("SELECT * FROM cargo_items WHERE isArrived = 0 ORDER BY addedTimestamp DESC")
    fun getActiveCargos(): Flow<List<CargoItem>>

    @Query("SELECT * FROM cargo_items WHERE isArrived = 1 ORDER BY arrivedTimestamp DESC, addedTimestamp DESC")
    fun getArrivedCargos(): Flow<List<CargoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCargo(cargo: CargoItem): Long

    @Update
    suspend fun updateCargo(cargo: CargoItem)

    @Delete
    suspend fun deleteCargo(cargo: CargoItem)

    @Query("DELETE FROM cargo_items WHERE id = :id")
    suspend fun deleteCargoById(id: Int)

    // Marketplace Presets
    @Query("SELECT * FROM marketplace_presets ORDER BY name ASC")
    fun getAllMarketplaces(): Flow<List<MarketplacePreset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketplace(preset: MarketplacePreset): Long

    @Delete
    suspend fun deleteMarketplace(preset: MarketplacePreset)

    // Address Presets
    @Query("SELECT * FROM address_presets ORDER BY name ASC")
    fun getAllAddresses(): Flow<List<AddressPreset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(preset: AddressPreset): Long

    @Delete
    suspend fun deleteAddress(preset: AddressPreset)

    // Carrier Presets
    @Query("SELECT * FROM carrier_presets ORDER BY name ASC")
    fun getAllCarriers(): Flow<List<CarrierPreset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCarrier(preset: CarrierPreset): Long

    @Delete
    suspend fun deleteCarrier(preset: CarrierPreset)
}
