package osama.com.angryportscanner.repositories

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import osama.com.angryportscanner.dao.NetworkDao
import osama.com.angryportscanner.model.Network

class NetworkRepository(private val networkDao: NetworkDao) {
    suspend fun getNetwork(networkId: Long): LiveData<Network> {
        withContext(Dispatchers.IO) {
            networkDao.getById(networkId)
        }
        return networkDao.getById(networkId)
    }
}