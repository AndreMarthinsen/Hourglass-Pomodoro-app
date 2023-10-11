package com.example.assignment1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.assignment1.ui.theme.Assignment1Theme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


/**
 * App entrypoint
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment1Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}


/**
 * Application navigation. Utilizes the routes Screen.ConversionScreen and
 * Screen.InputScreen.
 */
@Composable
fun AppNavigation () {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.InputScreen.route
    ) {
        composable(
            route = Screen.InputScreen.route
        ) {
            InputScreen(navController)
        }
        composable( // Route is called with degrees as an argument for the composable
            route = Screen.ConversionsScreen.route + "/{degrees}",
            arguments = listOf(
                navArgument("degrees") {
                    type = NavType.FloatType
                    defaultValue = 0.0f
                    nullable = false
                }
            )
        ) {entry ->
            ConvertedScreen(
                navController, degrees = entry.arguments?.getFloat("degrees")
            )
        }
    }
}




