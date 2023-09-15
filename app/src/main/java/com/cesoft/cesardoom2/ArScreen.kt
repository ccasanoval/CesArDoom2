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
                    centerOrigin = Position(x = 0.0f, y = 0.0f, z = 10.0f) ,
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
                //session.depthMode = Config.DepthMode.RAW_DEPTH_ONLY
            },
            onFrame = { arFrame ->
                // Retrieve ARCore frame update
                //spider.value!!.anchor()
            },
            onTap = { hitResult ->
                // User tapped in the AR view
                //if(!spider.value!!.isAnchored)
                spider.value!!.detachAnchor()
                spider.value!!.anchor = hitResult.createAnchor()
                spider.value!!.centerModel(Position(0f,0f,0f))
                //spider.value!!.anchor()
            }
        )
    }

}