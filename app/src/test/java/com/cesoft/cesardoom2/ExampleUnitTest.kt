package com.cesoft.cesardoom2

import com.google.ar.sceneform.math.Vector3
import io.github.sceneview.math.Position
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test1() {
        val cameraPos = Position(0f,0f,0f)
        val anchorPos = Position(0f, 0f, -3f)
        val angle = 0f

        val dir = Monster.getLocalCameraPosition(angle, anchorPos, cameraPos)

        assertEquals(Vector3(0f,0f,1f), dir)
    }


}