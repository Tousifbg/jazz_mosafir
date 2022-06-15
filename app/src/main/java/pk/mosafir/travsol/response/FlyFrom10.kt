package pk.mosafir.travsol.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "airport_list_from")
data class FlyFrom10(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val fly_from: String = ""
)