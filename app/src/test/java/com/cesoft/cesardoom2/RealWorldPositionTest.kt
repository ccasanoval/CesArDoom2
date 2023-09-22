package com.cesoft.cesardoom2

import io.github.sceneview.math.Position
import org.junit.Assert
import org.junit.Test

class RealWorldPositionTest {
    @Test
    fun test_real_world_position_0() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(0f, 0f, -3f)
        val angle = 0f

        val pos = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )

        Assert.assertEquals(Position(0f, 0f, -2f), pos)
    }

    @Test
    fun test_real_world_position_0b() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(0f, 0f, 3f)
        val angle = 0f

        val pos = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )

        Assert.assertEquals(Position(0f, 0f, 4f), pos)
    }

    @Test
    fun test_real_world_position_180() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(0f, 0f, -3f)
        val angle = 180f

        val pos = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )

        Assert.assertEquals(Position(0f, 0f, -4f), pos)
    }

    @Test
    fun test_real_world_position_180b() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(0f, 0f, 3f)
        val angle = 180f

        val pos = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )

        Assert.assertEquals(Position(0f, 0f, 2f), pos)
    }

    @Test
    fun test_real_world_position_90() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(3f, 0f, 0f)
        val angle = 90f

        val rotated = Util3D.rotate(angle = angle, modelPosition)

        val pos = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )

        Assert.assertEquals(Position(2f, 0f, 0f), pos)
    }

    @Test
    fun test_real_world_position_90b() {
        val modelPosition = Position(0.00f, 0.00f, 3.00f)
        val worldPosition = Position(3.00f, -1.00f, 0.00f)
        val angle = 90f

        val pos = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        Assert.assertEquals(Position(0f, -1f, 0f), pos)
    }

}