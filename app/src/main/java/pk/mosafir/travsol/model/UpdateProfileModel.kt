package pk.mosafir.travsol.model
import java.io.File
data class UpdateProfileModel (
    val update_check:String?,
    val user_id:String?,
    val profile_image: File?,
    val full_name:String?,
    val email:String?,
    val address:String?,
)