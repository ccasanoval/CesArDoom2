package com.cesoft.cesardoom2

import io.github.sceneview.math.Position
import org.junit.Assert
import org.junit.Test

class LocalDirectionTest {
    @Test
    fun test_local_direction_REAL_1() {
        val modelPosition = Position(0.00f, -0.00f, 3.01f)
        val worldPosition = Position(3.48f, -1.05f, -0.35f)
        val cameraPosition = Position(0.12f, -0.02f, 0.13f)
        val angle = -81.72742f

        val pos = Util3D.getLocalDirection(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(Position(-0.26418662f, 0f, 0.99976766f), pos)
    }

    @Test
    fun test_local_direction_REAL_2() {
        val modelPosition = Position(0.00f, -0.00f, 3.01f)
        val worldPosition = Position(3.48f, -1.05f, -0.35f)
        val cameraPosition = Position(0.12f, -0.02f, 0.13f)
        val angle = -81.72742f

        val pos = Util3D.getLocalDirection(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(Position(-0.26418662f, 0f, 0.99976766f), pos)
    }

    /*@Test
    fun test_local_direction_0() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(0f, 0f, -3f)
        val cameraPosition = Position(0f, 0f, 0f)
        val angle = 0f

        val pos = Util3D.getLocalDirection(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        assertEquals(Position(0f,0f,1f), pos)
    }

    @Test
    fun test_local_direction_90() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(3f, 0f, 0f)
        val cameraPosition = Position(0f, 0f, 0f)
        val angle = 90f

        val pos = Util3D.getLocalDirection(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        assertEquals(Position(0f,0f,1f), pos)
    }

    @Test
    fun test_local_direction_90_A() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(3f, 0f, 0f)
        val cameraPosition = Position(3f, 0f, 3f)
        val angle = 90f

        val pos = Util3D.getLocalDirection(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        assertEquals(Position(1f,0f,0f), pos)
    }

    @Test
    fun test_local_direction_0_A() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(0f, 0f, -4f)
        val cameraPosition = Position(3f, 0f, -3f)
        val angle = 0f

        val pos = Util3D.getLocalDirection(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        assertEquals(Position(1f,0f,0f), pos)
    }

    @Test
    fun test_local_direction_90_B() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(-3f, 0f, 0f)
        val cameraPosition = Position(3f, 0f, -3f)
        val angle = -90f

        val pos = Util3D.getLocalDirection(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        assertEquals(Position(0f,0f,1f), pos)
    }*/
}