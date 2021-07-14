package osama.com.angryportscanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import osama.com.angryportscanner.scanner.MacAddress
import osama.com.angryportscanner.util.inet4AddressFromInt
import osama.com.angryportscanner.util.maskWith
import java.net.Inet4Address

@Entity
data class Network(
    @PrimaryKey(autoGenerate = true) val networkId: Long,
    val baseIp: Inet4Address,
    val mask: Short,
    val scanId: Long,
    val interfaceName: String,
    val bssid: MacAddress?,
    val ssid: String?
) {

    companion object {
        fun from(
            ip: Inet4Address,
            mask: Short,
            scanId: Long,
            interfaceName: String,
            bssid: MacAddress?,
            ssid: String?
        ): Network {
            return Network(0, ip.maskWith(mask), mask, scanId, interfaceName, bssid, ssid)
        }
    }

    fun enumerateAddresses(): Sequence<Inet4Address> {
        return generateSequence(0) {
            val next = it + 1
            if (next < networkSize) next else null
        }
            .map { baseIp.hashCode() + it }
            .map { inet4AddressFromInt("", it) }
    }

    fun containsAddress(host: Inet4Address): Boolean {
        return this.baseIp.maskWith(mask) == host.maskWith(mask)
    }

    val networkSize get() = 2.shl(32 - mask.toInt() - 1)

}