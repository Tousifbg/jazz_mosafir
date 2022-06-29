package pk.mosafir.travsol.dao

import androidx.room.*
import pk.mosafir.travsol.response.HotelLocation
import pk.mosafir.travsol.response.Offer
import pk.mosafir.travsol.response.UserDetails

@Dao
interface UserDetailDao {
    @Query("SELECT * FROM user_detail")
    suspend fun getUserDetail(): UserDetails

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetail(offers: UserDetails)


    @Query("DELETE FROM user_detail")
    suspend fun clear()
}