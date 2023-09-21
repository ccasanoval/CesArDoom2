package com.cesoft.cesardoom2

import com.google.ar.sceneform.math.Vector3
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

        Assert.assertEquals(Vector3(0f, 0f, -2f), pos)
    }

    @Test
    fun test_real_world_position_90() {
        val modelPosition = Position(0f,0f,1f)
        val worldPosition = Position(3f, 0f, 0f)
        val angle = 90f

        val pos = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )

        Assert.assertEquals(Vector3(4f, 0f, 0f), pos)
    }

    @Test
    fun test_real_world_position_REAL_CASE_1() {
        val modelPosition = Position(0f,0f,-.03f)
        val worldPosition = Position(2.58f, -1.09f, .35f)
        val angle = 92.44208f

        val pos = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )

        Assert.assertEquals(Vector3(2.55003f, -1.09f, 0.351281f), pos)
    }

    @Test
    fun test_real_world_position_REAL_CASE_2() {
//        val cameraPosition = Position(0.12f, -0.02f, 0.13f)
//        val cameraLocalPos = Position(3.35f, 0.00f, 0.49f)

        val modelPosition = Position(0.00f, -0.00f, 3.01f)
        val worldPosition = Position(3.48f, -1.05f, -0.35f)
        val angle = -81.72742f

        /*angle = -89.999f
        val a = Util3D.toRadians(angle)
        assertEquals(angle * PI / 180.0, a, 0.0000001)
        val sin = Util3D.clean(sin(a))
        assertEquals(-1.0, sin, 0.0)
        val cos = Util3D.clean(cos(a))
        assertEquals(0.0, cos, 0.0)
        val rotated2 = Position(
            (modelPosition.x * cos + modelPosition.z * sin).toFloat(),
            modelPosition.y,
            (modelPosition.x * sin + modelPosition.z * cos).toFloat(),
        )
        assertEquals(Position(-3.01f, -0.00f, 0.00f), rotated2)*/

        val rotated = Util3D.rotate(angle = angle, modelPosition)
        Assert.assertEquals(Position(-2.978696f, -0.00f, 0.432838f), rotated)

        val pos = Util3D.getRealWorldPosition(
            angle = angle,
            modelPosition = modelPosition,
            worldPosition = worldPosition
        )
        Assert.assertEquals(Vector3(0.5013039f, -1.05f, 0.082838f), pos)
    }
}