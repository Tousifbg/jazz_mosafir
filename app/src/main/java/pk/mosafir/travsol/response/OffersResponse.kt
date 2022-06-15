package pk.mosafir.travsol.response

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class OfferResponse(val strong: List<Offer>)

@Entity(tableName = "offer")
data class Offer(
    @PrimaryKey val id: Long,
    @ColumnInfo
    val title: String,
    @ColumnInfo
    val city: String,
    @ColumnInfo
    val starting: String,
    @ColumnInfo
    val viewLocation: String,
    @ColumnInfo
    val s_currency: String,
    @ColumnInfo
    val f_currency: String,
    @ColumnInfo
    val s_price: String,
    @ColumnInfo
    val f_price: String
)