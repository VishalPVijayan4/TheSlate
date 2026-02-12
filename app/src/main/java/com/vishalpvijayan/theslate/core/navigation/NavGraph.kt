package com.vishalpvijayan.theslate.core.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vishalpvijayan.theslate.ui.screens.AppEntryViewModel
import com.vishalpvijayan.theslate.ui.screens.DashboardScreen
import com.vishalpvijayan.theslate.ui.screens.DashboardViewModel
import com.vishalpvijayan.theslate.ui.screens.LoginScreen
import com.vishalpvijayan.theslate.ui.screens.LoginViewModel
import com.vishalpvijayan.theslate.ui.screens.NoteEditorScreen
import com.vishalpvijayan.theslate.ui.screens.NoteEditorViewModel
import com.vishalpvijayan.theslate.ui.screens.OnboardingScreen
import com.vishalpvijayan.theslate.ui.screens.SplashScreen

@Composable
fun TheSlateNavGraph() {
    val navController = rememberNavController()
    val appVm: AppEntryViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(isLoggedIn = appVm.session.value.isLoggedIn) { loggedIn ->
                navController.navigate(if (loggedIn) "dashboard" else "onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("onboarding") {
            OnboardingScreen(onProceed = { navController.navigate("login") })
        }
        composable("login") {
            val vm: LoginViewModel = hiltViewModel()
            LoginScreen(onGoogleSignIn = {
                vm.loginAsDemo()
                navController.navigate("dashboard") { popUpTo("login") { inclusive = true } }
            })
        }
        composable("dashboard") {
            val vm: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = vm,
                onOpenNote = { navController.navigate("note/$it") },
                onCreate = { navController.navigate("note/new") },
                onSignedOut = {
                    navController.navigate("login") { popUpTo("dashboard") { inclusive = true } }
                }
            )
        }
        composable("note/new") {
            val vm: NoteEditorViewModel = hiltViewModel()
            NoteEditorScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(
            route = "note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.StringType })
        ) {
            val vm: NoteEditorViewModel = hiltViewModel()
            NoteEditorScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
    }
}
