package com.cesoft.cesardoom2

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.cesoft.cesardoom2.Util3D.distance
import com.cesoft.cesardoom2.Util3D.distance2
import com.cesoft.cesardoom2.Util3D.getLocalCameraPosition
import com.cesoft.cesardoom2.Util3D.getLocalDirection
import com.cesoft.cesardoom2.Util3D.getLocalMonsterAngle
import com.cesoft.cesardoom2.Util3D.getRealWorldPosition
import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.rotation
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import java.util.Locale
import kotlin.math.atan

//TODO: Detect a change in camera position so monster can follow it: ANGLE
//TODO: Sound Fx
//TODO: Way to shoot the monster and kill it
//TODO: Show life level bar...
//TODO: Depth
//3D MODEL: https://www.turbosquid.com/es/3d-models/3d-improved-gonome-1901177
class Monster(arSceneView: ArSceneView) {
    private val arModelNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT)
    private var state = MonsterAnimation.Idle
    private var idleStart = 0f//TODO: state machine + time
    private var anchorAngle = 0f

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

    fun update(deltaTime: Float, camera: Pose) {
        log(deltaTime, camera)
        if(arModelNode.isAnchored) {
            val angle = anchorAngle
            val worldPosition = arModelNode.worldPosition
            val modelPosition = arModelNode.modelPosition
            val cameraPosition = camera.position

            val realWorldPosition = getRealWorldPosition(
                angle = angle,
                modelPosition = modelPosition,
                worldPosition = worldPosition
            )

            //Rotate monster
            val monsterAngle = getLocalMonsterAngle(
                angle = angle,
                realWorldPosition = realWorldPosition,
                cameraPosition = camera.position
            )
            arModelNode.modelRotation = Rotation(0f, monsterAngle, 0f)

            when(state) {
                MonsterAnimation.Idle -> {
                    idleStart += deltaTime
                    if(idleStart > IdleDelay) {
                        idleStart = 0f
                        changeState(MonsterAnimation.Walk)
                    }
                }
                MonsterAnimation.Walk,
                MonsterAnimation.Run -> {

                    val delta = .25f * deltaTime
                    val dir = getLocalDirection(
                        angle = angle,
                        realWorldPosition = realWorldPosition,
                        cameraPosition = cameraPosition
                    )
                    arModelNode.modelPosition += dir * delta
                    //arModelNode.modelPosition += Position(0f, 0f, 1f) * delta//dir * delta//TODO: TESTING

                    val dist2 = distance2(
                        realWorldPosition = realWorldPosition,
                        cameraPosition = cameraPosition
                    )
                    if(dist2 < DistAttack) {
                        changeState(MonsterAnimation.Attack)
                    }
                }
                MonsterAnimation.Attack -> {
                    //.. check distance again
                    val dist2 = distance2(
                        realWorldPosition = realWorldPosition,
                        cameraPosition = cameraPosition
                    )
                    //Log.e("Mnstr", "attack-----------dist2 = $dist2")
                    if(dist2 > DistFollow) {
                        changeState(MonsterAnimation.Run)
                    }
                }
                else -> {}
            }
        }
    }

    private fun changeState(newState: MonsterAnimation) {
        state = newState
        for(i in 0..(arModelNode.animator?.animationCount ?: 0)) {
            arModelNode.stopAnimation(i)
        }
        arModelNode.playAnimation(state.animation, true)
    }

    fun anchor(hitResult: HitResult) {
        idleStart = 0f
        changeState(MonsterAnimation.Idle)

        arModelNode.centerModel(Position(0f,-1f,0f))
        arModelNode.rotation = hitResult.hitPose.rotation

        arModelNode.detachAnchor()
        arModelNode.anchor = hitResult.createAnchor()
        anchorAngle = arModelNode.worldRotation.y//arModelNode.anchor?.pose?.rotation?.y ?: 0f
        Log.e("Mnstr", "anchor1--------pos=${arModelNode.worldPosition.toS()} // model=${arModelNode.modelPosition.toS()} // anchor=${arModelNode.anchor?.pose?.position?.toS()}")
        Log.e("Mnstr", "anchor2--------rot=${arModelNode.worldRotation.toS()} // model=${arModelNode.modelRotation.toS()} // anchor=${arModelNode.anchor?.pose?.rotation?.toS()}")
        Log.e("Mnstr", "---------------------- dist=${hitResult.distance}-- rot=$anchorAngle --------------------")
    }

    private var logTime = 0f
    private fun log(deltaTime: Float, camera: Pose) {
        if(arModelNode.isAnchored) {
            logTime += deltaTime
            if(logTime > 1.0) {
                logTime = 0f

                val angle = anchorAngle
                val worldPosition = arModelNode.worldPosition
                val modelPosition = arModelNode.modelPosition
                val localCameraPosition = getLocalCameraPosition(
                    angle = angle,
                    worldPosition = worldPosition,
                    cameraPosition = camera.position
                )
                val realWorldPosition = getRealWorldPosition(
                    angle = angle,
                    modelPosition = modelPosition,
                    worldPosition = worldPosition
                )
                val distance = distance(
                    realWorldPosition = realWorldPosition,
                    cameraPosition = camera.position
                )
                val localDirection = getLocalDirection(
                    angle = angle,
                    realWorldPosition = realWorldPosition,
                    cameraPosition = camera.position
                )
                val localAngle = getLocalMonsterAngle(
                    angle = angle,
                    realWorldPosition = realWorldPosition,
                    cameraPosition = camera.position
                )
                Log.e("Monsrer", "--- Rot = $angle                  Dist = $distance   -   ${state.name}")
                Log.e("Monsrer", "--- Real Pos=${realWorldPosition.toS()}   Model Pos=${modelPosition.toS()}   World Pos=${worldPosition.toS()}  Anchor Pos=${arModelNode.anchor!!.pose!!.position.toS()}")
                Log.e("Monsrer", "--- Cam Pos = ${camera.position.toS()}")
                Log.e("Monsrer", "---------------------DIR LOC=${localDirection.toS()} // CAM LOC=${localCameraPosition.toS()}---------------------------------------------")
                Log.e("Monster", "--------------- monster angle = $localAngle")
                //position == worldPosition == anchor.pose.position (but anchor changes over time if plane relocates...)
            }
        }
    }

    companion object {
        private const val DistAttack = 0.9f
        private const val DistFollow = 1.0f
        private const val IdleDelay = 3
    }
}

fun Position.toS() = "%.2f, %.2f, %.2f".format(Locale.US, x, y, z)
fun Vector3.toS() = "%.2f, %.2f, %.2f".format(Locale.US, x, y, z)