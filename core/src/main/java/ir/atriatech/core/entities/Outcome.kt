package ir.atriatech.core.entities

sealed class Outcome<T> {

    data class Progress<T>(var loading: Boolean) : Outcome<T>()
    data class Success<T>(var data: T) : Outcome<T>()
    data class Failure<T>(val e: Throwable) : Outcome<T>()
//    data class ServerError<T>(val serverError: HttpException) : Outcome<T>()
    data class Unauthorized<T>(var shouldLogOut: Boolean) : Outcome<T>()

    companion object {
        fun <T> loading(isLoading: Boolean): Outcome<T> =
            Progress(isLoading)
        fun <T> unauthorized(shouldLogOut: Boolean): Outcome<T> =
            Unauthorized(shouldLogOut)
        fun <T> success(data: T): Outcome<T> =
            Success(data)
        fun <T> failure(e: Throwable): Outcome<T> =
            Failure(e)
//        fun <T> serverError(serverError: HttpException): Outcome<T> =
//            ServerError(serverError)
//        fun onErrorHandler(e: Throwable): Throwable {
//            Timber.e(e)
//            return e
//        }
    }
}