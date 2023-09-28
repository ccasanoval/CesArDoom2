package com.cesoft.cesardoom2

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cesoft.cesardoom2.ui.theme.CesArDoom2Theme
import com.google.ar.core.Config.LightEstimationMode
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArNode


//ArCore: https://developers.google.com/ar/develop/java/lighting-estimation/developer-guide?hl=en
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArScreen() {
    val activity = (LocalContext.current as? Activity)
    val configuration = LocalConfiguration.current
    val screenCY = configuration.screenHeightDp.dp
    val screenCX = configuration.screenWidthDp.dp
    val exitSize = screenCY*.06f
    val crosshairSize = exitSize*3

    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }

    val pain = remember { mutableFloatStateOf(0f) }
    val shot = remember { mutableStateOf(false) }

    val context = LocalContext.current
    DisposableEffect(true) {
        SoundFx.init(context)
        onDispose {
            SoundFx.release()
        }
    }

    Scaffold(
        topBar = {},
        floatingActionButton = {
            Column(modifier = Modifier.fillMaxWidth()) {

                // Exit button
                var x = screenCX - exitSize*3/2
                var y = -screenCY/2 + exitSize*2
                Icon(
                    painter = painterResource(R.drawable.exit),
                    contentDescription = "Salir",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(exitSize)
                        .offset(x = x, y = y)
                        .clickable {
                            activity?.finish()
                        }
                )

                // Health text
                x = 5.dp
                y -= exitSize
                val health = 100 - pain.floatValue
                val txt = if(health > 50) "❤ %.0f %%"
                else if(health > 15) "❤️\u200D\uD83E\uDE79 %.0f %%"
                else "\uD83D\uDC94 %.0f %%"
                Text(
                    text = txt.format(100 - pain.floatValue),
                    color = Color.Red,
                    style = TextStyle.Default.copy(
                        fontSize = 28.sp,
                        drawStyle = Stroke(
                            miter = 1f,
                            width = 5f,
                            join = StrokeJoin.Round
                        )
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .height(exitSize)
                        .offset(x = x, y = y)
                )

                // Game over
                if(health < 1) {
                    x = 0.dp//(screenCX - crosshairSize) / 2f
                    y = -screenCY / 2 + crosshairSize * 2
                    Icon(
                        painter = painterResource(R.drawable.gameover),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .width(350.dp)
                            .offset(x = x, y = y)
                    )
                }
                else {
                    // Crosshair
                    x = (screenCX - crosshairSize) / 2f
                    y = -screenCY / 2 + crosshairSize * 2
                    Icon(
                        painter = painterResource(R.drawable.crosshair),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(crosshairSize)
                            .offset(x = x, y = y)
                    )
                }

                // Rifle
                Icon(
                    painter = painterResource(R.drawable.rifle),
                    contentDescription = "Disparar",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .padding(0.dp)
                        .width(screenCX-50.dp)
                        .offset(x = 70.dp, y = 16.dp)
                        .clickable {
                            shot.value = true
                        }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ArScene(
                pain = pain,
                shot = shot,
                screenWidthPx = screenWidthPx,
                screenHeightPx = screenHeightPx
            )
        }
    }
}

@Composable
fun ArScene(
    pain: MutableFloatState,
    shot: MutableState<Boolean>,
    screenWidthPx: Int,
    screenHeightPx: Int
) {
    val monster = remember { mutableStateOf<Monster?>(null) }
    val nodes = remember { mutableStateListOf<ArNode>() }
    ARScene(
        modifier = Modifier.fillMaxSize(),
        nodes = nodes,
        planeRenderer = true,//false,//TODO: Release => false
        onCreate = { arSceneView ->
            //arSceneView.lightEstimationMode = LightEstimationMode.ENVIRONMENTAL_HDR
            arSceneView.lightEstimationMode = LightEstimationMode.AMBIENT_INTENSITY
            //arSceneView.planeRenderer.isShadowReceiver = true
            //arSceneView.isDepthOcclusionEnabled = true
            arSceneView.instantPlacementEnabled = true
            monster.value = Monster(arSceneView, pain).load().show(nodes)
        },
        onSessionCreate = { session ->
//            if(session.isDepthModeSupported(Config.DepthMode.AUTOMATIC))
//                session.depthMode = Config.DepthMode.AUTOMATIC
        },
        onFrame = { arFrame ->
            val deltaTime = arFrame.time.intervalSeconds.toFloat()

            if(shot.value && pain.floatValue < 98) {
                SoundFx.play(Sound.Gun)
                val hits = arFrame.frame.hitTest(screenWidthPx / 2f, screenHeightPx / 2f)
                for(hit in hits) {
                    //android.util.Log.e("ArScene", "ArScene------------------ HIT= ${hit.trackable.javaClass}")
                    if(hit.trackable is com.google.ar.core.Point) {
                        //android.util.Log.e("ArScene", "ArScene-------------------hit = ${hit.distance} +++ ${hit.trackable.javaClass} +++ ${hit.isTracking}")
                        monster.value?.shoot(hit.distance)
                    }
                }
            }
            shot.value = false

            monster.value?.anchor(deltaTime) //== true) arSession?.instantPlacementEnabled = false

            monster.value?.update(
                deltaTime = deltaTime,
                camera = arFrame.camera.pose,
            )
        },
        onTap = { hitResult ->
            //monster.value?.anchor(hitResult)
        }
    )
}

@Preview
@Composable
fun ArScreen_Preview() {
    CesArDoom2Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ArScreen()
        }
    }
}