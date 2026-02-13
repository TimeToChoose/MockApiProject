package com.shineofeidos.mockapiproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shineofeidos.mockapiproject.ui.screens.CustomViewLearningScreen
import com.shineofeidos.mockapiproject.ui.screens.HomeScreen
import com.shineofeidos.mockapiproject.ui.screens.MemoryLeakScreen
import com.shineofeidos.mockapiproject.ui.screens.NetworkLearningScreen
import com.shineofeidos.mockapiproject.ui.theme.MockApiProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MockApiProjectTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToNetwork = { navController.navigate("network") },
                    onNavigateToCustomView = { navController.navigate("custom_view") },
                    onNavigateToMemoryLeak = { navController.navigate("memory_leak") }
                )
            }
            
            composable("network") {
                NetworkLearningScreen()
            }
            
            composable("custom_view") {
                CustomViewLearningScreen()
            }

            composable("memory_leak") {
                MemoryLeakScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
