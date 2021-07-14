package osama.com.angryportscanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import osama.com.angryportscanner.model.enums.Protocol

@Entity
data class PortDescription(
    @PrimaryKey
    val portId: Long,
    val port: Int,
    val protocol: Protocol,
    val serviceName: String,
    val serviceDescription: String
) {
    companion object {
        val commonPorts = listOf(
            PortDescription(0, 21, Protocol.TCP, "FTP", "File Transfer Protocol"),
            PortDescription(0, 22, Protocol.TCP, "SFTP/SSH", "Secure FTP or Secure Shell"),
            PortDescription(0, 80, Protocol.TCP, "HTTP", "Hypertext Transport Protocol"),
            PortDescription(0, 53, Protocol.UDP, "DNS", "DNS Server"),
            PortDescription(0, 443, Protocol.TCP, "HTTPS", "Secure HTTP"),
            PortDescription(0, 548, Protocol.TCP, "AFP", "AFP over TCP"),
            PortDescription(0, 631, Protocol.TCP, "IPP", "Internet Printing Protocol"),
            PortDescription(0, 989, Protocol.TCP, "FTPS", "FTP over TLS"),
            PortDescription(0, 1883, Protocol.TCP, "MQTT", "Message Queuing Telemetry Transport"),
            PortDescription(0, 5000, Protocol.TCP, "UPNP", "Universal Plug and Play"),
            PortDescription(0, 8000, Protocol.TCP, "HTTP Alt", "HTTP common alternative"),
            PortDescription(0, 8080, Protocol.TCP, "HTTP-Proxy", "HTTP Proxy"),
            PortDescription(0, 62078, Protocol.TCP, "iPhone-Sync", "lockdown iOS Service")

        )
    }
}