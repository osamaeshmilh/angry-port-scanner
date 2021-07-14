package osama.com.angryportscanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import osama.com.angryportscanner.scanner.MacAddress
import java.net.Inet4Address

@Entity
data class Device(
    @PrimaryKey(autoGenerate = true) val deviceId: Long,
    val networkId: Long,
    val ip: Inet4Address,
    val deviceName: String?,
    val hwAddress: MacAddress?,
    val isScanningDevice: Boolean = false
)