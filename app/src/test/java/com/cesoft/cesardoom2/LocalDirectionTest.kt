package com.cesoft.cesardoom2

import io.github.sceneview.math.Position
import org.junit.Assert
import org.junit.Test
import kotlin.math.floor

class LocalDirectionTest {

    @Test
    fun test_local_direction_90_A() {
        val modelPosition = Position(0.00f, 0.00f, 3.00f)
        val worldPosition = Position(3.00f, -1.00f, 0.00f)
        val cameraPosition = Position(0.00f, 0.00f, 10.00f)
        val angle = 90f
        val realWorldPosition = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        val pos = Util3D.getLocalDirection(
            angle = angle,
            realWorldPosition = realWorldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(Position(1.0f, 0f, 0.0f), pos)
    }

    @Test
    fun test_local_direction_90_B() {
        val modelPosition = Position(0.00f, -0.00f, 3.00f)
        val worldPosition = Position(3.0f, -1.00f, 0.00f)
        val cameraPosition = Position(0.0f, 0.00f, -10.0f)
        val angle = 90f
        val realWorldPosition = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        val pos = Util3D.getLocalDirection(
            angle = angle,
            realWorldPosition = realWorldPosition,
            cameraPosition = cameraPosition
        )
        Assert.assertEquals(Position(-1.0f, 0f, 0.0f), pos)//-0?
    }

    @Test
    fun test_local_direction_90_C() {
        val modelPosition = Position(0.00f, -0.00f, 3.00f)
        val worldPosition = Position(3.0f, -1.00f, 0.00f)
        val cameraPosition = Position(-10.0f, 0.00f, 0.0f)
        val angle = 90f
        val realWorldPosition = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        val pos = Util3D.getLocalDirection(
            angle = angle,
            realWorldPosition = realWorldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(Position(0f, 0f, 1.0f), pos)
    }

    @Test
    fun test_local_direction_A4() {
        val modelPosition = Position(0.00f, -0.00f, 3.00f)
        val worldPosition = Position(3.0f, -1.00f, 0.00f)
        val cameraPosition = Position(10.0f, 0.00f, 0.0f)
        val angle = 90f
        val realWorldPosition = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        val pos = Util3D.getLocalDirection(
            angle = angle,
            realWorldPosition = realWorldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(Position(0f, 0f, -1f), pos)//-0?
    }

    @Test
    fun test_local_direction_A5() {
        val modelPosition = Position(9.00f, 0.00f, 2.00f)
        val worldPosition = Position(3.0f, -1.00f, 0.00f)
        val cameraPosition = Position(1.0f, 0.00f, 4.0f)
        val angle = 90f
        val realWorldPosition = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        val pos = Util3D.getLocalDirection(
            angle = angle,
            realWorldPosition = realWorldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(Position(-1f, 0f, 0f), pos)//-0?
    }

    @Test
    fun test_local_direction_A6() {
        val modelPosition = Position(0.0f, 0.0f, 0.0f)
        val worldPosition = Position(0.0f, 0.0f, 3.0f)
        val cameraPosition = Position(0.0f, 0.0f, 0.0f)
        val angle = 180f
        val realWorldPosition = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        val pos = Util3D.getLocalDirection(
            angle = angle,
            realWorldPosition = realWorldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(Position(0f, 0f, 1f), pos)//-0?
    }

    @Test
    fun test_local_direction_A7() {
        val modelPosition = Position(0.0f, -0.0f, -0.03f)
        val worldPosition = Position(3.54f, -1.09f, 0.61f)
        val cameraPosition = Position(0.27f, 0.11f, 0.22f)
        val angle = 96.753876f
        val realWorldPosition = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        val pos = Util3D.getLocalDirection(
            angle = angle,
            realWorldPosition = realWorldPosition,
            cameraPosition = cameraPosition
        )
        val pos2 = Position(
            floor(pos.x.toDouble()).toFloat(),
            floor(pos.y.toDouble()).toFloat(),
            floor(pos.z.toDouble()).toFloat(),
        )

        Assert.assertEquals(Position(-1f, 0f, 0f), pos2)//-0?
    }
}