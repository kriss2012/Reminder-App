package com.vasant.pillpal.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationRoute {
    @Serializable
    data object AuthScreens : NavigationRoute

    @Serializable
    data object MainScreens : NavigationRoute
}

@Serializable
sealed interface MainUiRoute {
    @Serializable
    data object  SplashScreen : MainUiRoute
    @Serializable
    data object HomeScreen : MainUiRoute

    @Serializable
    data object AddMedicineScreen : MainUiRoute

    @Serializable
    object SettingScreen : MainUiRoute


    @Serializable
    object ReminderScreen : MainUiRoute

    @Serializable
    object ProfileScreen : MainUiRoute

    @Serializable
    object NotificationScreen : MainUiRoute

    @Serializable
    object ChatScreen : MainUiRoute
}

@Serializable
sealed interface AuthenticationRoute {
    @Serializable
    data object SplashScreen : AuthenticationRoute

    @Serializable
    data object WelcomeScreen : AuthenticationRoute

    @Serializable
    data object LoginScreen : AuthenticationRoute

    @Serializable
    data object SingUpScreen : AuthenticationRoute

    @Serializable
    data object GuestLoginScreen : AuthenticationRoute
}