package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marketplace_presets")
data class MarketplacePreset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "address_presets")
data class AddressPreset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Entity(tableName = "carrier_presets")
data class CarrierPreset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
