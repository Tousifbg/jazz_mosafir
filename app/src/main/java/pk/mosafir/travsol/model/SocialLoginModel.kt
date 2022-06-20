package pk.mosafir.travsol.model

import com.google.gson.annotations.SerializedName

data class SocialLoginModel (
     val gmail:String?,
     val name:String?,
     val image_url:String?,
     val oauth_id:String?,
     val oauth_type:String?,
)