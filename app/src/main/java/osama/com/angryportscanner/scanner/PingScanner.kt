package osama.com.angryportscanner.scanner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import osama.com.angryportscanner.model.Network
import java.net.Inet4Address

class PingScanner(
    val network: Network,
    val ipGuesses: Map<Inet4Address, Int> = mapOf(),
    val onUpdate: (ScanResult) -> Unit
) {

    suspend fun pingIpAddresses(): List<ScanResult> =
        withContext(Dispatchers.IO) {
            network.enumerateAddresses()
                .sortedByDescending {
                    // most often seen first, then all other addresses
                    ipGuesses[it] ?: 0
                }
                .chunked(10)
                .map { ipAddresses ->
                    async {
                        ipAddresses.map { ipAddress ->
                            val isReachable = ipAddress.isReachable(1000)
                            val result =
                                ScanResult(ipAddress, isReachable, 1.0 / network.networkSize)
                            onUpdate(result)
                            result
                        }
                    }
                }
                .toList()
                .awaitAll()
                .flatten()
        }

    data class ScanResult(
        val ipAddress: Inet4Address,
        val isReachable: Boolean,
        val progressIncrease: Double
    )
}

