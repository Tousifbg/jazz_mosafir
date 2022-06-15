package pk.mosafir.travsol.model

import com.google.gson.annotations.SerializedName

data class SocialLoginModel (
    @SerializedName("gmail") val gmail:String?,
    @SerializedName("name") val name:String?,
    @SerializedName("image_url") val image_url:String?,
    @SerializedName("oauth_id") val oauth_id:String?,
    @SerializedName("oauth_type") val oauth_type:String?,
)