package com.example.hydrogenmobile

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hydrogenmobile.ui.theme.HydrogenMobileTheme
import com.example.hydrogenmobile.ui.views.MainScreenForm
import com.example.hydrogenmobile.utils.permission_array
import com.example.hydrogenmobile.models.BTModel
import com.example.hydrogenmobile.utils.ApplicationSimulator

class MainActivity : ComponentActivity() {
    private val btModel by lazy {
        BTModel(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            HydrogenMobileTheme {
                // Test in Smaller Device (SmartPhone)
                ApplicationSimulator() {
                    val navController =
                        rememberNavController() // navigation controller for screen switch

                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) { // manage screen with navhost
                        composable("main") {
                            MainScreenForm(btModel)
                        }
                    }
                }
                // Actual Start Code (in Tablet)
                // val navController =
                //     rememberNavController() // navigation controller for screen switch
                // NavHost(
                //     navController = navController,
                //     startDestination = "main"
                // ) { // manage screen with navhost
                //     composable("main") {
                //         MainScreenForm(btModel)
                //     }
                // }
            }
        }

        if (permission_array.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, "권한 확인", Toast.LENGTH_SHORT).show()
        }
        else{
            requestPermissionLauncher.launch(permission_array)
        }
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }
    }
}

//@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=320")
//@Composable
//fun GreetingPreview() {
//    HydrogenMobileTheme {
//        MainScreenForm()
//    }
//}
