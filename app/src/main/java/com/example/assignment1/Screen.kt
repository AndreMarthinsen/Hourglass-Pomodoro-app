package com.example.assignment1


sealed class Screen(val route: String) {
    object ActiveTimer: Screen("active_timer")
    object Presets: Screen("presets")
    object Edit: Screen("edit")

    object Settings: Screen("settings")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}