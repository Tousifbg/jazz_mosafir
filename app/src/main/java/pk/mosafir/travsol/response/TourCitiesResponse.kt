package pk.mosafir.travsol.response

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class TourCitiesResponse(
    val strong: List<TourCity>
)
@Entity(tableName = "tour_city")
data class TourCity(
    @PrimaryKey
    @SerializedName("id")
    val id: Long,

    @ColumnInfo
    @SerializedName("city")
    val city: String,
)