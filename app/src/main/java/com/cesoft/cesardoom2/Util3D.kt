package com.cesoft.cesardoom2

import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.math.Position
import io.github.sceneview.math.toVector3
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

object Util3D {
    private const val DEGREES_TO_RADIANS = 0.017453292519943295
    fun toRadians(value: Float): Double {
        val v = if(value > 360) value - 360 else if(value < -90) value + 360 else value
        return v * DEGREES_TO_RADIANS
    }

    fun getRealWorldPosition(
        angle: Float,
        modelPosition: Position,
        worldPosition: Position
    ): Vector3 {
        val rotated = rotate(angle = angle, modelPosition)
        return Vector3(
            rotated.x + worldPosition.x,// arModelNode.anchor!!.pose!!.position.x,
            rotated.y + worldPosition.y,
            rotated.z + worldPosition.z,//arModelNode.anchor!!.pose!!.position.z,
        )
    }

    fun distance(
        angle: Float,
        modelPosition: Position,
        worldPosition: Position,
        cameraPosition: Position
    ): Float {
        return sqrt(distance2(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        ))
    }
    fun distance2(
        angle: Float,
        modelPosition: Position,
        worldPosition: Position,
        cameraPosition: Position
    ): Float {
        //return camera.distanceTo(arModelNode.pose!!)//W t shit is this?
        val modRot = getRealWorldPosition(angle, modelPosition, worldPosition)
        val distance = Vector3.subtract(modRot, cameraPosition.toVector3())
        return distance.x*distance.x + distance.z*distance.z
    }

    fun getLocalCameraPosition(
        angle: Float,
        worldPosition: Position,
        cameraPosition: Position
    ): Vector3 {
        val mod3 = Vector3(
            cameraPosition.x - worldPosition.x,
            0f,
            cameraPosition.z - worldPosition.z,
        )
        val mod = sqrt(mod3.x*mod3.x + mod3.z*mod3.z.toDouble())
        //val rotY = angle//worldRotation.y
        val alpha = Math.toRadians(angle.toDouble())

        return Vector3(
            (mod * sin(alpha)).toFloat(),
            0f,
            (mod * cos(alpha)).toFloat(),
        )
    }

    fun getLocalDirection(
        angle: Float,
        modelPosition: Position,
        worldPosition: Position,
        cameraPosition: Position
    ): Position {
        val realWorldPos = getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        val dir = Vector3(
            cameraPosition.x - realWorldPos.x,
            0f,
            cameraPosition.z - realWorldPos.z,
        ).normalized()
        return rotate(angle = angle, position = Position(dir.x, dir.y, dir.z))
    }

    fun rotate(angle: Float, position: Position): Position {
        val a = toRadians(angle)
        val sin = clean(sin(a))
        val cos = clean(cos(a))
        return Position(
            (position.x * cos + position.z * sin).toFloat(),
            position.y,
            (position.x * sin + position.z * cos).toFloat(),
        )
    }

    fun clean(value: Double): Double {
        return floor(10_000*value)/10_000
    }
}