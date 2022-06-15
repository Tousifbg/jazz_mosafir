package pk.mosafir.travsol.response

data class AirportList(
    val fly_from_10: List<FlyFrom10> = listOf(),
    val fly_to_10: List<FlyTo10> = listOf()
)