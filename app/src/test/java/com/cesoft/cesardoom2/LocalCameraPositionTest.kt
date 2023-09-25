package com.cesoft.cesardoom2

import io.github.sceneview.math.Position
import org.junit.Assert
import org.junit.Test

class LocalCameraPositionTest {
    @Test
    fun test_local_camera_0() {
        val cameraPos = Position(0f,0f,0f)
        val anchorPos = Position(0f, 0f, -3f)
        val angle = 0f

        val dir = Util3D.getLocalCameraPosition(angle, anchorPos, cameraPos)

        Assert.assertEquals(Position(0f, 0f, 3f), dir)
    }

    @Test
    fun test_local_camera_90() {
        val cameraPos = Position(0f,0f,0f)
        val anchorPos = Position(0f, 0f, -3f)
        val angle = 90f

        val dir = Util3D.getLocalCameraPosition(angle, anchorPos, cameraPos)

        Assert.assertEquals(Position(3f, 0f, 0f), dir)
    }

    @Test
    fun test_local_camera_270() {
        val cameraPos = Position(0f,0f,0f)
        val anchorPos = Position(0f, 0f, -3f)
        val angle = 90f

        val dir = Util3D.getLocalCameraPosition(angle, anchorPos, cameraPos)

        Assert.assertEquals(Position(3f, 0f, 0f), dir)
    }

    @Test
    fun test_local_camera_180() {
        val cameraPos = Position(0f,0f,0f)
        val anchorPos = Position(0f, 0f, -3f)
        val angle = 180f

        val dir = Util3D.getLocalCameraPosition(angle, anchorPos, cameraPos)

        Assert.assertEquals(Position(0f, 0f, -3f), dir)
    }
}