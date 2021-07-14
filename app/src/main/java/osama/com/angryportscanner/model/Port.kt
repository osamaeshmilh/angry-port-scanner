package osama.com.angryportscanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import osama.com.angryportscanner.model.enums.Protocol

@Entity
data class Port(
    @PrimaryKey(autoGenerate = true) val portId: Long, val port: Int,
    val protocol: Protocol,
    val deviceId: Long
) {
    val description get() = PortDescription.commonPorts.find { it.port == port }
}