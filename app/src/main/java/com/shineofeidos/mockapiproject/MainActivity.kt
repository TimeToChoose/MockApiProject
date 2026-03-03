package com.shineofeidos.mockapiproject

import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.os.Build
import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shineofeidos.mockapiproject.ui.screens.CustomViewLearningScreen
import com.shineofeidos.mockapiproject.ui.screens.HomeScreen
import com.shineofeidos.mockapiproject.ui.screens.MarkdownScreen
import com.shineofeidos.mockapiproject.ui.screens.MemoryLeakScreen
import com.shineofeidos.mockapiproject.ui.screens.NetworkLearningScreen
import com.shineofeidos.mockapiproject.ui.screens.TouchEventLearningScreen
import com.shineofeidos.mockapiproject.ui.screens.TouchEventLegacyActivity
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
    val context = LocalContext.current
    
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
                    onNavigateToMemoryLeak = { navController.navigate("memory_leak") },
                    onNavigateToTouchEvent = { navController.navigate("touch_event") },
                    onNavigateToTouchEventLegacy = {
                        context.startActivity(
                            Intent(context, TouchEventLegacyActivity::class.java)
                        )
                        if (context is MainActivity) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                context.overrideActivityTransition(
                                    OVERRIDE_TRANSITION_OPEN,
                                    com.shineofeidos.mockapiproject.R.anim.fade_in,
                                    com.shineofeidos.mockapiproject.R.anim.fade_out,
                                )
                            } else {
                                @Suppress("DEPRECATION")
                                context.overridePendingTransition(
                                    com.shineofeidos.mockapiproject.R.anim.fade_in,
                                    com.shineofeidos.mockapiproject.R.anim.fade_out,
                                )
                            }
                        }
                    }
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

            composable("touch_event") {
                TouchEventLearningScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToGuide = { navController.navigate("touch_guide") }
                )
            }

            composable("touch_guide") {
                MarkdownScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
