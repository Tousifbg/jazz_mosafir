package pk.mosafir.travsol.response

sealed class Response<out T> {
    class Success<T>(val data: T) : Response<T>()

    class Error<T>(val message: String?) : Response<T>()
}