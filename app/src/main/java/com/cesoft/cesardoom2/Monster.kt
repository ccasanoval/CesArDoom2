package com.cesoft.cesardoom2

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.ar.core.HitResult
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.rotation
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

class Monster(arSceneView: ArSceneView) {
    private val arModelNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT)
    private var zOffset = 0f
    private var _isLoaded = false
    val isLoaded: Boolean = _isLoaded

    fun load(nodes: SnapshotStateList<ArNode>): Monster {
        arModelNode.loadModelGlbAsync(
            glbFileLocation = "gonome.glb",
            autoAnimate = false,
            scaleToUnits = 1f,
            //centerOrigin = Position(x = 0f, y = 0f, z = 15f) ,
            onError = { exception ->
                Log.e("ArScreen", "Load Model-------------------e: $exception")
            },
            onLoaded = { modelInstance ->
                Log.e("ArScreen", "Load Model-------------------ok: ${modelInstance.root}")
                _isLoaded = true
            }
        )
        nodes.add(arModelNode)
        return this
    }

    fun update(deltaTime: Float) {
        zOffset -= 1.0f * deltaTime
        arModelNode.centerModel(arModelNode.modelPosition + Position(0f,0f, zOffset))
    }

    fun anchor(hitResult: HitResult) {
        zOffset = 0f
        arModelNode.centerModel(Position(0f, 0f, 0f))
        arModelNode.rotation = hitResult.hitPose.rotation

        arModelNode.detachAnchor()
        arModelNode.anchor = hitResult.createAnchor()

        arModelNode.playAnimation(MonsterAnimation.Run.animation, true)
    }
}