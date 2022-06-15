package pk.mosafir.travsol.response

data class UserDetailTable(
    val Status_code: String? = "",
    val message: String? = "",
    val success: String? = "",
    val user_details: UserDetails = UserDetails()
)