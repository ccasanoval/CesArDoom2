package com.cesoft.cesardoom2

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.ar.core.HitResult
import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.ArFrame
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.rotation
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position

//TODO: Detect a change in camera position so monster can follow it: angle and position...
//TODO: Sound Fx
//TODO: Way to shoot the monster and kill it
//TODO: Show life level bar...
//TODO: Depth
//3D MODEL: https://www.turbosquid.com/es/3d-models/3d-improved-gonome-1901177
class Monster(arSceneView: ArSceneView) {
    private val arModelNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT)
    private var zOffset = 0f
    private var state = MonsterAnimation.Idle

    fun load(nodes: SnapshotStateList<ArNode>): Monster {
        arModelNode.loadModelGlbAsync(
            glbFileLocation = "gonome.glb",
            autoAnimate = false,
            scaleToUnits = 1f,
            //centerOrigin = Position(x = 0f, y = 0f, z = 0f) ,
            onError = { exception ->
                Log.e("ArScreen", "Load Model-------------------e: $exception")
            },
            onLoaded = { modelInstance ->
                Log.e("ArScreen", "Load Model-------------------ok: ${modelInstance.root}")
                //_isLoaded = true
            }
        )
        nodes.add(arModelNode)
        return this
    }

    private var idleStart = 0f//TODO
    fun update(deltaTime: Float, cameraPosition: Vector3) {
        if(arModelNode.isAnchored) {
            when(state) {
                MonsterAnimation.Idle -> {
                    idleStart += deltaTime
                    if(idleStart > 5) {
                        idleStart = 0f
                        changeState(MonsterAnimation.Walk)
                    }
                }
                MonsterAnimation.Walk,
                MonsterAnimation.Run -> {
                    zOffset = arModelNode.modelPosition.z + .25f * deltaTime
                    val position = Position(0f, 0f, zOffset)
                    //arModelNode.centerModel(position)
                    arModelNode.modelPosition = position

                    val dist2 = distance2(cameraPosition)
                    Log.e("Mnstr", "run-----------dist2 = $dist2")
                    if(dist2 < distAttack) {
                        changeState(MonsterAnimation.Attack)
                    }
                }
                MonsterAnimation.Attack -> {
                    //.. check distance again
                    val dist2 = distance2(cameraPosition)
                    Log.e("Mnstr", "attack-----------dist2 = $dist2")
                    if(dist2 > distFollow) {
                        changeState(MonsterAnimation.Run)
                    }
                }
                else -> {}
            }
        }
    }

    private fun changeState(newState: MonsterAnimation) {
        state = newState
        for(i in 0..(arModelNode?.animator?.animationCount ?: 0)) {
            arModelNode.stopAnimation(i)
        }
        arModelNode.playAnimation(state.animation, true)
    }
    private fun distance2(cameraPosition: Vector3): Float {
        val x = cameraPosition.x - arModelNode.modelPosition.x - arModelNode.anchor!!.pose!!.position.x
        val y = cameraPosition.y - arModelNode.modelPosition.y - arModelNode.anchor!!.pose!!.position.y
        val z = cameraPosition.z - arModelNode.modelPosition.z - arModelNode.anchor!!.pose!!.position.z
        return x*x + y*y + z*z
    }

    fun anchor2(frame: ArFrame) {
        if(arModelNode.isAnchored) {
            //val rotation = floatArrayOf(0f, 0f, 0f, 1f)

//            try {
//                Log.e("Mnstr", "anchor2--10------- ${arModelNode.modelPosition.x}, ${arModelNode.modelPosition.y}, ${arModelNode.modelPosition.y}, ::: $zOffset ")
//                val position = arModelNode.modelPosition + Position(0f, -1f, zOffset)
//                zOffset = 0f
//                arModelNode.detachAnchor()
//                arModelNode.anchor = frame.session.createAnchor(Pose(position.toFloatArray(), floatArrayOf(0f, 0f, 0f, 1f)))
//                Log.e("Mnstr", "anchor2--20------- ${arModelNode.modelPosition.x}, ${arModelNode.modelPosition.y}, ${arModelNode.modelPosition.y}, ")
//            }
//            catch (e: Exception) {
//                Log.e("Mnstr", "anchor2---------$e")
//            }
        }
    }

    fun anchor(hitResult: HitResult) {
        zOffset = 0f
        //arModelNode.centerModel(Position(0f, 0f, 0f))
        arModelNode.rotation = hitResult.hitPose.rotation

        arModelNode.detachAnchor()
        arModelNode.anchor = hitResult.createAnchor()
        Log.e("Mnstr", "anchor--------${arModelNode.modelPosition} // ${arModelNode.anchor?.pose?.position}")

        changeState(MonsterAnimation.Idle)
    }

    companion object {
        private const val distAttack = 2.1f
        private const val distFollow = 2.6f
    }
}