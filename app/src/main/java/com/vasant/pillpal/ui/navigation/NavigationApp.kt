package com.vasant.pillpal.ui.navigation

import android.content.Context.MODE_PRIVATE
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.vasant.pillpal.ui.screens.AddMedsScreen
import com.vasant.pillpal.ui.screens.AuthScreens.SignIn
import com.vasant.pillpal.ui.screens.AuthScreens.SignUpScreen
import com.vasant.pillpal.ui.screens.AuthScreens.WelcomeScreen
import com.vasant.pillpal.ui.screens.AuthScreens.SplashScreen
import com.vasant.pillpal.ui.screens.AuthScreens.GuestLoginScreen
import com.vasant.pillpal.ui.screens.ChatScreen
import com.vasant.pillpal.ui.screens.HomeScreen
import com.vasant.pillpal.ui.screens.NotificationsScreen
import com.vasant.pillpal.ui.screens.SettingsScreen

@Composable
fun NavigationApp(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()

    val context = LocalContext.current
    val prf = context.getSharedPreferences("login", MODE_PRIVATE)
    val isLoggedIn = prf.getBoolean("IS_LOGGED_IN", false)

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.AuthScreens
    ) {
        navigation<NavigationRoute.AuthScreens>(startDestination = AuthenticationRoute.SplashScreen) {

            composable<AuthenticationRoute.SplashScreen> {
                SplashScreen(navController, isLoggedIn)
            }

            composable<AuthenticationRoute.WelcomeScreen>(
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(700)
                    )
                }
            ) {
                WelcomeScreen(navController, windowSizeClass = windowSizeClass)
            }

            composable<AuthenticationRoute.LoginScreen>(
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(900)
                    )
                }
            ) {
                SignIn(navController, windowSizeClass = windowSizeClass)
            }

            composable<AuthenticationRoute.SingUpScreen>(
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(900)
                    )
                }
            ) {
                SignUpScreen(navController, windowSizeClass = windowSizeClass)
            }

            composable<AuthenticationRoute.GuestLoginScreen>(
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(700)
                    )
                }
            ) {
                GuestLoginScreen(navController, windowSizeClass = windowSizeClass)
            }
        }
        navigation<NavigationRoute.MainScreens>(MainUiRoute.HomeScreen) {
            composable<MainUiRoute.HomeScreen>(enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(700)
                )
            }) {
                HomeScreen(navController, windowSizeClass)
            }
            composable<MainUiRoute.AddMedicineScreen>(
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(700)
                    )
                },
            )
            {
                AddMedsScreen(navController, windowSizeClass = windowSizeClass)
            }
            composable<MainUiRoute.ChatScreen> {

                ChatScreen(navController, windowSizeClass = windowSizeClass)

            }
            composable<MainUiRoute.NotificationScreen> {

                NotificationsScreen(navController, windowSizeClass = windowSizeClass)
            }
            composable<MainUiRoute.SettingScreen> {
                SettingsScreen(navController, windowSizeClass = windowSizeClass)
            }
            composable<MainUiRoute.ProfileScreen> { }
        }

    }


}