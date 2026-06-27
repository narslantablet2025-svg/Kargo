package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cargo_items")
data class CargoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productName: String,
    val address: String,
    val cargoCarrier: String,
    val marketplace: String,
    val isArrived: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis(),
    val arrivedTimestamp: Long? = null
)
