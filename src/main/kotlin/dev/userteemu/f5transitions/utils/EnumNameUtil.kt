package dev.userteemu.f5transitions.utils

private val upperCaseLetterPattern = "(?<space>( .))".toRegex()

val Enum<*>.readableName: String
    /**
     * Example 1: "GRASS_AND_DIRT" -> "Grass And Dirt"
     * Example 2: "STONE" -> "Stone"
     * @return name for the enum that is more readable to the user than Enum#name
     */
    get() {
        if (this.name.isEmpty()) return ""
        var out = this.name.replace("_", " ").toLowerCase().capitalize()
        upperCaseLetterPattern.findAll(out).forEach {
            out = out.replaceRange(it.range, it.value.toUpperCase())
        }
        return out
    }