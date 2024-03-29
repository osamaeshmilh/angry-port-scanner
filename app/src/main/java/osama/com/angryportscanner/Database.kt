package osama.com.angryportscanner

import android.app.Application
import androidx.room.*
import osama.com.angryportscanner.dao.DeviceDao
import osama.com.angryportscanner.dao.NetworkDao
import osama.com.angryportscanner.dao.PortDao
import osama.com.angryportscanner.dao.ScanDao
import osama.com.angryportscanner.model.*
import osama.com.angryportscanner.model.DBViews.DeviceWithName
import osama.com.angryportscanner.model.enums.Protocol
import osama.com.angryportscanner.scanner.MacAddress
import osama.com.angryportscanner.util.inet4AddressFromInt
import java.net.Inet4Address
import java.util.*

@Database(
    entities = [Network::class, Device::class, Port::class, MacVendor::class, Scan::class],
    views = [DeviceWithName::class],
    version = 2
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun networkDao(): NetworkDao
    abstract fun deviceDao(): DeviceDao
    abstract fun portDao(): PortDao
    abstract fun scanDao(): ScanDao

    companion object {
        fun createInstance(application: Application): AppDatabase {
            return Room
                .databaseBuilder(
                    application.applicationContext,
                    AppDatabase::class.java, "angry-port-db"
                )
                .createFromAsset("mac_and_ports.db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

class Converter {

    @TypeConverter
    fun toInet4Address(value: Int?): Inet4Address? {
        return if (value != null) inet4AddressFromInt(
            "",
            value
        ) else null
    }

    @TypeConverter
    fun fromInet4Address(value: Inet4Address?): Int? {
        return value?.hashCode()
    }

    @TypeConverter
    fun toProtocol(value: String?): Protocol? {
        if (value == null) return null
        return Protocol.valueOf(value)
    }

    @TypeConverter
    fun fromProtocol(value: Protocol?): String? {
        if (value == null) return null
        return value.name
    }

    @TypeConverter
    fun toMacAddress(value: String?): MacAddress? {
        return if (value != null) MacAddress(value.uppercase(Locale.getDefault())) else null
    }

    @TypeConverter
    fun fromMacAddress(value: MacAddress?): String? {
        return value?.address?.uppercase(Locale.getDefault())
    }
}