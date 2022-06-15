package pk.mosafir.travsol.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_detail")
data class UserDetails(
    val country_code: String? = "",
    val email: String? = "",
    val mobile: String? = "",
    val token: String? = "",
    @PrimaryKey val user_id: Int? = 0
)