package com.cesoft.cesardoom2

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.cesoft.cesardoom2.Util3D.distance2
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

//TODO: Button to close / button to mute?
//TODO: Change color of plane?

//TODO: Way to shoot the monster and kill it // After this, create another one
//TODO: Show life level bar...
//TODO: Depth & light
//3D MODEL: https://www.turbosquid.com/es/3d-models/3d-improved-gonome-1901177
class Monster(arSceneView: ArSceneView) {
    private val arModelNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT)
    private var state = MonsterAnimation.Idle
    private var anchorAngle = 0f

    private var idleStart = 0f//TODO: state machine + time
    private var anchorDelay = 0f

    fun load(): Monster {
        arModelNode.loadModelGlbAsync(
            glbFileLocation = "gonome.glb",
            autoAnimate = false,
            scaleToUnits = 1.20f,
            //centerOrigin = Position(x = 0f, y = 0f, z = 0f) ,
            onError = { exception ->
                Log.e("ArScreen", "Load Model-------------------e: $exception")
            },
            onLoaded = { modelInstance ->
                Log.e("ArScreen", "Load Model-------------------ok: ${modelInstance.root}")
            }
        )
        return this
    }

    fun show(nodes: SnapshotStateList<ArNode>): Monster {
        nodes.add(arModelNode)
        return this
    }

    fun update(deltaTime: Float, camera: Pose) {
//        log(deltaTime, camera)
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

            val distance2 = distance2(
                realWorldPosition = realWorldPosition,
                cameraPosition = cameraPosition
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
                        SoundFx.play(sound = Sound.Awake, distance2 = distance2)
                    }
                }
                MonsterAnimation.Walk -> {
                    val dir = getLocalDirection(
                        angle = angle,
                        realWorldPosition = realWorldPosition,
                        cameraPosition = cameraPosition
                    )
                    val delta = DeltaWalk * deltaTime
                    doWalk(distance2 = distance2, direction = dir, delta = delta)
                }
                MonsterAnimation.Run -> {
                    val dir = getLocalDirection(
                        angle = angle,
                        realWorldPosition = realWorldPosition,
                        cameraPosition = cameraPosition
                    )
                    val delta = DeltaRun * deltaTime
                    doWalk(distance2 = distance2, direction = dir, delta = delta)
                }
                MonsterAnimation.Attack -> {
                    //TODO: Player health -= delta
                    doAttack(distance2)
                }
                else -> {}
            }
        }
    }

    private fun doWalk(distance2: Float, direction: Position, delta: Float) {
        arModelNode.modelPosition += direction * delta
        if(distance2 < DistAttack) {
            changeState(MonsterAnimation.Attack)
            SoundFx.play(sound = Sound.Attack, distance2 = distance2, loop = true)
        }
        if(SoundFx.lastTimePlayed > 2.5f) {
            SoundFx.play(sound = Sound.Hurt, distance2 = distance2)
        }
    }

    private fun doAttack(distance2: Float) {
        if(distance2 > DistFollow) {
            changeState(MonsterAnimation.Run)
            SoundFx.stop()
            SoundFx.play(sound = Sound.Hurt, distance2 = distance2)
        }
    }

    private fun changeState(newState: MonsterAnimation) {
        state = newState
        for(i in 0..(arModelNode.animator?.animationCount ?: 0)) {
            arModelNode.stopAnimation(i)
        }
        arModelNode.playAnimation(state.animation, true)
    }

    fun anchor(deltaTime: Float): Boolean {
        if( ! arModelNode.isAnchored && deltaTime < 1000) {
            anchorDelay += deltaTime
            if(anchorDelay > AnchorDelay) {
                anchorDelay = 0f
                SoundFx.stop()
                idleStart = 0f
                changeState(MonsterAnimation.Idle)

                arModelNode.detachAnchor()
                anchorAngle = arModelNode.worldRotation.y
                arModelNode.anchor()
                return true
            }
        }
        return false
    }

    fun anchor(hitResult: HitResult) {
        SoundFx.stop()
        idleStart = 0f
        changeState(MonsterAnimation.Idle)

        //arModelNode.centerModel(Position(0f,-1f,0f))
        arModelNode.rotation = hitResult.hitPose.rotation

        arModelNode.detachAnchor()
        arModelNode.anchor = hitResult.createAnchor()
        anchorAngle = arModelNode.worldRotation.y
//        Log.e("Mnstr", "anchor1--------pos=${arModelNode.worldPosition.toS()} // model=${arModelNode.modelPosition.toS()} // anchor=${arModelNode.anchor?.pose?.position?.toS()}")
//        Log.e("Mnstr", "anchor2--------rot=${arModelNode.worldRotation.toS()} // model=${arModelNode.modelRotation.toS()} // anchor=${arModelNode.anchor?.pose?.rotation?.toS()}")
//        Log.e("Mnstr", "---------------------- dist=${hitResult.distance}-- rot=$anchorAngle --------------------")
    }

//    private var logTime = 0f
//    private fun log(deltaTime: Float, camera: Pose) {
//        if(arModelNode.isAnchored) {
//            logTime += deltaTime
//            if(logTime > 1.0) {
//                logTime = 0f
//
//                val angle = anchorAngle
//                val worldPosition = arModelNode.worldPosition
//                val modelPosition = arModelNode.modelPosition
//                val localCameraPosition = getLocalCameraPosition(
//                    angle = angle,
//                    worldPosition = worldPosition,
//                    cameraPosition = camera.position
//                )
//                val realWorldPosition = getRealWorldPosition(
//                    angle = angle,
//                    modelPosition = modelPosition,
//                    worldPosition = worldPosition
//                )
//                val distance = distance(
//                    realWorldPosition = realWorldPosition,
//                    cameraPosition = camera.position
//                )
//                val localDirection = getLocalDirection(
//                    angle = angle,
//                    realWorldPosition = realWorldPosition,
//                    cameraPosition = camera.position
//                )
//                val localAngle = getLocalMonsterAngle(
//                    angle = angle,
//                    realWorldPosition = realWorldPosition,
//                    cameraPosition = camera.position
//                )
//                Log.e("Monsrer", "--- Rot = $angle                  Dist = $distance   -   ${state.name}")
//                Log.e("Monsrer", "--- Real Pos=${realWorldPosition.toS()}   Model Pos=${modelPosition.toS()}   World Pos=${worldPosition.toS()}  Anchor Pos=${arModelNode.anchor!!.pose!!.position.toS()}")
//                Log.e("Monsrer", "--- Cam Pos = ${camera.position.toS()}")
//                Log.e("Monsrer", "---------------------DIR LOC=${localDirection.toS()} // CAM LOC=${localCameraPosition.toS()}---------------------------------------------")
//                Log.e("Monster", "--------------- monster angle = $localAngle")
//                //position == worldPosition == anchor.pose.position (but anchor changes over time if plane relocates...)
//            }
//        }
//    }

    companion object {
        private const val DistAttack = 0.8f
        private const val DistFollow = 1.1f
        private const val IdleDelay = 3
        private const val AnchorDelay = 5
        private const val DeltaRun = .30f
        private const val DeltaWalk = .25f
    }
}

fun Position.toS() = "%.2f, %.2f, %.2f".format(Locale.US, x, y, z)
fun Vector3.toS() = "%.2f, %.2f, %.2f".format(Locale.US, x, y, z)