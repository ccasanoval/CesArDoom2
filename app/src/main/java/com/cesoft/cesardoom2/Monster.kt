package com.cesoft.cesardoom2

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import io.github.sceneview.math.toVector3
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

//TODO: Make the distance calc right !!!!!!!!!

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
    private var idleStart = 0f//TODO: state machine + time

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
                    zOffset = .25f * deltaTime
                    val position = arModelNode.modelPosition + Position(0f, 0f, zOffset)
                    //arModelNode.centerModel(position)
                    arModelNode.modelPosition = position

                    val dist2 = distance2(camera)
                    //Log.e("Mnstr", "run-----------dist2 = $dist2")
                    if(dist2 < DistAttack) {
                        changeState(MonsterAnimation.Attack)
                    }
                }
                MonsterAnimation.Attack -> {
                    //.. check distance again
                    val dist2 = distance2(camera)
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

    private fun direction(cameraPosition: Position): Vector3 {
        return if(arModelNode.isAnchored) {
            val x = arModelNode.position.x - cameraPosition.x
            val y = 0f//arModelNode.position.y + arModelNode.anchor!!.pose!!.position.y - cameraPosition.y
            val z = arModelNode.position.z - cameraPosition.z
            Vector3(x, y, z).normalized()
        } else Vector3()
    }

    private fun toCamera(): Vector3 {
        var rotY = arModelNode.worldRotation.y
        if(rotY < 0) rotY += 360
        val alpha =  - toRadians(rotY.toDouble()).toFloat()
        val mod3 = Vector3(
            arModelNode.modelPosition.x,// + arModelNode.anchor!!.pose!!.position.x,
            0f,
            arModelNode.modelPosition.z,// + arModelNode.anchor!!.pose!!.position.z,
        )
        val mod = sqrt(mod3.x*mod3.x + mod3.z*mod3.z)

        return Vector3(
            mod * sin(alpha) + arModelNode.anchor!!.pose!!.position.x,
            0f,
            mod * cos(alpha) + arModelNode.anchor!!.pose!!.position.z,
        )
    }

    private fun distance2(camera: Pose): Float {
        //return camera.distanceTo(arModelNode.pose!!)//W t shit is this?
        val modRot = toCamera()
        val distance = Vector3.subtract(modRot, camera.position.toVector3())
        return distance.x*distance.x + distance.z*distance.z
    }

    private var logTime = 0f
    private fun log(deltaTime: Float, camera: Pose) {
        if(arModelNode.isAnchored) {
            logTime += deltaTime
            if(logTime > 1.0) {
                logTime = 0f
                //Log.e("Monster", "-------------------------------ROT=${camera.rotation.toS()}---POS=${camera.position.toS()}")
                //Log.e("Monster", "MOD-------POS=${(arModelNode.anchor!!.pose.position +  arModelNode.modelPosition).toS()}  /// DIST="+"%.2f".format(distance2(camera))+" .... "+state.name)
                //Log.e("Monster", "MOD-------POS=${arModelNode.worldPosition.toS()}")
                Log.e("Monsrer", "--- Rot = ${arModelNode.worldRotation.y}               Dist = ${distance2(camera)}   -   ${state.animation}")
                Log.e("Monsrer", "--- Pos = ${toCamera().toS()} ..  POS1=${arModelNode.modelPosition.toS()}   POS3=${arModelNode.worldPosition.toS()}  POS4=${arModelNode.anchor!!.pose!!.position.toS()}")
                Log.e("Monsrer", "--- Cam = ${camera.position.toS()} ")
                Log.e("Monsrer", "---------------------------------------------------------------------")
                //position == worldPosition == anchor.pose.position (but anchor changes over time if plane relocates...)
            }
        }
    }

    fun anchor(hitResult: HitResult) {
        zOffset = 0f
        idleStart = 0f
        changeState(MonsterAnimation.Idle)

        arModelNode.centerModel(Position(0f,-1f,0f))
        arModelNode.rotation = hitResult.hitPose.rotation

        arModelNode.detachAnchor()
        arModelNode.anchor = hitResult.createAnchor()
        Log.e("Mnstr", "anchor1--------pos=${arModelNode.worldPosition.toS()} // model=${arModelNode.modelPosition.toS()} // anchor=${arModelNode.anchor?.pose?.position?.toS()}")
        Log.e("Mnstr", "anchor2--------rot=${arModelNode.worldRotation.toS()} // model=${arModelNode.modelRotation.toS()} // anchor=${arModelNode.anchor?.pose?.rotation?.toS()}")
        Log.e("Mnstr", "---------------------- ${hitResult.distance}----------------------")
    }

    companion object {
        private const val DistAttack = 0.9f
        private const val DistFollow = 1.0f
        private const val IdleDelay = 3
    }
}

fun Position.toS() = "%.2f, %.2f, %.2f".format(x, y, z)
fun Vector3.toS() = "%.2f, %.2f, %.2f".format(x, y, z)