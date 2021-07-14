package osama.com.angryportscanner.model

import androidx.room.Entity

@Entity(primaryKeys = ["name", "mac"])
data class MacVendor(val name: String, val mac: String)