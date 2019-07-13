package ontrigger.steamFind

import java.io.File

interface ILocatorHelper {
    val steamLocators: List<ISteamLocator>

    fun validateSteamFolder(path: String)
    fun validateSteamFolder(path: File)

    fun findSteamFolder(): File

}

class LocatorHelper : ILocatorHelper {
    override val steamLocators: List<ISteamLocator>
        get() = listOf(Win32Locator())


    override fun findSteamFolder(): File {
        steamLocators.forEach { locator ->
            when (val result = attempt { locator.tryFindSteam() }) {
                is Either.Success -> {
                    validateSteamFolder(result.value)
                    return File(result.value)
                }
                is Either.Error -> return@forEach
            }
        }
        throw SteamNotFoundException("a")

    }

    override fun validateSteamFolder(path: File) {
        return
    }

    override fun validateSteamFolder(path: String) {
        if (File(path).exists()) return

        throw Exception("Folder doesn't exist")
    }

}