package osama.com.angryportscanner.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import osama.com.angryportscanner.model.Network

@Dao
interface NetworkDao {
    @Insert
    fun insert(network: Network): Long

    @Insert
    fun insertAll(vararg networks: Network)

    @Query("SELECT * FROM network WHERE networkId = :networkId")
    fun getByIdNow(networkId: Long): Network


    @Query("SELECT * FROM network WHERE networkId = :networkId")
    fun getById(networkId: Long): LiveData<Network>


}