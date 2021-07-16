package osama.com.angryportscanner

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import androidx.lifecycle.*
import osama.com.angryportscanner.model.Network
import osama.com.angryportscanner.repositories.ScanRepository
import osama.com.angryportscanner.scanner.InterfaceScanner

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.createInstance(application)

    val deviceDao = db.deviceDao()
    private val networkDao = db.networkDao()
    val portDao = db.portDao()
    private val scanDao = db.scanDao()
    private val networkScanRepository = ScanRepository(networkDao, scanDao, deviceDao, application)
    val scanProgress by lazy { MutableLiveData<ScanRepository.ScanProgress>() }
    private val currentNetworkId = MutableLiveData<Long>()
    val currentScanId = MutableLiveData<Long>()

    val devices = Transformations.switchMap(currentNetworkId) {
        deviceDao.getAll(it)
    }

    fun fetchAvailableInterfaces() = networkScanRepository.fetchAvailableInterfaces()

    val availableInterfaces by lazy {
        MediatorLiveData<List<InterfaceScanner.NetworkResult>>()
    }

    class NetworksLiveData(context: Context, private val networkScanRepository: ScanRepository) :
        LiveData<List<InterfaceScanner.NetworkResult>>() {
        private val connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        private val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: android.net.Network) {
                refresh()
            }
        }

        override fun onActive() {
            super.onActive()
            connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
        }

        override fun onInactive() {
            super.onInactive()
            connectivityManager.unregisterNetworkCallback(callback)
        }

        fun refresh() {
            value = networkScanRepository.fetchAvailableInterfaces()
        }
    }

    suspend fun startScan(interfaceName: String): Network? {
        val network = networkScanRepository.startScan(interfaceName, scanProgress, currentNetworkId)
            ?: return null
        currentScanId.value = network.scanId
        return network
    }
}

