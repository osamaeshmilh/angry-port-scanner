package osama.com.angryportscanner.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import osama.com.angryportscanner.model.Port

@Dao
interface PortDao {
    @Insert
    fun insert(port: Port): Long

    @Transaction
    suspend fun upsert(port: Port): Long {
        val portFromDB = getPortFromNumber(port.deviceId, port.port) ?: return insert(port)

        update(Port(portFromDB.portId, port.port, port.protocol, port.deviceId))
        return portFromDB.portId
    }

    @Update
    fun update(port: Port)

    @Query("SELECT * FROM Port WHERE deviceId = :deviceId AND port = :port")
    fun getPortFromNumber(deviceId: Long, port: Int): Port?


    @Query("SELECT * FROM Port WHERE deviceId = :deviceId")
    fun getAllForDevice(deviceId: Long): LiveData<List<Port>>
}