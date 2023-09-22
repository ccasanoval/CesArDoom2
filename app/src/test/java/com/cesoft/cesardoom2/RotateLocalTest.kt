package com.cesoft.cesardoom2

import io.github.sceneview.math.Position
import org.junit.Assert
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

class RotateLocalTest {
    @Test
    fun test_rotate_loc_0() {
        val angle = 0f
        val pos = Position(0f,0f,1f)
        Assert.assertEquals(pos, Util3D.rotateLocal(angle, pos))
    }

    @Test
    fun test_rotate_loc_90() {
        val angle = 90f
        val pos = Position(0f,0f,1f)
        val res = Position(-1f, 0f, 0f)
         Assert.assertEquals(res, Util3D.rotateLocal(angle, pos))
    }

    @Test
    fun test_rotate__90() {
        val angle = -90f
        val pos = Position(0f,0f,1f)
        val res = Position(1f, 0f, 0f)
        Assert.assertEquals(res, Util3D.rotateLocal(angle, pos))
    }

    @Test
    fun test_rotate_loc_180() {
        val angle = 180f
        val pos = Position(0f,0f,1f)
        val res = Position(0f, 0f, -1f)
        Assert.assertEquals(res, Util3D.rotateLocal(angle, pos))
    }

    @Test
    fun test_rotate_loc_30() {
        val angle = 30f
        val pos = Position(0f,0f,1f)
        val res = Position(
            -Util3D.clean(sin(Util3D.toRadians(angle))).toFloat(),
            0f,
            Util3D.clean(cos(Util3D.toRadians(angle))).toFloat()
        )
        Assert.assertEquals(res, Util3D.rotateLocal(angle, pos))
    }

    @Test
    fun test_rotate_loc_60() {
        val angle = 60f
        val pos = Position(0f,0f,1f)
        val res = Position(
            -Util3D.clean(sin(Util3D.toRadians(angle))).toFloat(),
            0f,
            Util3D.clean(cos(Util3D.toRadians(angle))).toFloat()
        )
        Assert.assertEquals(res, Util3D.rotateLocal(angle, pos))
    }
}