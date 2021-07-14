package osama.com.angryportscanner.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Scan(@PrimaryKey(autoGenerate = true) val scanId: Long, val startedAt: Long)
