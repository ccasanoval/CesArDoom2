package com.cesoft.cesardoom2

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.cesoft.cesardoom2.ui.theme.CesArDoom2Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            android.util.Log.e("MainAct", "activityResultLauncher: ${result.resultCode}")
            if(result.resultCode != RESULT_OK) {
                //Toast.makeText(this, R.string.update_is_mandatory, Toast.LENGTH_LONG).show()
                //finish()//exitProcess(0)
                setContent {
                    AlertDialogUpdateMandatory()
                }
            }
        }

    @Composable
    fun AlertDialogUpdateMandatory() {
        AlertDialog(
            icon = { Icon(Icons.Default.Warning, contentDescription = "") },
            title = {},
            text = { Text(text = stringResource(R.string.update_is_mandatory)) },
            onDismissRequest = { finish() },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { finish() }) {
                    Text(stringResource(android.R.string.ok))
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CesArDoom2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val systemUiController = rememberSystemUiController()
                    SideEffect {
                        systemUiController.setStatusBarColor(color = Color(0xff000000))
                    }
                    ArScreen()
                }
                LaunchedEffect(Unit){
                    delay(1500)
                    //
                    //updateApp()
                    //permission : android.permission.CHANGE_NETWORK_STATE
                    val networkRequest = NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .build()
                    val connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
                    connectivityManager.requestNetwork(networkRequest, networkCallback)
                }
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        updateApp()
//    }

    //https://developer.android.com/guide/playcore/in-app-updates/test
    private fun updateApp() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)//AppUpdateType.FLEXIBLE
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    private var justOneUpdateApp: AtomicBoolean = AtomicBoolean(true)
    private val networkCallback= object: ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            android.util.Log.e("AAA", "onAvailable----a--------------------- ${hashCode()} $justOneUpdateApp")
            if(justOneUpdateApp.getAndSet(false)) {
                updateApp()
            }
            android.util.Log.e("AAA", "onAvailable---b---------------------- ${hashCode()} $justOneUpdateApp")
        }
    }
}
