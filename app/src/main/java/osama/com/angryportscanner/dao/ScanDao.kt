package osama.com.angryportscanner.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import osama.com.angryportscanner.model.Scan

@Dao
interface ScanDao {
    @Insert
    suspend fun insert(scan: Scan): Long

    @Query("Select * FROM SCAN")
    fun getAll(): LiveData<List<Scan>>

    @Query("Select * FROM SCAN")
    fun getAllNow(): List<Scan>

    @Query("SELECT * FROM SCAN WHERE scanId = :scanId")
    fun getById(scanId: Long): LiveData<Scan?>

}