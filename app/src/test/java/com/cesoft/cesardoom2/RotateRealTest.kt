package com.cesoft.cesardoom2

import io.github.sceneview.math.Position
import org.junit.Assert
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

class RotateRealTest {
    @Test
    fun test_rotate_0() {
        val angle = 0f
        val pos = Position(0f,0f,1f)

        //cos(Math.toRadians(90.0)) //expected:<0> but was:<6.123233995736766E-17>
        Assert.assertEquals(0.0, Util3D.clean(cos(Math.toRadians(90.0))), 0.0)

        Assert.assertEquals(pos, Util3D.rotateRealToLocal(angle, pos))
    }

    @Test
    fun test_rotate_90() {
        val angle = 90f
        val pos = Position(1f,0f,0f)
        val res = Position(0f, 0f, -1f)
         Assert.assertEquals(res, Util3D.rotateRealToLocal(angle, pos))
    }

    @Test
    fun test_rotate_90_B() {
        val angle = 90f
        val pos = Position(-1f,0f,0f)
        val res = Position(0f, 0f, 1f)
        Assert.assertEquals(res, Util3D.rotateRealToLocal(angle, pos))
    }

    @Test
    fun test_rotate__90() {
        val angle = -90f
        val pos = Position(0f,0f,1f)
        val res = Position(-1f, 0f, 0f)
        Assert.assertEquals(res, Util3D.rotateRealToLocal(angle, pos))
    }

    @Test
    fun test_rotate__90_B() {
        val angle = -90f
        val pos = Position(-1f,0f,0f)
        val res = Position(0f, 0f, -1f)
        Assert.assertEquals(res, Util3D.rotateRealToLocal(angle, pos))
    }

    @Test
    fun test_rotate_180() {
        val angle = 180f
        val pos = Position(0f,0f,1f)
        val res = Position(0f, 0f, -1f)
        Assert.assertEquals(res, Util3D.rotateRealToLocal(angle, pos))
    }

    @Test
    fun test_rotate_30() {
        val angle = 30f
        val pos = Position(0f,0f,1f)
        val res = Position(
            Util3D.clean(sin(Util3D.toRadians(angle))).toFloat(),
            0f,
            Util3D.clean(cos(Util3D.toRadians(angle))).toFloat()
        )
        Assert.assertEquals(res, Util3D.rotateRealToLocal(angle, pos))
    }

    @Test
    fun test_rotate_60() {
        val angle = 60f
        val pos = Position(0f,0f,1f)
        val res = Position(
            Util3D.clean(sin(Util3D.toRadians(angle))).toFloat(),
            0f,
            Util3D.clean(cos(Util3D.toRadians(angle))).toFloat()
        )
        Assert.assertEquals(res, Util3D.rotateRealToLocal(angle, pos))
    }
}