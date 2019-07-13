package ontrigger.steamFind

import java.io.File

fun main() {
    val s = SteamFind()

}

class SteamFind(filepath: String? = null) :
    ILocatorHelper by LocatorHelper() {

    var location: File = if (filepath != null) {
        validateSteamFolder(filepath)
        File(filepath)
    } else {
        findSteamFolder()
    }


    val `cool property`: String by lazy {
        "cool"
    }

    fun `does cool stuff`() {
    }

}

interface ISteamLocator {
    fun tryFindSteam(): String
}


class Win32Locator : ISteamLocator {
    override fun tryFindSteam(): String {
        val errors = mutableListOf<String>()

        HKEY_LOCATIONS.forEach { location ->
            when (val result = attempt { readRegistry(location) }) {
                is Either.Success -> return result.value
                is Either.Error -> {
                    errors.add("$location -> ${result.exception}")
                    return@forEach
                }
            }
        }

        throw SteamNotFoundException(
            """                                                      
        Could not find the steam folder!                     
        Tried the following registry keys:                   
        """.trimIndent() + errors.joinToString("")
        )
    }


    private fun readRegistry(location: String): String {

        val result = "reg query $location /v SteamPath"
            .startProcess().result

        return result
            .trim()
            .split("[ ]{2,}".toRegex())
            .last()

    }

    companion object {
        val HKEY_LOCATIONS = arrayOf(
            "HKCU\\Software\\Valve\\Steadm",
            "HKCU\\SOFTWARE\\Wow6432Node\\Valve\\Steam"
        )

        const val FLATPAK_LOCATION = "~/.var/app/com.valvesoftware.Steam"
        const val GENERIC_LOCATION = "~/.local/share/Steam"
    }
}


class SteamNotFoundException(message: String) : Exception(message)

inline fun <T, R> T.attempt(block: T.() -> R): Either<R> {
    return try {
        Either.Success(block())
    } catch (e: Exception) {
        Either.Error(e)
    }
}