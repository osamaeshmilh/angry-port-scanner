package osama.com.angryportscanner.dao

import androidx.room.Dao
import androidx.room.Query
import osama.com.angryportscanner.model.MacVendor
import osama.com.angryportscanner.scanner.MacAddress

@Dao
interface MacVendorsDao {
    @Query("SELECT * FROM macvendor WHERE mac = :mac")
    fun getFromMac(mac: MacAddress): MacVendor
}