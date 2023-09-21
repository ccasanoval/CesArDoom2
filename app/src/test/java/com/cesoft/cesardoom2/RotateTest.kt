package com.cesoft.cesardoom2

import io.github.sceneview.math.Position
import org.junit.Assert
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

class RotateTest {
    @Test
    fun test_rotate_0() {
        val pos = Position(0f,0f,1f)

        //cos(Math.toRadians(90.0)) //expected:<0> but was:<6.123233995736766E-17>
        Assert.assertEquals(0.0, Util3D.clean(cos(Math.toRadians(90.0))), 0.0)

        var res = Util3D.rotate(0f, pos)
        Assert.assertEquals(pos, res)

        res = Util3D.rotate(90f, pos)
        Assert.assertEquals(Position(1f, 0f, 0f), res)

        res = Util3D.rotate(-90f, pos)
        Assert.assertEquals(Position(-1f, 0f, 0f), res)

        val a = Util3D.toRadians(180.0f)
        val sin = Util3D.clean(sin(a))
        val cos = Util3D.clean(cos(a))
        Assert.assertEquals(0.0, sin, 0.0)
        Assert.assertEquals(-1.0, cos, 0.0)

        res = Util3D.rotate(180f, pos)
        Assert.assertEquals(Position(0f, 0f, -1f), res)
    }
}