package ontrigger.steamFind

import java.util.concurrent.TimeUnit

fun String.startProcess(seconds: Long = 1): WrappedProcess {
    return WrappedProcess(
        ProcessBuilder(split("\\s".toRegex()))
            .start().apply { waitFor(seconds, TimeUnit.SECONDS) }
    )
}

inline class WrappedProcess(private val process: Process) {
    val result: String
        get() {
            val result = process.inputStream.bufferedReader().readText()
            val error = process.errorStream.bufferedReader().readText()

            return when {
                error.isNotEmpty() -> throw ExecutionException(error)
                result.isEmpty() -> throw EmptyResultException()
                else -> result
            }
        }
}

sealed class Either<out T> {
    data class Error(val exception: Exception) : Either<Nothing>()
    data class Success<T>(val value: T) : Either<T>()
}

class ExecutionException(message: String) : Exception(message)
class EmptyResultException : Exception()