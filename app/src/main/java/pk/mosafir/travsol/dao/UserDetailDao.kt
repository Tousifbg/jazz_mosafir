package pk.mosafir.travsol.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pk.mosafir.travsol.response.UserDetails
import retrofit2.http.DELETE

@Dao
interface UserDetailDao {
    @Query("SELECT * FROM user_detail")
    suspend fun getUserDetail(): UserDetails
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserDetail(offers: UserDetails)
}