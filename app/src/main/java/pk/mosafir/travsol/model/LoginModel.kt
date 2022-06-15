package pk.mosafir.travsol.model

import com.google.gson.annotations.SerializedName

data class LoginModel (
    @SerializedName("full_name") val full_name:String?,
    @SerializedName("mobile") val mobile:String,
    @SerializedName("mobile_country") val mobile_country: String
)