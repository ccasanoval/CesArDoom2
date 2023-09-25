package com.cesoft.cesardoom2

import io.github.sceneview.math.Position
import org.junit.Assert
import org.junit.Test
import kotlin.math.atan
import kotlin.math.atan2

class LocalMonsterAngleTest {

    @Test
    fun test_local_monster_angle_0() {
        val modelPosition = Position(0f, 0f, 0f)
        val worldPosition = Position(0f, 0f, -5f)
        val cameraPosition = Position(0f, 0f, 0f)
        val angle = 0f

        val ret = Util3D.getLocalMonsterAngle(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(0f, ret)
    }

    @Test
    fun test_local_monster_angle_90() {
        val modelPosition = Position(0f, 0f, 0f)
        val worldPosition = Position(0f, 0f, -5f)
        val cameraPosition = Position(10f, 0f, -5f)
        val angle = 0f

        val ret = Util3D.getLocalMonsterAngle(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(90f, ret)
    }

    @Test
    fun test_local_monster_angle__90() {
        val modelPosition = Position(0f, 0f, 0f)
        val worldPosition = Position(0f, 0f, -5f)
        val cameraPosition = Position(-10f, 0f, -5f)
        val angle = 0f

        val ret = Util3D.getLocalMonsterAngle(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(-90f, ret)
    }

    @Test
    fun test_local_monster_angle_180() {
        val modelPosition = Position(0f, 0f, 0f)
        val worldPosition = Position(0f, 0f, -5f)
        val cameraPosition = Position(0f, 0f, -15f)
        val angle = 0f

        val ret = Util3D.getLocalMonsterAngle(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(180f, ret)
    }

    @Test
    fun test_local_monster_angle_180b() {
        val modelPosition = Position(0f, 0f, 0f)
        val worldPosition = Position(0f, 0f, -5f)
        val cameraPosition = Position(0f, 0f, -15f)
        val angle = 0f

        val ret = Util3D.getLocalMonsterAngle(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition,
            cameraPosition = cameraPosition
        )

        Assert.assertEquals(180f, ret)
    }
}