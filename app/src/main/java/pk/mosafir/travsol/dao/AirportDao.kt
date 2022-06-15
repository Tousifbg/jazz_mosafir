package pk.mosafir.travsol.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pk.mosafir.travsol.response.DiscoverPakistanCity
import pk.mosafir.travsol.response.FlyFrom10
import pk.mosafir.travsol.response.GeneralFlightResponse

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport_list_from")
    suspend fun getAirports(): List<FlyFrom10>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirports(offers: List<FlyFrom10>)

    @Query("SELECT * FROM airport_list_from WHERE id=:id")
    suspend fun getOfferId(id: Long): FlyFrom10

    @Query("SELECT * FROM airport_list_from WHERE fly_from like :name")
    suspend fun getAirportLike(name: String): List<GeneralFlightResponse>?

    @Query("SELECT fly_from FROM airport_list_from WHERE fly_from like :name")
    suspend fun getAirportSingleLike(name: String): String
}