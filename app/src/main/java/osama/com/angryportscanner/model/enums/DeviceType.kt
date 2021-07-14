package osama.com.angryportscanner.model.enums

import osama.com.angryportscanner.R

enum class DeviceType {
    PC,
    VM,
    PHONE,
    SPEAKER,
    SOC,
    ROUTER,
    NETWORK_DEVICE,
    GAME_CONSOLE,
    CAST,
    HOME_APPLIANCE,
    UNKNOWN;

    val icon
        get() = when (this) {
            PC -> R.drawable.ic_laptop_white_48dp
            VM -> R.drawable.ic_baseline_layers_48
            PHONE -> R.drawable.ic_baseline_phone_android_48
            SPEAKER -> R.drawable.ic_baseline_speaker_48
            SOC -> R.drawable.ic_memory_white_48dp
            ROUTER -> R.drawable.ic_baseline_router_48
            NETWORK_DEVICE -> R.drawable.ic_baseline_settings_ethernet_48
            GAME_CONSOLE -> R.drawable.ic_baseline_videogame_asset_48
            CAST -> R.drawable.ic_baseline_cast_48
            HOME_APPLIANCE -> R.drawable.ic_laptop_white_48dp
            UNKNOWN -> R.drawable.ic_baseline_devices_other_48
        }

    val label
        get() = when (this) {
            PC -> R.string.device_type_pc
            VM -> R.string.device_type_vm
            PHONE -> R.string.device_type_phone
            SPEAKER -> R.string.device_type_speaker
            SOC -> R.string.device_type_soc
            ROUTER -> R.string.device_type_router
            NETWORK_DEVICE -> R.string.device_type_network_device
            GAME_CONSOLE -> R.string.device_type_game_console
            CAST -> R.string.device_type_cast
            HOME_APPLIANCE -> R.string.device_type_home_appliance
            UNKNOWN -> R.string.device_type_unknown
        }
}
