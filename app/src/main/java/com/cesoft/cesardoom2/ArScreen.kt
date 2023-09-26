package com.cesoft.cesardoom2

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.ar.core.Config.LightEstimationMode
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.isTracking
import io.github.sceneview.ar.node.ArNode


//ArCore: https://developers.google.com/ar/develop/java/lighting-estimation/developer-guide?hl=en
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArScreen() {
    val activity = (LocalContext.current as? Activity)
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val buttonSize = screenHeight*.075f
    val buttonPosY = screenHeight*.78f - buttonSize

    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }
    val shot = remember { mutableStateOf(false) }

    val context = LocalContext.current
    DisposableEffect(true) {
        SoundFx.init(context)
        onDispose {
            SoundFx.release()
        }
    }

    Scaffold(
        topBar = {},
        floatingActionButton = {
            Row {
                Icon(
                    painter = painterResource(R.drawable.exit),
                    contentDescription = "Salir",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(buttonSize)
                        .offset(x = 5.dp, y = -buttonPosY)
                        .clickable {
                            activity?.finish()
                        }
                )
                //TODO: Show crossbow in the middle of screen
                Icon(
                    painter = painterResource(R.drawable.rifle),
                    contentDescription = "Disparar",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .padding(0.dp)
                        .offset(x = 80.dp, y = 16.dp)
                        .clickable {
                            //monster.value?.shoot()
                            shot.value = true
                        }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ArScene(shot, screenWidthPx, screenHeightPx)
        }
    }
}

@Composable
fun ArScene(
    shot: MutableState<Boolean>,
    screenWidthPx: Int,
    screenHeightPx: Int
) {
    val monster = remember { mutableStateOf<Monster?>(null) }
    val nodes = remember { mutableStateListOf<ArNode>() }
    ARScene(
        modifier = Modifier.fillMaxSize(),
        nodes = nodes,
        planeRenderer = true,//false,//TODO: Release => false
        onCreate = { arSceneView ->
            //arSceneView.lightEstimationMode = LightEstimationMode.ENVIRONMENTAL_HDR
            arSceneView.lightEstimationMode = LightEstimationMode.AMBIENT_INTENSITY
            //arSceneView.planeRenderer.isShadowReceiver = true
            //arSceneView.isDepthOcclusionEnabled = true
            arSceneView.instantPlacementEnabled = true
            monster.value = Monster(arSceneView).load().show(nodes)
        },
        onSessionCreate = { session ->
//                if(session.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
//                    session.depthMode = Config.DepthMode.AUTOMATIC
        },
        onFrame = { arFrame ->

            if(shot.value) {
                SoundFx.play(Sound.Gun)
                val results = arFrame.frame.hitTest(screenWidthPx / 2f, screenHeightPx / 2f)
                for(hit in results) {
                    if(hit.trackable is com.google.ar.core.Point) {
                        android.util.Log.e("ArScene", "ArScene-------------------${hit.distance} +++ ${hit.trackable} +++ ${hit.isTracking}")
                        monster.value?.shoot(hit.distance)
                    }
                }
                shot.value = false
            }

            val deltaTime = arFrame.time.intervalSeconds.toFloat()
            if(monster.value?.anchor(deltaTime) == true) {
                arSession?.instantPlacementEnabled = false
            }
            monster.value?.update(
                deltaTime = deltaTime,
                camera = arFrame.camera.pose,
            )
        },
        onTap = { hitResult ->
            monster.value?.anchor(hitResult)
        }
    )
}
