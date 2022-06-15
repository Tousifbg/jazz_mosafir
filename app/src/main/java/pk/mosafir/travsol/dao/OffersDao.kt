package pk.mosafir.travsol.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pk.mosafir.travsol.response.Offer

@Dao
interface OffersDao {
    @Query("SELECT * FROM offer")
    suspend fun getOffers(): List<Offer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<Offer>)

    @Query("SELECT * FROM offer WHERE id=:id")
    suspend fun getOfferId(id: Long): Offer
}