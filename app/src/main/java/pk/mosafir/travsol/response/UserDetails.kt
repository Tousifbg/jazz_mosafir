package pk.mosafir.travsol.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_detail")
data class UserDetails(
    val country_code: String? = "",
    val email: String? = "",
    val mobile: String? = "",
    val token: String? = "",
    val profile_image: String? = "",
    val full_name: String? = "",
    var auth_type: String? = "",
    @PrimaryKey val user_id: Int
)