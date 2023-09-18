package com.cesoft.cesardoom2

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArNode

@Composable
fun ArScreen() {
    val nodes = remember { mutableStateListOf<ArNode>() }
    val monster = remember { mutableStateOf<Monster?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                //TODO:
                //arSceneView.lightEstimationMode = Config.LightEstimationMode.AMBIENT_INTENSITY
                //arSceneView.planeRenderer.isShadowReceiver = true
                //arSceneView.isDepthOcclusionEnabled = true

                monster.value = Monster(arSceneView).load(nodes)
            },
            onSessionCreate = { session ->
                //TODO:
                //https://developers.google.com/ar/develop/java/depth/developer-guide#kotlin_1
//                if(session.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
//                    session.depthMode = Config.DepthMode.AUTOMATIC
            },
            onFrame = { arFrame ->
if(monster.value!=null)android.util.Log.e("AA", "onFrame------------ ${monster.value}")
                monster.value?.update(arFrame.time.intervalSeconds.toFloat())
            },
            onTap = { hitResult ->
android.util.Log.e("AA", "hitResult---${hitResult.hitPose.translation}--------- ${monster.value}")
                monster.value?.anchor(hitResult)
            }
        )
    }
}
