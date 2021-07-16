package osama.com.angryportscanner.scanner

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket

class PingTools {

    fun pingHost(ip: String): Boolean {
        val runtime = Runtime.getRuntime()
        var process: Process? = null
        try {
            process = runtime.exec("ping -c 1 $ip")
            process.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val exit = process!!.exitValue()
        return exit == 0
    }

    fun socketPing(host: String?, port: Int, timeout: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), timeout)
            socket.close()
            true
        } catch (ex: java.lang.Exception) {
            false
        }
    }

    fun getSocketBanner(host: String?, port: Int, timeout: Int): String {
        var banner: String
        var s: Socket?
        try {
            s = Socket()
            s.connect(InetSocketAddress(host, port), timeout)
            val `in` = BufferedReader(InputStreamReader(s.getInputStream()))
            Thread.sleep(timeout.toLong())
            if (!`in`.ready()) {
                banner = "No Banner"
                Log.d(port.toString() + "", banner)
            } else {
                banner = `in`.readLine()
                Log.d(port.toString() + "", banner)
            }
            `in`.close()
            s.close()
        } catch (ex: java.lang.Exception) {
            banner = "No Banner"
            Log.d("error", ex.toString())
        }
        return banner
    }
}