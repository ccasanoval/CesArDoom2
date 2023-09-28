package com.cesoft.cesardoom2

import android.util.Log
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.cesoft.cesardoom2.Util3D.distance2
import com.cesoft.cesardoom2.Util3D.getLocalDirection
import com.cesoft.cesardoom2.Util3D.getLocalMonsterAngle
import com.cesoft.cesardoom2.Util3D.getRealWorldPosition
import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import com.google.ar.sceneform.math.Vector3
import dev.romainguy.kotlin.math.Float3
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.rotation
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import java.util.Locale

//TODO: Show life level bar...
//TODO: Depth & light
//3D MODEL: https://www.turbosquid.com/es/3d-models/3d-improved-gonome-1901177
class Monster(arSceneView: ArSceneView, private val painInflicted: MutableFloatState) {
    private val arModelNode = ArModelNode(arSceneView.engine, PlacementMode.INSTANT)
    private var state = MonsterAnimation.None
    private var anchorAngle = 0f

    private var walkOrRun = true
    private var idleStart = 0f//TODO: state machine = STATUS
    private var anchorDelay = 0f
    private var dieDelay = 0f
    private var happyDelay = 0f

    private var generation = 0

    private fun init() {
        anchorAngle = 0f
        walkOrRun = !walkOrRun
        idleStart = 0f
        anchorDelay = 0f
        dieDelay = 0f
        happyDelay = 0f
        arModelNode.detachAnchor()
        //arModelNode.anchor = null
        arModelNode.scale = Float3(1f,1f,1f)
        arModelNode.modelPosition = Position()
        //arModelNode.pose = Pose.IDENTITY
        state = MonsterAnimation.None
    }

    fun load(): Monster {
        arModelNode.loadModelGlbAsync(
            glbFileLocation = "gonome.glb",
            autoAnimate = false,
            scaleToUnits = 1f,//1.25f,
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
        //log(deltaTime, camera)
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
                        if(walkOrRun)
                            changeState(MonsterAnimation.Walk)
                        else
                            changeState(MonsterAnimation.Run)
                        SoundFx.play(sound = Sound.Awake, distance2 = distance2)
                    }
                }
                MonsterAnimation.Walk -> {
                    ifMoving(
                        run = false,
                        deltaTime = deltaTime,
                        distance2 = distance2,
                        angle = angle,
                        realWorldPosition = realWorldPosition,
                        cameraPosition = cameraPosition
                    )
                }
                MonsterAnimation.Run -> {
                    ifMoving(
                        run = true,
                        deltaTime = deltaTime,
                        distance2 = distance2,
                        angle = angle,
                        realWorldPosition = realWorldPosition,
                        cameraPosition = cameraPosition
                    )
                }
                MonsterAnimation.Attack -> {
                    ifAttacking(distance2)
                    painInflicted.floatValue += 5*deltaTime
                    if(painInflicted.floatValue > 99f) {
                        changeState(MonsterAnimation.Happy)
                        SoundFx.stop()
                        SoundFx.play(Sound.Laugh)
                    }
                }
                MonsterAnimation.Die -> {
                    dieDelay += deltaTime
                    if(dieDelay > DieDelay) {
                        init()
                    }
                }
                MonsterAnimation.Happy -> {
                    happyDelay += deltaTime
                    if(happyDelay > HappyDelay) {
                        painInflicted.floatValue = 0f
                        init()
                    }
                }
                else -> {}
            }
        }
    }

    private fun ifMoving(
        run: Boolean,
        deltaTime: Float,
        distance2: Float,
        angle: Float,
        realWorldPosition: Position,
        cameraPosition: Position
    ) {
        val dir = getLocalDirection(
            angle = angle,
            realWorldPosition = realWorldPosition,
            cameraPosition = cameraPosition
        )
        val deltaWalkRun = if(run) DeltaRun else DeltaWalk
        val delta = (deltaWalkRun + (generation%3)*.15f) * deltaTime
        move(distance2 = distance2, direction = dir, delta = delta)
    }

    private fun move(distance2: Float, direction: Position, delta: Float) {
        arModelNode.modelPosition += direction * delta
        if(distance2 < DistAttack) {
            changeState(MonsterAnimation.Attack)
            SoundFx.play(sound = Sound.Attack, distance2 = distance2, loop = true)
        }
        if(SoundFx.lastTimePlayed > 2.5f) {
            SoundFx.play(sound = Sound.Hurt, distance2 = distance2)
        }
    }

    private fun ifAttacking(distance2: Float) {
        if(distance2 > DistFollow) {
            changeState(MonsterAnimation.Run)
            SoundFx.stop()
            SoundFx.play(sound = Sound.Hurt, distance2 = distance2)
        }
    }

    private fun changeState(newState: MonsterAnimation, loop: Boolean = true) {
android.util.Log.e("Monster", "changeState---------------- $newState, $loop")
        state = newState
        for(i in 0..(arModelNode.animator?.animationCount ?: 0)) {
            arModelNode.stopAnimation(i)
        }
        arModelNode.playAnimation(state.animation, loop)
    }

    fun shoot(distance: Float) {
        if(state != MonsterAnimation.Die) {
            changeState(MonsterAnimation.Die, false)
            SoundFx.stop(Sound.Attack)
            SoundFx.play(sound = Sound.Hurt, distance2 = distance * distance)
            generation++
            //TODO: Points++
        }
    }

    fun anchor(deltaTime: Float): Boolean {
        if(state == MonsterAnimation.None && deltaTime < .1f) {
            anchorDelay += deltaTime
            if(anchorDelay > AnchorDelay) {
                //android.util.Log.e("Monster", "anchor-----------------DONE $anchorDelay  ${arModelNode.isTracking}")
                SoundFx.stop()
                //arModelNode.detachAnchor()
                arModelNode.anchor()
                anchorAngle = arModelNode.worldRotation.y
                anchorDelay = 0f
                idleStart = 0f
                changeState(MonsterAnimation.Idle)
                return true
            }
        }
        return false
    }

    //TODO: Release -> Remove
    fun anchor(hitResult: HitResult) {
        SoundFx.stop()
        idleStart = 0f
        changeState(MonsterAnimation.Idle)

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
//                Log.e("Monsrer", "--- Cam Pos = ${camera.position.toS()}   Cam Rot = ${camera.rotation.toS()}")
//                Log.e("Monsrer", "---------------------DIR LOC=${localDirection.toS()} // CAM LOC=${localCameraPosition.toS()}---------------------------------------------")
//                Log.e("Monster", "--------------- monster angle = $localAngle")
//                //position == worldPosition == anchor.pose.position (but anchor changes over time if plane relocates...)
//            }
//        }
//    }

    companion object {
        private const val DistAttack = 0.8f
        private const val DistFollow = 1.1f

        private const val IdleDelay = 2
        private const val AnchorDelay = 4
        private const val DieDelay = 4.8f
        private const val HappyDelay = 7f

        private const val DeltaRun = .32f
        private const val DeltaWalk = .25f
    }
}

fun Position.toS() = "%.2f, %.2f, %.2f".format(Locale.US, x, y, z)
fun Vector3.toS() = "%.2f, %.2f, %.2f".format(Locale.US, x, y, z)