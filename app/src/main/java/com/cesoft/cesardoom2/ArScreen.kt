package com.cesoft.cesardoom2

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.rotation
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

@Composable
fun ArScreen() {
    val nodes = remember { mutableStateListOf<ArNode>() }
    val spider = remember { mutableStateOf<ArModelNode?>(null) }
    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.AMBIENT_INTENSITY
                arSceneView.planeRenderer.isShadowReceiver = true
                //arSceneView.isDepthOcclusionEnabled = true

                spider.value = ArModelNode(arSceneView.engine, PlacementMode.INSTANT)
                spider.value!!.loadModelGlbAsync(
                    glbFileLocation = "spider.glb",
                    autoAnimate = false,
                    scaleToUnits = 0.5f,
                    centerOrigin = Position(x = 0f, y = -2f, z = 5f) ,
                    onError = { exception ->
                        Log.e("ArScreen", "Load Model-------------------e: $exception")
                    },
                    onLoaded = { modelInstance ->
                        Log.e("ArScreen", "Load Model-------------------ok: ${modelInstance.root}")
                    }
                )
                nodes.add(spider.value!!)
            },
            onSessionCreate = { session ->
                //https://developers.google.com/ar/develop/java/depth/developer-guide#kotlin_1
//                if(session.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
//                    session.depthMode = Config.DepthMode.AUTOMATIC
            },
            onFrame = { arFrame ->
                // Retrieve ARCore frame update
                //spider.value!!.anchor()
            },
            onTap = { hitResult ->
                // User tapped in the AR view
                //if(!spider.value!!.isAnchored)
                spider.value?.let { s ->
                    s.detachAnchor()
                    s.anchor = hitResult.createAnchor()//spider.value!!.anchor()
                    s.centerModel(Position(0f, 0f, 0f))
                    s.rotation = hitResult.hitPose.rotation

                    // IDLE(10-110)
                    // WALK(120-160)
                    // SCREAM(170-270)
                    // JUMP WITH ROOT(280-330)
                    // JUMP(340-390)
                    // HEAD(400-415)
                    // ATACK SECTION(420-500)
                    s.playAnimation("basic", true)
                }

            }
        )
    }

}