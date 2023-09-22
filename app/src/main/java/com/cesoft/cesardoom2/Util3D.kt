package com.cesoft.cesardoom2

import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.math.Position
import io.github.sceneview.math.toVector3
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

object Util3D {
    private const val DEGREES_TO_RADIANS = 0.017453292519943295
    fun toRadians(value: Float): Double {
        //val v = if(value > 360) value - 360 else if(value < -90) value + 360 else value
        return value * DEGREES_TO_RADIANS
    }

    fun getRealWorldPosition(
        angle: Float,
        modelPosition: Position,
        worldPosition: Position
    ): Position {
        val rotated = rotateLocal(angle = angle, modelPosition)
        return Position(
            worldPosition.x + rotated.x,
            worldPosition.y + rotated.y,
            worldPosition.z + rotated.z,
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
        val modRot = getRealWorldPosition(angle, modelPosition, worldPosition)
        val x = modRot.x - cameraPosition.x
        val z = modRot.z - cameraPosition.z
        return x*x + z*z
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

    // Rotate position from real world to local
    fun rotate(angle: Float, position: Position): Position {
        val a = toRadians(angle)
        val sin = sin(a)
        val cos = cos(a)
        val pos = Position(
            clean(position.x * cos + position.z * sin).toFloat(),//TODO: Remove clean here, do it in the last Util3D called func
            position.y,
            clean(-position.x * sin + position.z * cos).toFloat(),
        )
        if(pos.x == -0f) pos.x = 0f
        if(pos.z == -0f) pos.z = 0f
        return pos
    }

    // Rotate position from local to real world
    fun rotateLocal(angle: Float, position: Position): Position {
        val a = toRadians(angle)
        val sin = sin(a)
        val cos = cos(a)
        val pos = Position(
            clean(position.x * cos - position.z * sin).toFloat(),
            position.y,
            clean(position.x * sin + position.z * cos).toFloat(),
        )
        if(pos.x == -0f) pos.x = 0f
        if(pos.z == -0f) pos.z = 0f
        return pos
    }

    fun clean(value: Double): Double {
        return if(abs(value) < 0.000_001) 0.0
        else floor(100_000*value)/100_000
    }
}