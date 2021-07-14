package osama.com.angryportscanner.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import osama.com.angryportscanner.model.Device
import osama.com.angryportscanner.model.DeviceWithName
import osama.com.angryportscanner.scanner.MacAddress
import java.net.Inet4Address

@Dao
interface DeviceDao {
    @Query("Select * FROM DeviceWithName WHERE networkId = :networkId ORDER BY ip ASC")
    fun getAll(networkId: Long): LiveData<List<DeviceWithName>>


    @Query("SELECT * FROM Device WHERE networkId = :networkId")
    fun getAllNow(networkId: Long): List<Device>

    @Insert
    fun insert(device: Device): Long

    @Transaction
    fun upsert(device: Device): Long {
        val existingDevice = getByAddressInNetwork(device.ip, device.networkId)
        return if (existingDevice == null) {
            insert(device)
        } else {
            update(device.copy(deviceId = existingDevice.deviceId))
            existingDevice.deviceId
        }
    }

    @Transaction
    fun upsertName(networkId: Long, ip: Inet4Address, name: String, allowNew: Boolean = true) {
        val existingDevice = getByAddressInNetwork(ip, networkId)
        if (existingDevice != null) {
            updateServiceName(existingDevice.deviceId, name)
        } else if (allowNew) {
            insert(Device(0, networkId, ip, name, null))
        }
    }

    @Transaction
    fun upsertHwAddress(
        networkId: Long,
        ip: Inet4Address,
        hwAddress: MacAddress,
        allowNew: Boolean
    ) {
        val existingDevice = getByAddressInNetwork(ip, networkId)
        if (existingDevice != null) {
            updateHwAddress(existingDevice.deviceId, hwAddress)
        } else if (allowNew) {
            insert(Device(0, networkId, ip, null, hwAddress))
        }
    }

    @Update
    fun update(device: Device)

    @Query("SELECT * FROM DeviceWithName WHERE deviceId = :id")
    fun getById(id: Long): LiveData<DeviceWithName>

    @Query("SELECT * FROM DEVICE WHERE deviceId = :id")
    fun getByIdNow(id: Long): Device

    @Query("SELECT * FROM Device WHERE ip = :ip AND networkId = :networkId")
    fun getByAddressInNetwork(ip: Inet4Address, networkId: Long): Device?

    @Query("SELECT * FROM Device WHERE ip = :ip AND networkId IN (SELECT networkId FROM Network WHERE scanId = :scanId)")
    fun getByAddress(ip: Inet4Address, scanId: Long): Device?

    @Query("SELECT * FROM device WHERE networkId IN (SELECT networkId FROM Network WHERE ssid=:ssid and bssid= :bssid and baseIp = :baseIp)")
    fun getDevicesInPreviousScans(
        ssid: String?,
        bssid: MacAddress?,
        baseIp: Inet4Address
    ): List<Device>


    @Query("UPDATE Device SET hwAddress = :hwAddress WHERE deviceId = :deviceId")
    fun updateHwAddress(deviceId: Long, hwAddress: MacAddress)

    @Query("UPDATE Device SET deviceName = :deviceName WHERE deviceId = :deviceId")
    fun updateServiceName(deviceId: Long, deviceName: String?)

    @Transaction
    fun insertIfNew(networkId: Long, ip: Inet4Address): Long {
        val existingAddress = getByAddressInNetwork(ip, networkId)
            ?: return insert(Device(0, networkId, ip, null, null))
        return existingAddress.deviceId
    }
}