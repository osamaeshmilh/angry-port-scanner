package osama.com.angryportscanner.model

import androidx.room.DatabaseView
import androidx.room.Ignore
import osama.com.angryportscanner.model.enums.DeviceType
import osama.com.angryportscanner.scanner.MacAddress
import java.net.Inet4Address

@DatabaseView("SELECT Device.deviceId, Device.networkId, Device.ip, Device.hwAddress, Device.deviceName, MacVendor.name as vendorName, Device.isScanningDevice FROM Device LEFT JOIN MacVendor ON MacVendor.mac = substr(Device.hwAddress, 0, 9)")
data class DeviceWithName(
    val deviceId: Long,
    val networkId: Long,
    val ip: Inet4Address,
    val hwAddress: MacAddress?,
    val deviceName: String?,
    val vendorName: String?,
    val isScanningDevice: Boolean
) {
    @Ignore
    val asDevice = Device(deviceId, networkId, ip, deviceName, hwAddress, isScanningDevice)

    val deviceType
        get() = when {
            isScanningDevice -> DeviceType.PHONE

            /**
             * Device type based on MAC address
             */
            // Unofficial mac address range used by KVM Virtual Machines
            hwAddress?.address?.startsWith("52:54:00") == true -> DeviceType.VM

            /**
             * Device type based on vendor name
             */
            vendorName == null -> DeviceType.UNKNOWN

            // Mixed
            vendorName.contains("Apple, Inc.", ignoreCase = true) &&
                    this.deviceName?.contains("MacBook") == true -> DeviceType.PC
            vendorName.contains("Apple, Inc.", ignoreCase = true) &&
                    this.deviceName?.contains("iPhone") == true -> DeviceType.PHONE

            // PC
            vendorName.contains("Micro-Star INTL", ignoreCase = true) -> DeviceType.PC
            vendorName.contains("Dell", ignoreCase = true) -> DeviceType.PC
            vendorName.contains("Hewlett Packard", ignoreCase = true) -> DeviceType.PC
            // Lenovo
            vendorName.contains("LCFC(HeFei) Electronics Technology", ignoreCase = true) ->
                DeviceType.PC
            // Intel WIFI-Cards
            vendorName.contains("Intel Corporate", ignoreCase = true) -> DeviceType.PC

            // Phone
            vendorName.contains("LG Electronics (Mobile Communications)", ignoreCase = true) ->
                DeviceType.PHONE
            vendorName.contains("HUAWEI", ignoreCase = true) -> DeviceType.PHONE
            vendorName.contains("Xiaomi Communications", ignoreCase = true) -> DeviceType.PHONE
            vendorName.contains("Fairphone", ignoreCase = true) -> DeviceType.PHONE
            vendorName.contains("Motorola Mobility", ignoreCase = true) -> DeviceType.PHONE
            vendorName.contains("HTC", ignoreCase = true) -> DeviceType.PHONE

            // Router
            vendorName.contains("Compal", ignoreCase = true) -> DeviceType.ROUTER
            vendorName.contains("Ubiquiti", ignoreCase = true) -> DeviceType.ROUTER
            vendorName.contains("AVM", ignoreCase = true) -> DeviceType.ROUTER
            vendorName.contains("TP-LINK", ignoreCase = true) -> DeviceType.ROUTER

            // Speaker
            vendorName.contains("Sonos", ignoreCase = true) -> DeviceType.SPEAKER

            // SoC
            vendorName.contains("Espressif", ignoreCase = true) -> DeviceType.SOC
            vendorName.contains("Raspberry", ignoreCase = true) -> DeviceType.SOC

            // Network device
            vendorName.contains("ADMTEK", ignoreCase = true) -> DeviceType.NETWORK_DEVICE

            // Video game
            vendorName.contains("Nintendo", ignoreCase = true) -> DeviceType.GAME_CONSOLE

            // Cast
            vendorName.contains("AzureWave", ignoreCase = true) -> DeviceType.CAST

            // VM
            vendorName.contains("VMware", ignoreCase = true) -> DeviceType.VM

            // Home Appliance
            vendorName.contains("XIAOMI Electronics,CO.,LTD", ignoreCase = true) ->
                DeviceType.HOME_APPLIANCE

            else -> DeviceType.UNKNOWN
        }
}