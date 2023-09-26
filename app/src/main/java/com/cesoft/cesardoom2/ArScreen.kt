package com.cesoft.cesardoom2

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.ar.core.Config.LightEstimationMode
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArNode


//ArCore: https://developers.google.com/ar/develop/java/lighting-estimation/developer-guide?hl=en
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArScreen() {
    val nodes = remember { mutableStateListOf<ArNode>() }
    val monster = remember { mutableStateOf<Monster?>(null) }
    val activity = (LocalContext.current as? Activity)

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
            FloatingActionButton(onClick = { activity?.finish()  }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ArScene(nodes, monster)
        }
    }
}

@Composable
fun ArScene(
    nodes: SnapshotStateList<ArNode>,
    monster: MutableState<Monster?>
) {
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
