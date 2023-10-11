package com.example.assignment1


sealed class Screen(val route: String) {
    object InputScreen: Screen("input_screen")
    object ConversionsScreen: Screen("conversions_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}