package com.cesoft.cesardoom2

import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.math.Position
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

object Util3D {
    private const val DEGREES_TO_RADIANS = 0.017453292519943295
    fun toRadians(value: Float): Double {
        return value * DEGREES_TO_RADIANS
    }
    fun toDegrees(value: Double): Double {
        return value / DEGREES_TO_RADIANS
    }

    fun getLocalMonsterAngle(
        angle: Float,
        realWorldPosition: Position,
        cameraPosition: Position
    ): Float {
        val x = cameraPosition.x - realWorldPosition.x.toDouble()
        val z = cameraPosition.z - realWorldPosition.z.toDouble()
        return toDegrees(atan2(x, z)).toFloat() + angle
    }

    fun getRealWorldPosition(
        angle: Float,
        modelPosition: Position,
        worldPosition: Position
    ): Position {
        val rotated = rotateLocalToReal(angle = angle, modelPosition)
        return Position(
            worldPosition.x + rotated.x,
            worldPosition.y + rotated.y,
            worldPosition.z + rotated.z,
        )
    }

    fun distance(
        realWorldPosition: Position,
        cameraPosition: Position
    ): Float {
        return sqrt(distance2(
            realWorldPosition = realWorldPosition,
            cameraPosition = cameraPosition
        ))
    }
    fun distance2(
        realWorldPosition: Position,
        cameraPosition: Position
    ): Float {
        val x = realWorldPosition.x - cameraPosition.x
        val z = realWorldPosition.z - cameraPosition.z
        return x*x + z*z
    }

    fun getLocalCameraPosition(
        angle: Float,
        worldPosition: Position,
        cameraPosition: Position
    ): Position {
//        val mod3 = Vector3(
//            cameraPosition.x - worldPosition.x,
//            0f,
//            cameraPosition.z - worldPosition.z,
//        )
//        val mod = sqrt(mod3.x*mod3.x + mod3.z*mod3.z.toDouble())
//        val alpha = Math.toRadians(angle.toDouble())
//
//        return Vector3(
//            (mod * sin(alpha)).toFloat(),
//            0f,
//            (mod * cos(alpha)).toFloat(),
//        )
        val dir = Position(
            cameraPosition.x - worldPosition.x,
            0f,
            cameraPosition.z - worldPosition.z
        )
        return rotateRealToLocal(angle, dir)

    }//TODO !!!!!!

    fun getLocalDirection(
        angle: Float,
        realWorldPosition: Position,
        cameraPosition: Position
    ): Position {
        val dir = Vector3(
            cameraPosition.x - realWorldPosition.x,
            0f,
            cameraPosition.z - realWorldPosition.z,
        ).normalized()
        return rotateRealToLocal(angle = angle, position = Position(dir.x, dir.y, dir.z))
    }

    fun rotateRealToLocal(angle: Float, position: Position): Position {
        val a = toRadians(angle)
        val sin = sin(a)
        val cos = cos(a)
        val pos = Position(
            clean(position.x * cos + position.z * sin).toFloat(),
            position.y,
            clean(-position.x * sin + position.z * cos).toFloat(),
        )
        if(pos.x == -0f) pos.x = 0f
        if(pos.z == -0f) pos.z = 0f
        return pos
    }

    // Rotate position from local to real world
    fun rotateLocalToReal(angle: Float, position: Position): Position {
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