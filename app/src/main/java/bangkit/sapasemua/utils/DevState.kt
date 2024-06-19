package amirlabs.sapasemua.utils

sealed class DevState<T> {
    class Loading<T> : DevState<T>()
    class Default<T> : DevState<T>()
    class Empty<T> : DevState<T>()
    data class Success<T>(val data: T) : DevState<T>()
    data class Failure<T>(val throwable: Throwable?, val message: String?) : DevState<T>()

    companion object {

        fun <T> loading(): DevState<T> = Loading()
        fun <T> default(): DevState<T> = Default()
        fun <T> success(data: T): DevState<T> = Success(data)
        fun <T> empty(): DevState<T> = Empty()
        fun <T> fail(throwable: Throwable?, message: String?): DevState<T> = Failure(throwable, message)
        fun <T> fail(message: String?): DevState<T> = Failure(null, message)
    }

    @Suppress("UNCHECKED_CAST")
    fun <S> map(mapper: (T) -> S):DevState<S>{
        return when(this){
            is Success -> success(mapper(this.data))
            else -> this as DevState<S>
        }
    }
}