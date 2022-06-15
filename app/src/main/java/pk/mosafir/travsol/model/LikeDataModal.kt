package pk.mosafir.travsol.model

data class LikeDataModal(
    val city: String?,
    val src: String?,
    val dataList: ArrayList<data>?
)
class data(
    val id: Int,
    val from: String?,
    val to:String?,
    val price: String?
)
